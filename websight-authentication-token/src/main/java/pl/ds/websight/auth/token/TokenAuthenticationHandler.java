package pl.ds.websight.auth.token;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.auth.core.AuthConstants;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.DefaultAuthenticationFeedbackHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component(service = AuthenticationHandler.class, immediate = true, property = { "path=/", "service.ranking:Integer=100" })
@Designate(ocd = TokenAuthenticationHandlerConfig.class)
public class TokenAuthenticationHandler extends DefaultAuthenticationFeedbackHandler implements AuthenticationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationHandler.class);
    private static final String PAR_J_REASON = "j_reason";

    private String loginPage;

    @Activate
    public void activate(TokenAuthenticationHandlerConfig config) {
        AuthUtils.COOKIE_NAME = config.cookie_name();
        loginPage = config.login_page();
    }

    @Override
    public AuthenticationInfo extractCredentials(final HttpServletRequest request, final HttpServletResponse response) {
        AuthenticationInfo authenticationInfo = getAuthenticationInfo(request, response);
        if (authenticationInfo == null) {
            authenticationInfo = getAuthenticationInfoFromCookie(request);
        }
        return authenticationInfo;
    }

    @Override
    public boolean authenticationSucceeded(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationInfo authInfo) {

        String token = AuthUtils.getToken(request);
        if (StringUtils.isBlank(token)) {
            token = AuthUtils.createToken(authInfo);
            if (StringUtils.isNotBlank(token)) {
                AuthUtils.updateTokenCookie(request, response, token);
            }
        }
        boolean result = false;
        if (AuthUtils.REQUEST_METHOD.equals(request.getMethod()) && request.getRequestURI().endsWith(AuthUtils.REQUEST_URL_SUFFIX)) {
            String resource = AuthUtil.getLoginResource(request, null);
            if (AuthUtil.isRedirectValid(request, resource)) {
                try {
                    response.sendRedirect(resource);
                } catch (IOException ioe) {
                    LOG.error("Failed to send redirect to: " + resource, ioe);
                }
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean requestCredentials(final HttpServletRequest request, final HttpServletResponse response) {
        if (StringUtils.isBlank(loginPage) || !AuthUtil.checkReferer(request, loginPage)) {
            return false;
        }
        final String resource = AuthUtil.setLoginResourceAttribute(request, request.getRequestURI());
        HashMap<String, String> params = new HashMap<>();
        params.put(Authenticator.LOGIN_RESOURCE, resource);

        if (request.getAttribute(AuthenticationHandler.FAILURE_REASON) != null) {
            final Object jReason = request.getAttribute(AuthenticationHandler.FAILURE_REASON);
            @SuppressWarnings("rawtypes")
            final String reason = (jReason instanceof Enum) ? ((Enum) jReason).name() : jReason.toString();
            params.put(AuthenticationHandler.FAILURE_REASON, reason);
        }
        try {
            AuthUtil.sendRedirect(request, response, loginPage, params);
        } catch (IOException e) {
            LOG.error("Failed to redirect to the login page " + loginPage, e);
        }
        return true;
    }

    @Override
    public void dropCredentials(final HttpServletRequest request, final HttpServletResponse response) {
        AuthUtils.updateTokenCookie(request, response, null);
    }

    @Override
    public void authenticationFailed(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationInfo authInfo) {
        String token = AuthUtils.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            request.setAttribute(PAR_J_REASON, "Session timed out, please login again");
        } else {
            request.setAttribute(PAR_J_REASON, "User name and password do not match");
        }
        dropCredentials(request, response);
    }

    private AuthenticationInfo getAuthenticationInfo(final HttpServletRequest request, final HttpServletResponse response) {
        if (AuthUtils.isAuthenticationRequest(request)) {
            if (!AuthUtil.isValidateRequest(request)) {
                // In case of new login request, the old credentials are dropped
                dropCredentials(request, response);
                AuthUtil.setLoginResourceAttribute(request, request.getContextPath());
            }
            SimpleCredentials credentials = new SimpleCredentials(AuthUtils.getUsername(request),
                    AuthUtils.getPassword(request).toCharArray());
            credentials.setAttribute(AuthUtils.ATTR_TOKEN, StringUtils.EMPTY);
            AuthenticationInfo info = AuthUtils.createAuthenticationInfo(credentials, request);
            info.put(AuthConstants.AUTH_INFO_LOGIN, new Object());
            return info;
        }
        return null;
    }

    private AuthenticationInfo getAuthenticationInfoFromCookie(final HttpServletRequest request) {
        String token = AuthUtils.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            TokenCredentials tokenCredentials = new TokenCredentials(token);
            return AuthUtils.createAuthenticationInfo(tokenCredentials, request);
        }
        return null;
    }
}