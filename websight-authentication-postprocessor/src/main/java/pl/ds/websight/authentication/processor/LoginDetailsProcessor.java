package pl.ds.websight.authentication.processor;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.system.user.provider.service.SystemUserConfig;
import pl.ds.websight.system.user.provider.service.SystemUserProvider;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;

public class LoginDetailsProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LoginDetailsProcessor.class);

    private static final String LOGIN_COUNT_PROPERTY_NAME = "loginCount";
    private static final String LAST_LOGGED_IN_PROPERTY_NAME = "lastLoggedIn";
    private static final String META_INFO_NODE_NAME = "metainfo";

    private ResourceResolverFactory resourceResolverFactory;
    private SystemUserProvider systemUserProvider;
    private String userId;
    private SystemUserConfig systemUserConfig = new AuthProcessorSystemUserConfig();

    public LoginDetailsProcessor(SystemUserProvider systemUserProvider, ResourceResolverFactory resourceResolverFactory, String userId) {
        this.systemUserProvider = systemUserProvider;
        this.resourceResolverFactory = resourceResolverFactory;
        this.userId = userId;
    }

    @Override
    public void run() {
        try (ResourceResolver resourceResolver = systemUserProvider.
                getSystemUserResourceResolver(resourceResolverFactory, systemUserConfig)) {
            UserManager userManager = AccessControlUtil.getUserManager(resourceResolver.adaptTo(Session.class));
            if (userManager != null) {
                Authorizable user = userManager.getAuthorizable(userId);
                if (user != null) {
                    processUser(resourceResolver, user);
                    resourceResolver.commit();
                }
            }
        } catch (RepositoryException | PersistenceException | LoginException e) {
            LOG.warn("Failed to store login details for user: " + userId);
        }
    }

    private void processUser(ResourceResolver resourceResolver, Authorizable user) throws RepositoryException, PersistenceException {
        Resource userResource = resourceResolver.getResource(user.getPath());
        Resource metaInfo = userResource.getChild(META_INFO_NODE_NAME);
        if (metaInfo == null) {
            metaInfo = resourceResolver.create(userResource, META_INFO_NODE_NAME, null);
        }
        storeLoginDetails(metaInfo);
    }

    private void storeLoginDetails(Resource profileResource) {
        ModifiableValueMap valueMap = profileResource.adaptTo(ModifiableValueMap.class);
        long loginCount = valueMap.get(LOGIN_COUNT_PROPERTY_NAME, 0L);
        valueMap.put(LOGIN_COUNT_PROPERTY_NAME, ++loginCount);
        valueMap.put(LAST_LOGGED_IN_PROPERTY_NAME, Calendar.getInstance());
    }
}
