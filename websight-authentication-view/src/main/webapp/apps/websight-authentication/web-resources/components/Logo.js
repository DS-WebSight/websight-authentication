import React from 'react';

export default class Logo extends React.Component {
    render() {
        return (
            <img width={this.props.size || 36} style={this.props.style}
                src='/apps/websight-authentication/web-resources/images/websight-logo.svg'
                alt='WebSight Logo' />
        )
    }
}
