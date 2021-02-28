import React from 'react';
import { Profile as AtlaskitProfile, SignIn } from '@atlaskit/atlassian-navigation';
import Popup from '@atlaskit/popup';
import { LinkItem, MenuGroup, Section } from '@atlaskit/menu';
import Tooltip from '@atlaskit/tooltip';
import styled from 'styled-components';

import authContextAware from 'websight-rest-atlaskit-client/AuthContextAware';

const Icon = styled.i`
    border-radius: 50%;
    width: 24px;
    height: 24px;
`;

class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpenForProfile: false
        };

        this.profileMenu = this.profileMenu.bind(this);
    }

    profileMenu() {
        return (
            <MenuGroup>
                <Section hasSeparator>
                    {/* TODO implement and link here user profile view */}
                    <LinkItem href="/system/sling/logout?resource=/">Log out</LinkItem>
                </Section>
            </MenuGroup>
        );
    }

    render() {
        const { isOpenForProfile } = this.state;
        const { authContext } = this.props;

        if (!authContext.isInitialized()) {
            return <></>;
        } else {
            return (
                authContext.isLoggedIn() ?
                    (<Popup
                        content={this.profileMenu}
                        isOpen={isOpenForProfile}
                        placement='bottom-start'
                        onClose={() => this.setState({ isOpenForProfile: false })}
                        trigger={triggerProps => (
                            <AtlaskitProfile
                                {...triggerProps}
                                icon={
                                    <Tooltip hideTooltipOnClick={true} content={'Logged in as ' + authContext.userId}>
                                        <Icon className='material-icons'>account_circle</Icon>
                                    </Tooltip>
                                }
                                onClick={() => this.setState((prevState) => ({ isOpenForProfile: !prevState.isOpenForProfile }))}
                                isSelected={isOpenForProfile}
                            />
                        )}
                    />) :
                    (<SignIn tooltip='Log in' href={`/system/sling/login?resource=${window.location.pathname}`} />)
            );
        }
    }
}

export default authContextAware(Profile);