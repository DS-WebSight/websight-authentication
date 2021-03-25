package pl.ds.websight.authentication.fragments.administration.globalnavigation.profile;

import org.osgi.service.component.annotations.Component;
import pl.ds.websight.fragments.registry.WebFragment;

@Component
public class ProfileWebFragment implements WebFragment {

    @Override
    public String getKey() {
        return "websight.administration.global-navigation.profile";
    }

    @Override
    public String getFragment() {
        return "/apps/websight-authentication/web-resources/fragments/administration/global-navigation/profile/ProfileFragment.js";
    }

    @Override
    public int getRanking() {
        return 100;
    }
}
