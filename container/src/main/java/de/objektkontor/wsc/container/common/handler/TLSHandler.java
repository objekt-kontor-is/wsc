package de.objektkontor.wsc.container.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

import de.objektkontor.wsc.container.InboundHandler;
import de.objektkontor.wsc.container.OutboundHandler;
import de.objektkontor.wsc.container.common.SslUtilities;
import de.objektkontor.wsc.container.common.config.TLSConfig;
import de.objektkontor.wsc.container.common.config.TLSServerConfig;

public class TLSHandler implements InboundHandler, OutboundHandler {

    protected final TLSConfig config;
    protected SSLContext sslContext;

    public TLSHandler(TLSServerConfig config) throws GeneralSecurityException, IOException {
        this.config = config;
        sslContext = config.isEnabled() ? createSSLContext() : null;
    }

    @Override
    public String name() {
        return "TLS (Client)";
    }

    @Override
    public Class<?> inputInboundType() {
        return ByteBuf.class;
    }

    @Override
    public Class<?> inputOutboundType() {
        return ByteBuf.class;
    }

    @Override
    public Class<?> outputInboundType() {
        return ByteBuf.class;
    }

    @Override
    public Class<?> outputOutboundType() {
        return ByteBuf.class;
    }

    @Override
    public ChannelHandler create() {
        return config.isEnabled() ? new SslHandler(createSSLEngine()) : null;
    }

    protected SSLContext createSSLContext() throws GeneralSecurityException, IOException {
        KeyManager[] keyManagers = SslUtilities.createKeyManagers(config.getKeystoreLocation(), config.getKeystorePassword());
        TrustManager[] trustManagers = SslUtilities.createTrustManagers(config.getTruststoreLocation(), config.getTruststorePassword());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    protected SSLEngine createSSLEngine() {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }
}