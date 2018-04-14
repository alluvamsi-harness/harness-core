package software.wings.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import software.wings.app.MainConfiguration;
import software.wings.app.PortalConfig;
import software.wings.beans.AuthToken;
import software.wings.beans.ErrorCode;
import software.wings.beans.User;
import software.wings.common.AuditHelper;
import software.wings.dl.WingsPersistence;
import software.wings.exception.WingsException;
import software.wings.service.intfc.AuditService;
import software.wings.service.intfc.AuthService;
import software.wings.service.intfc.UserService;

import java.io.IOException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class AuthenticationFilterTest {
  @Mock WingsPersistence wingsPersistence = mock(WingsPersistence.class);
  @Mock MainConfiguration configuration = mock(MainConfiguration.class);
  @Mock UserService userService = mock(UserService.class);
  @Mock AuthService authService = mock(AuthService.class);
  @Mock AuditService auditService = mock(AuditService.class);
  @Mock AuditHelper auditHelper = mock(AuditHelper.class);

  @InjectMocks AuthenticationFilter authenticationFilter;

  ContainerRequestContext context = mock(ContainerRequestContext.class);

  SecurityContext securityContext = mock(SecurityContext.class);

  @Before
  public void setUp() {
    authenticationFilter =
        new AuthenticationFilter(authService, wingsPersistence, configuration, userService, auditService, auditHelper);
    authenticationFilter = spy(authenticationFilter);
    when(context.getSecurityContext()).thenReturn(securityContext);
    when(securityContext.isSecure()).thenReturn(true);
    PortalConfig portalConfig = mock(PortalConfig.class);
    when(configuration.getPortal()).thenReturn(portalConfig);
  }

  @Test
  public void testAuthenticationFilterTestOptions() throws IOException {
    when(context.getMethod()).thenReturn(HttpMethod.OPTIONS);
    authenticationFilter.filter(context);
    assertThat(context.getSecurityContext().isSecure());

    doReturn(true).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
    authenticationFilter.filter(context);
    assertThat(context.getSecurityContext().isSecure());
  }

  @Test
  public void testNoAuthorizationToken() throws IOException {
    try {
      doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
      authenticationFilter.filter(context);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException e) {
      assertThatExceptionOfType(WingsException.class);
    }
  }

  @Test
  public void testDelegateRequestAuthentication() throws IOException {
    when(context.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Delegate token");
    doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
    doReturn(true).when(authenticationFilter).delegateAPI();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPathParameters()).thenReturn(new MultivaluedHashMap<>());
    when(uriInfo.getQueryParameters()).thenReturn(new MultivaluedHashMap<>());
    when(context.getUriInfo()).thenReturn(uriInfo);
    authenticationFilter.filter(context);
    assertThat(context.getSecurityContext().isSecure());
  }

  @Test
  public void testLearningEngineRequestAuthentication() throws IOException {
    when(context.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("LearningEngine token");
    doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
    doReturn(true).when(authenticationFilter).learningEngineServiceAPI();
    doReturn(false).when(authenticationFilter).delegateAPI();
    authenticationFilter.filter(context);
    assertThat(context.getSecurityContext().isSecure());
  }

  @Test
  public void testInvalidBearerTokenPresent() throws IOException {
    try {
      when(context.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bearerToken");
      doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
      doReturn(false).when(authenticationFilter).delegateAPI();
      doReturn(false).when(authenticationFilter).learningEngineServiceAPI();
      when(authService.validateToken(anyString())).thenThrow(new WingsException(ErrorCode.USER_DOES_NOT_EXIST));
      authenticationFilter.filter(context);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException e) {
      assertThatExceptionOfType(WingsException.class);
    }
  }

  @Test
  public void testValidBearerTokenPresent() throws IOException {
    try {
      when(context.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bearerToken");
      doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
      doReturn(false).when(authenticationFilter).delegateAPI();
      doReturn(false).when(authenticationFilter).learningEngineServiceAPI();
      AuthToken authToken = new AuthToken("testUser", 0L);
      authToken.setUser(mock(User.class));
      when(authService.validateToken(anyString())).thenReturn(authToken);
      authenticationFilter.filter(context);
    } catch (WingsException e) {
      fail(e.getMessage(), WingsException.class);
    }
  }

  @Test
  public void testIncorrectToken() throws IOException {
    try {
      when(context.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("fakeToken");
      doReturn(false).when(authenticationFilter).authenticationExemptedRequests(any(ContainerRequestContext.class));
      doReturn(false).when(authenticationFilter).delegateAPI();
      doReturn(false).when(authenticationFilter).learningEngineServiceAPI();
      authenticationFilter.filter(context);
    } catch (WingsException e) {
      assertThatExceptionOfType(WingsException.class);
    }
  }
}
