package io.harness.shell;

import static io.harness.data.structure.HasPredicate.hasSome;
import static io.harness.eraro.ErrorCode.UNKNOWN_EXECUTOR_TYPE_ERROR;
import static io.harness.shell.AccessType.KEY_SUDO_APP_USER;
import static io.harness.shell.AccessType.KEY_SU_APP_USER;
import static io.harness.shell.ExecutorType.BASTION_HOST;
import static io.harness.shell.ExecutorType.KEY_AUTH;
import static io.harness.shell.ExecutorType.PASSWORD_AUTH;
import static io.harness.shell.SshHelperUtils.normalizeError;

import io.harness.exception.WingsException;
import io.harness.logging.LogCallback;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class SshSessionManager {
  private static ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();
  private static ConcurrentMap<String, List<Session>> simplexSessions = new ConcurrentHashMap<>();

  /**
   * Gets cached session.
   *
   * @param config the config
   * @param logCallback
   * @return the cached session
   */
  public static synchronized Session getCachedSession(SshSessionConfig config, LogCallback logCallback) {
    String key = getKey(config.getExecutionId(), config.getHost());
    log.info("Fetch session for executionId : {}, hostName: {} ", config.getExecutionId(), config.getHost());

    Session cachedSession = sessions.computeIfAbsent(key, s -> {
      log.info("No session found. Create new session for executionId : {}, hostName: {}", config.getExecutionId(),
          config.getHost());
      return getSession(config, logCallback);
    });

    // Unnecessary but required test before session reuse.
    // test channel. http://stackoverflow.com/questions/16127200/jsch-how-to-keep-the-session-alive-and-up
    try {
      testSession(config, cachedSession);
    } catch (Exception exception) {
      log.error("Session connection test failed. Reopen new session", exception);
      cachedSession = sessions.merge(key, cachedSession, (session1, session2) -> getSession(config, logCallback));
    }
    return cachedSession;
  }

  private static void testSession(SshSessionConfig config, Session cachedSession) throws JSchException {
    ChannelExec testChannel = (ChannelExec) cachedSession.openChannel("exec");
    testChannel.setCommand("true");
    testChannel.connect(config.getSocketConnectTimeout());
    testChannel.disconnect();
    log.info("Session connection test successful");
  }

  @NotNull
  private static String getKey(String executionId, String host) {
    return executionId + "~" + host.trim();
  }

  private static Session getSession(SshSessionConfig config, LogCallback executionLogCallback) {
    if (config.getExecutorType() == null) {
      if (config.getBastionHostConfig() != null) {
        config.setExecutorType(BASTION_HOST);
      } else {
        if (config.getAccessType() == AccessType.KEY || config.getAccessType() == KEY_SU_APP_USER
            || config.getAccessType() == KEY_SUDO_APP_USER) {
          config.setExecutorType(KEY_AUTH);
        } else {
          config.setExecutorType(PASSWORD_AUTH);
        }
      }
    }

    try {
      switch (config.getExecutorType()) {
        case PASSWORD_AUTH:
        case KEY_AUTH:
          return SshSessionFactory.getSSHSession(config, executionLogCallback);
        case BASTION_HOST:
          return SshSessionFactory.getSSHSessionWithJumpbox(config, executionLogCallback);
        default:
          throw new WingsException(
              UNKNOWN_EXECUTOR_TYPE_ERROR, new Throwable("Unknown executor type: " + config.getExecutorType()));
      }
    } catch (JSchException jschEx) {
      throw new WingsException(normalizeError(jschEx), normalizeError(jschEx).name(), jschEx);
    }
  }

  /**
   * Evict and disconnect cached session.
   *
   * @param executionId the execution id
   * @param hostName    the host name
   */
  public static void evictAndDisconnectCachedSession(String executionId, String hostName) {
    log.info("Clean up session for executionId : {}, hostName: {} ", executionId, hostName);
    String key = getKey(executionId, hostName);
    Session session = sessions.remove(key);
    disconnectSession(executionId, hostName, session);

    List<Session> simplexSessionList = simplexSessions.remove(key);
    if (hasSome(simplexSessionList)) {
      simplexSessionList.forEach(s -> disconnectSession(executionId, hostName, s));
    }
  }

  private static void disconnectSession(String executionId, String hostName, Session session) {
    if (session != null && session.isConnected()) {
      log.info("Found cached session. disconnecting the session");
      session.disconnect();
      log.info("Session disconnected successfully");
    } else {
      log.info("No cached session found for executionId : {}, hostName: {} ", executionId, hostName);
    }
  }

  private static void updateSimplexSessionMap(SshSessionConfig config, Session session) {
    String key = getKey(config.getExecutionId(), config.getHost());
    simplexSessions.putIfAbsent(key, new LinkedList<>());
    simplexSessions.get(key).add(session);
  }

  public static Session getSimplexSession(SshSessionConfig config, LogCallback logCallback) {
    Session session = getSession(config, logCallback);
    try {
      testSession(config, session);
    } catch (Exception exception) {
      log.error("Session connection test failed. Reopen new session", exception);
      session = getSession(config, logCallback);
    }

    updateSimplexSessionMap(config, session);
    return session;
  }
}
