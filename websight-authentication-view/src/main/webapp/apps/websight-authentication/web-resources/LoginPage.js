import React from 'react';
import {
    FormHeader
} from '@atlaskit/form';
import { colors as atlaskitColors } from '@atlaskit/theme';
import styled from 'styled-components';
import {
    AtlassianNavigation,
    ProductHome,
    generateTheme
} from '@atlaskit/atlassian-navigation';

import Logo from './components/Logo.js';
import LoginForm from './components/LoginForm.js';

const Content = styled.div`
    display: flex;
    width: 400px;
    max-width: 100%;
    margin: 0px auto 0 auto;
    flex-direction: column;
    box-shadow: 0 1px 1px ${atlaskitColors.N40A}, 0 0 0 1px ${atlaskitColors.N30A};
    border: none;
    border-radius: 3px;
    padding: 20px;
`;

const ContentContainer = styled.div`
    padding-top: 124px;
    min-height: 476px;
`;

const theme = generateTheme({
    name: 'high-contrast',
    backgroundColor: atlaskitColors.B500,
    highlightColor: atlaskitColors.N0
});

const HomeSection = () => (
    <ProductHome icon={Logo} logo={Logo} href='/' />
);

export default class Login extends React.Component {
    render() {
        return (
            <>
                <AtlassianNavigation
                    theme={theme}
                    renderProductHome={HomeSection}
                />
                <ContentContainer>
                    <Content>
                        <FormHeader title='Log in' />
                        <LoginForm />
                    </Content>
                </ContentContainer>
            </>
        );
    }
}