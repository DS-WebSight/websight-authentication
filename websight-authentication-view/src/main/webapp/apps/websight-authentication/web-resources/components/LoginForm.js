import React from 'react';
import Button, { ButtonGroup } from '@atlaskit/button';
import Form, {
    ErrorMessage,
    Field,
    FormFooter
} from '@atlaskit/form';
import TextField from '@atlaskit/textfield';
import { colors as atlaskitColors } from '@atlaskit/theme';
import styled from 'styled-components';
import {
    generateTheme
} from '@atlaskit/atlassian-navigation';

import AuthContextProvider from 'websight-rest-esm-client/AuthContextProvider.js';

const HiddenField = styled.div`
    display: none;
`;

const theme = generateTheme({
    name: 'high-contrast',
    backgroundColor: atlaskitColors.B500,
    highlightColor: atlaskitColors.N0
});

export default class Login extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            targetPath: this.getTargetPath(),
            errorMessage: this.getErrorMessage()
        };

        this.asyncFormSubmit = this.asyncFormSubmit.bind(this);
    }

    getTargetPath() {
        const resource = this.getQueryStringParameter(window.location.href, 'resource');
        return resource ? resource : '/';
    }

    getErrorMessage() {
        const jReason = this.getQueryStringParameter(window.location.href, 'j_reason');
        if (jReason) {
            if (jReason === 'INVALID_CREDENTIALS') {
                return 'Invalid username or password';
            }
            return 'Authentication failed: ' + jReason;
        }
        return '';
    }

    getQueryStringParameter(url, parameter) {
        const params = new URLSearchParams(new URL(url).search);
        return params.get(parameter);
    }

    asyncFormSubmit(data) {
        const formData = new FormData();
        Object.keys(data).forEach((key) =>
            formData.append(key, data[key])
        );

        fetch('/j_security_check', {
            method: 'POST',
            redirect: 'manual',
            body: formData
        }).then((response => {
            if (response.status !== 403) {
                AuthContextProvider.checkAuthentication();
            } else {
                response.text().then(text =>
                    this.setState({ errorMessage: text })
                )
            }
        }));
    }

    render() {
        const { targetPath } = this.state;
        const { asyncSubmit } = this.props;

        return (
            <>
                <Form
                    onSubmit={(data) => {
                        if (asyncSubmit) {
                            this.asyncFormSubmit(data);
                        } else {
                            const form = document.querySelector('#login-form')
                            form.submit();
                        }
                    }}
                >
                    {({ formProps, submitting }) => (
                        <form id='login-form' method='post' action='j_security_check' {...formProps}>
                            <HiddenField>
                                <Field name='_charset_' defaultValue='UTF-8'>
                                    {({ fieldProps }) => (
                                        <TextField {...fieldProps} />
                                    )}
                                </Field>
                            </HiddenField>
                            <HiddenField>
                                <Field name='resource' defaultValue={targetPath}>
                                    {({ fieldProps }) => (
                                        <TextField {...fieldProps} />
                                    )}
                                </Field>
                            </HiddenField>
                            <Field name='j_username' label='Username' isRequired defaultValue=''>
                                {({ fieldProps, error }) => (
                                    <>
                                        <TextField autoComplete='off' {...fieldProps} />
                                        {error && (
                                            <ErrorMessage>
                                                Invalid username or password.
                                            </ErrorMessage>
                                        )}
                                    </>
                                )}
                            </Field>
                            <Field name='j_password' label='Password' defaultValue='' isRequired>
                                {({ fieldProps, error }) => (
                                    <>
                                        <TextField type='password' {...fieldProps} />
                                        {error && (
                                            <ErrorMessage>
                                                Password needs to be more than 3 characters.
                                            </ErrorMessage>
                                        )}
                                    </>
                                )}
                            </Field>
                            {this.state.errorMessage ? <p style={{ color: atlaskitColors.R400 }}>{this.state.errorMessage}</p> : undefined}
                            <FormFooter>
                                <ButtonGroup >
                                    <Button type='submit' appearance='primary' isLoading={submitting}>
                                        Log in
                                    </Button>
                                </ButtonGroup>
                            </FormFooter>
                        </form>
                    )}
                </Form>
            </>
        );
    }
}