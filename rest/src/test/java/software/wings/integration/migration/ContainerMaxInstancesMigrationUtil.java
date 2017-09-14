package software.wings.integration.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static software.wings.beans.InstanceUnitType.COUNT;
import static software.wings.beans.InstanceUnitType.PERCENTAGE;

import com.google.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import software.wings.WingsBaseTest;
import software.wings.dl.WingsPersistence;
import software.wings.rules.Integration;
import software.wings.sm.State;
import software.wings.sm.StateMachine;
import software.wings.sm.StateType;
import software.wings.sm.states.EcsServiceDeploy;
import software.wings.sm.states.EcsServiceSetup;
import software.wings.sm.states.KubernetesReplicationControllerDeploy;
import software.wings.sm.states.KubernetesReplicationControllerSetup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Migration script to add maxInstances to container setup steps
 * @author brett on 09/13/17
 */
@Integration
@Ignore
public class ContainerMaxInstancesMigrationUtil extends WingsBaseTest {
  private static final String STATE_MACHINES = "stateMachines";

  @Inject private WingsPersistence wingsPersistence;

  @Test
  public void migrateContainerCounts() {
    List<StateMachine> stateMachines = wingsPersistence.createQuery(StateMachine.class).asList();
    Set<StateMachine> affectedStateMachines = new HashSet<>();
    Set<StateMachine> percentUsedMachines = new HashSet<>();
    for (StateMachine stateMachine : stateMachines) {
      Map<String, StateMachine> childStateMachines = stateMachine.getChildStateMachines();
      List<State> kubeSetup = new ArrayList<>();
      List<State> kubeDeploy = new ArrayList<>();
      List<State> ecsSetup = new ArrayList<>();
      List<State> ecsDeploy = new ArrayList<>();

      for (StateMachine sm : childStateMachines.values()) {
        for (State state : sm.getStates()) {
          switch (StateType.valueOf(state.getStateType())) {
            case KUBERNETES_REPLICATION_CONTROLLER_SETUP:
              kubeSetup.add(state);
              break;
            case KUBERNETES_REPLICATION_CONTROLLER_DEPLOY:
              kubeDeploy.add(state);
              break;
            case ECS_SERVICE_SETUP:
              ecsSetup.add(state);
              break;
            case ECS_SERVICE_DEPLOY:
              ecsDeploy.add(state);
              break;
            default:
              break;
          }
        }
      }
      if (!kubeSetup.isEmpty() || !kubeDeploy.isEmpty() || !ecsSetup.isEmpty() || !ecsDeploy.isEmpty()) {
        System.out.println("\n\n*** State Machine: " + stateMachine.getUuid());

        if (!kubeSetup.isEmpty()) {
          System.out.println("\nKubernetes Setups: " + kubeSetup.size());
          System.out.println("Kubernetes Deploys: " + kubeDeploy.size());
          assertThat(kubeSetup.size() == 1);
          KubernetesReplicationControllerSetup setup = (KubernetesReplicationControllerSetup) kubeSetup.get(0);
          int totalInstances = 0;
          kubeDeploy.sort(
              Comparator.comparingInt(state -> ((KubernetesReplicationControllerDeploy) state).getInstanceCount()));
          for (State state : kubeDeploy) {
            KubernetesReplicationControllerDeploy deploy = (KubernetesReplicationControllerDeploy) state;
            System.out.println("Kubernetes deploy incremental instances: " + deploy.getInstanceCount());
            totalInstances += deploy.getInstanceCount();
            if (deploy.getInstanceCount() < totalInstances) {
              deploy.setInstanceCount(totalInstances);
              System.out.println("---Setting Kubernetes deploy instance count to " + totalInstances);
            }
            if (deploy.getInstanceUnitType() == COUNT) {
              System.out.println("Already set to COUNT");
            } else if (deploy.getInstanceUnitType() == PERCENTAGE) {
              System.out.println("Already set to PERCENTAGE");
              percentUsedMachines.add(stateMachine);
            } else {
              deploy.setInstanceUnitType(COUNT);
              System.out.println("---Setting Kubernetes deploy instance unit type to Count");
            }
          }
          if (setup.getMaxInstances() < 10) {
            setup.setMaxInstances(Math.max(totalInstances, 10));
            System.out.println("---Setting Kubernetes setup max instances to " + setup.getMaxInstances());
          }
        }

        if (!ecsSetup.isEmpty()) {
          System.out.println("\nECS Setups: " + ecsSetup.size());
          System.out.println("ECS Deploys: " + ecsDeploy.size());
          assertThat(ecsSetup.size() == 1);
          EcsServiceSetup setup = (EcsServiceSetup) ecsSetup.get(0);
          int totalInstances = 0;
          ecsDeploy.sort(Comparator.comparingInt(state -> ((EcsServiceDeploy) state).getInstanceCount()));
          for (State state : ecsDeploy) {
            EcsServiceDeploy deploy = (EcsServiceDeploy) state;
            System.out.println("ECS deploy incremental instances: " + deploy.getInstanceCount());
            totalInstances += deploy.getInstanceCount();
            if (deploy.getInstanceCount() < totalInstances) {
              deploy.setInstanceCount(totalInstances);
              System.out.println("---Setting ECS deploy instance count to " + totalInstances);
            }
            if (deploy.getInstanceUnitType() == COUNT) {
              System.out.println("Already set to COUNT");
            } else if (deploy.getInstanceUnitType() == PERCENTAGE) {
              System.out.println("Already set to PERCENTAGE");
              percentUsedMachines.add(stateMachine);
            } else {
              deploy.setInstanceUnitType(COUNT);
              System.out.println("---Setting ECS deploy instance unit type to Count");
            }
          }
          if (setup.getMaxInstances() < 10) {
            setup.setMaxInstances(Math.max(totalInstances, 10));
            System.out.println("---Setting ECS setup max instances to " + setup.getMaxInstances());
          }
        }

        affectedStateMachines.add(stateMachine);
        //        wingsPersistence.save(stateMachine);
      }
    }
    System.out.println("\nMigration complete.");
    System.out.println("Checked " + stateMachines.size() + " state machines.");
    Set<String> affectedSmIds = affectedStateMachines.stream().map(StateMachine::getUuid).collect(Collectors.toSet());
    System.out.println("Migrated " + affectedSmIds.size() + " state machines: " + affectedSmIds);
    Set<String> percentSmIds = percentUsedMachines.stream().map(StateMachine::getUuid).collect(Collectors.toSet());
    System.out.println("Percentage used in " + percentSmIds.size() + " state machines: " + percentSmIds);
    Set<String> percentSmOriginIds =
        percentUsedMachines.stream().map(StateMachine::getOriginId).collect(Collectors.toSet());
    System.out.println("Percentage used in " + percentSmOriginIds.size() + " workflows: " + percentSmOriginIds);
    Set<String> percentSmAppIds = percentUsedMachines.stream().map(StateMachine::getAppId).collect(Collectors.toSet());
    System.out.println("Percentage used in " + percentSmAppIds.size() + " apps: " + percentSmAppIds);

    //    DBCollection collection = wingsPersistence.getCollection(STATE_MACHINES);
    //    saveMongoObjectsAsJson("affectedBackupProd.json", collection, affectedSmIds);
    //    saveMongoObjectsAsJson("percentBackupProd.json", collection, percentSmIds);
  }
}
