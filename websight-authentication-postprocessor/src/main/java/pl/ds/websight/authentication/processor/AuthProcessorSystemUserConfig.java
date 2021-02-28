package pl.ds.websight.authentication.processor;

import pl.ds.websight.system.user.provider.service.SystemUserConfig;

import javax.jcr.security.Privilege;
import java.util.HashMap;
import java.util.Map;

public class AuthProcessorSystemUserConfig implements SystemUserConfig {

    private static final Map<String, String[]> privileges = new HashMap<>();

    static {
        privileges.put("/home/users", new String[] {
                Privilege.JCR_READ,
                Privilege.JCR_WRITE,
                Privilege.JCR_LOCK_MANAGEMENT,
                Privilege.JCR_VERSION_MANAGEMENT});
    }

    @Override
    public String getSystemUserId() {
        return "websight-auth-process";
    }

    @Override
    public Map<String, String[]> getPrivileges() {
        return privileges;
    }
}
