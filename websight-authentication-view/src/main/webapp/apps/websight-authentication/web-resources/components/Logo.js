import React from 'react';

export default class Logo extends React.Component {
    render() {
        return (
            // TODO: Please replace it with proper WebSight logo.
            <img width={this.props.size || 36} style={this.props.style}
                src={`https://api.iconify.design/mdi:semantic-web.svg?color=${(this.props.color || '#ccc').replace('#', '%23')}`}
                alt='WebSight Logo' />
        )
    }
}
