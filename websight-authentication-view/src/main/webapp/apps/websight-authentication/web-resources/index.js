import React from 'react';
import ReactDOM from 'react-dom';
import '@atlaskit/css-reset';

import WebFragments from 'websight-fragments-esm';

import LoginPage from './LoginPage.js';

const FooterFragment = () => (
    <WebFragments fragmentsKey='websight.global.page.footer' />
);

class App extends React.Component {
    render() {
        return (
            <>
                <LoginPage />
                <FooterFragment />
            </>
        );
    }
}

ReactDOM.render(<App />, document.getElementById('app-root'));