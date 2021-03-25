package pl.ds.websight.authentication.fragments.global.globalnavigation.profile;

import org.osgi.service.component.annotations.Component;
import pl.ds.websight.fragments.registry.WebFragment;

@Component
public class ProfileWebFragment implements WebFragment {

    @Override
    public String getKey() {
        return "websight.global.global-navigation.profile";
    }

    @Override
    public String getFragment() {
        return "/apps/websight-authentication/web-resources/fragments/global/global-navigation/profile/ProfileFragment.js";
    }

    @Override
    public int getRanking() {
        return 100;
    }
}
