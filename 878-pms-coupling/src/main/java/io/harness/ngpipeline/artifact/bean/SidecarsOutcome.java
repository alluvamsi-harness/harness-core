package io.harness.ngpipeline.artifact.bean;

import io.harness.annotation.RecasterAlias;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.pms.sdk.core.data.Outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.HashMap;
import org.springframework.data.annotation.TypeAlias;

@OwnedBy(HarnessTeam.CDC)
@JsonTypeName("SidecarsOutcome")
@TypeAlias("sidecarsOutcome")
@RecasterAlias("io.harness.ngpipeline.artifact.bean.SidecarsOutcome")
public class SidecarsOutcome extends HashMap<String, ArtifactOutcome> implements Outcome {}
