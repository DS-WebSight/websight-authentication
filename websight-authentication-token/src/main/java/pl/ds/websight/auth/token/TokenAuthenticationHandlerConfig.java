package pl.ds.websight.auth.token;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Token Authentication Handler Config")
@interface TokenAuthenticationHandlerConfig {

    @AttributeDefinition(name = "Login page", description = "Location of login page")
    String login_page() default "";

    @AttributeDefinition(name = "Cookie name", description = "Name of the cookie containing authentication token")
    String cookie_name() default "websight.auth";
}
