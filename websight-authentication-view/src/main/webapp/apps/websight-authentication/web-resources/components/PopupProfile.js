import React from 'react';
import { Profile as AtlaskitProfile } from '@atlaskit/atlassian-navigation';
import Popup from '@atlaskit/popup';
import { LinkItem, MenuGroup, Section } from '@atlaskit/menu';
import Modal, { ModalTransition } from '@atlaskit/modal-dialog';
import styled from 'styled-components';

import authContextAware from 'websight-rest-atlaskit-client/AuthContextAware';
import LoginForm from './LoginForm.js';
import AuthContextProvider, { AUTH_CONTEXT_UPDATED } from 'websight-rest-esm-client/AuthContextProvider.js';

const Icon = styled.i`
    border-radius: 50%;
    width: 24px;
    height: 24px;
`;

const FormContainer = styled.div`
    padding: 20px 30px;

    input {
        width: 250px;
    }
`;

class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpenForLogIn: false,
            isOpenForProfile: false,
            isOpenModal: false
        };

        this.profileMenu = this.profileMenu.bind(this);

    }

    componentDidMount() {
      window.addEventListener(AUTH_CONTEXT_UPDATED, e => {
          if (e.detail.authContext.userId === 'anonymous') {
              this.setState({ isOpenModal : true, isOpenForLogIn : false });
          } else {
              this.setState({ isOpenModal : false });
          }
      });
    }

    loginFormContainer() {
        return (
            <FormContainer>
                <LoginForm asyncSubmit={true} />
            </FormContainer>
        );
    }

    profileMenu() {
        const onFinish = () =>
            this.setState({ isOpenForProfile: false, isOpenForLogIn: false });

        const asyncFormLogout = () =>
            fetch('/system/sling/logout', {
                method: 'GET',
                redirect: 'manual'
            }).then((() => {
                AuthContextProvider.checkAuthentication(onFinish, onFinish);
            }));

        return (
            <MenuGroup>
                <Section hasSeparator>
                    <LinkItem onClick={asyncFormLogout}>Log out</LinkItem>
                </Section>
            </MenuGroup>
        );
    }

    render() {
        const { isOpenForProfile, isOpenForLogIn, isOpenModal} = this.state;
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
                                icon={<Icon className='material-icons'>account_circle</Icon>}
                                onClick={() => this.setState((prevState) => ({ isOpenForProfile: !prevState.isOpenForProfile }))}
                                isSelected={isOpenForProfile}
                            />
                        )}
                    />) :
                    (<>
                    <ModalTransition>
                    {isOpenModal && (
                        <Modal
                          actions={[
                            { text: 'Close', onClick: () => this.setState({ isOpenModal : false })},
                          ]}
                          heading="You have been logged out">
                          Your session has been terminated. Log in again or continue as anonymous user.
                        </Modal>
                        )}
                    </ModalTransition>
                    <Popup
                        content={this.loginFormContainer}
                        isOpen={isOpenForLogIn}
                        placement='bottom-start'
                        onClose={() => this.setState({ isOpenForLogIn: false })}
                        trigger={triggerProps => (
                            <AtlaskitProfile
                                {...triggerProps}
                                icon={<Icon className='material-icons'>login</Icon>}
                                onClick={() => this.setState((prevState) => ({ isOpenForLogIn: !prevState.isOpenForLogIn }))}
                                isSelected={isOpenForLogIn}
                            />
                        )}
                    /></>)
            );
        }
    }
}

export default authContextAware(Profile);
