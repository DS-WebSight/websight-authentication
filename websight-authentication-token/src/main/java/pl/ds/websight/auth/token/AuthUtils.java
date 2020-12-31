package pl.ds.websight.auth.token;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.sling.auth.core.spi.AuthenticationInfo;

import javax.jcr.SimpleCredentials;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class AuthUtils {

    public static String COOKIE_NAME = "websight.auth";
    public static final String REQUEST_URL_SUFFIX = "/j_security_check";
    public static final String REQUEST_METHOD = "POST";
    public static final String ATTR_TOKEN = ".token";
    private static final String ATTR_NAME_TOKEN = "websight-token";
    private static final String PAR_J_USERNAME = "j_username";
    private static final String PAR_J_PASSWORD = "j_password";
    private static final String ATTR_REFERER = "referer";
    private static final String JCR_CREDENTIALS = "user.jcr.credentials";
    private static final String JCR_CREDENTIALS_USER = "user.name";
    private static final String AUTH_TYPE = "TOKEN";

    private AuthUtils() {
    }

    public static boolean isAuthenticationRequest(HttpServletRequest request) {
        return (REQUEST_METHOD.equals(request.getMethod())) && (request.getRequestURI().endsWith(REQUEST_URL_SUFFIX))
                && (request.getParameter(PAR_J_USERNAME) != null && request.getParameter(PAR_J_PASSWORD) != null);
    }

    public static AuthenticationInfo createAuthenticationInfo(final SimpleCredentials credentials, final HttpServletRequest request) {
        String referrer = request.getHeader(ATTR_REFERER);
        if (referrer != null) {
            credentials.setAttribute(ATTR_REFERER, referrer);
        }
        AuthenticationInfo info = new AuthenticationInfo(AUTH_TYPE);
        info.put(JCR_CREDENTIALS, credentials);
        info.put(JCR_CREDENTIALS_USER, credentials.getUserID());
        return info;
    }

    public static AuthenticationInfo createAuthenticationInfo(final TokenCredentials credentials, final HttpServletRequest request) {
        String referrer = request.getHeader(ATTR_REFERER);
        if (referrer != null) {
            credentials.setAttribute(ATTR_REFERER, referrer);
        }
        AuthenticationInfo info = new AuthenticationInfo(AUTH_TYPE);
        info.put(JCR_CREDENTIALS, credentials);
        return info;
    }

    public static String getToken(final HttpServletRequest request) {
        String token = (String) request.getAttribute(ATTR_NAME_TOKEN);
        if (StringUtils.isBlank(token)) {
            String cookieToken = getCookie(request, COOKIE_NAME);
            if (StringUtils.isNotBlank(cookieToken)) {
                token =  new String(Base64.getDecoder().decode(cookieToken));
            }
            request.setAttribute(ATTR_NAME_TOKEN, token);
        }
        return token;
    }

    public static void updateTokenCookie(final HttpServletRequest request, final HttpServletResponse response, final String token) {
        request.setAttribute(ATTR_NAME_TOKEN, token);
        setCookie(request, response, COOKIE_NAME, token, StringUtils.isNotBlank(token) ? -1 : 0, null);
    }

    public static String createToken(final AuthenticationInfo authInfo) {
        String token = null;
        Object credentials = authInfo.get(AuthUtils.JCR_CREDENTIALS);
        if (credentials instanceof SimpleCredentials) {
            Object tokenObject = ((SimpleCredentials) credentials).getAttribute(ATTR_TOKEN);
            if (tokenObject != null) {
                token = tokenObject.toString();
            }
        } else if (credentials instanceof TokenCredentials) {
            token = ((TokenCredentials) credentials).getToken();
        }
        return token;
    }

    private static String getCookie(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(name) && StringUtils.isNotBlank(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static void setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name,
            final String value, final int age, final String domain) {
        final String ctxPath = request.getContextPath();
        final String cookiePath = (ctxPath == null || ctxPath.length() == 0) ? "/" : ctxPath;
        final StringBuilder header = new StringBuilder();
        String cookieString = value != null ? Base64.getEncoder().encodeToString(value.getBytes()) : value;
        header.append(name).append("=").append(cookieString);
        header.append("; Path=").append(cookiePath);
        header.append("; HttpOnly");

        if (domain != null) {
            header.append("; Domain=").append(domain);
        }
        if (age >= 0) {
            header.append("; Max-Age=").append(age);
        }
        if (request.isSecure()) {
            header.append("; Secure");
        }
        response.addHeader("Set-Cookie", header.toString());
    }

    public static String getUsername(HttpServletRequest request) {
        return request.getParameter(PAR_J_USERNAME);
    }

    public static String getPassword(HttpServletRequest request) {
        return request.getParameter(PAR_J_PASSWORD);
    }
}
