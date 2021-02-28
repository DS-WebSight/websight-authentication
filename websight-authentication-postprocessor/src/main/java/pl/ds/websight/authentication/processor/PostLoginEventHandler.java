package pl.ds.websight.authentication.processor;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import pl.ds.websight.system.user.provider.service.SystemUserProvider;

@Component(service = EventHandler.class, immediate = true, property = {
        "event.topics=org/apache/sling/auth/core/Authenticator/LOGIN" })
public class PostLoginEventHandler implements EventHandler {

    private static final String THREAD_POOL_NAME = "login-details-processor";

    @Reference
    private SystemUserProvider userProvider;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ThreadPoolManager threadPoolManager;
    private ThreadPool threadPool;

    @Activate
    private void activate() {
        threadPool = threadPoolManager.get(THREAD_POOL_NAME);
    }

    @Deactivate
    private void deactivate() {
        threadPoolManager.release(threadPool);
    }

    @Override public void handleEvent(Event event) {
            threadPool.execute(new LoginDetailsProcessor(userProvider, resourceResolverFactory,
                    (String) event.getProperty(SlingConstants.PROPERTY_USERID)));
    }
}
