package de.objektkontor.wsc.container.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SslUtilities {

    public static KeyManager[] createKeyManagers(String keystoreLocation, String keystorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        KeyManager[] keyManagers = null;
        if (keystoreLocation != null && keystorePassword != null) {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] password = keystorePassword.toCharArray();
            InputStream inputStream = ClassLoader.class.getResourceAsStream(keystoreLocation);
            keystore.load(inputStream, password);

            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, password);

            keyManagers = keyManagerFactory.getKeyManagers();
        }
        return keyManagers;
    }

    public static TrustManager[] createTrustManagers(String truststoreLocation, String truststorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        TrustManager[] trustManagers = null;
        if (truststoreLocation != null && truststorePassword != null) {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] password = truststorePassword.toCharArray();
            InputStream inputStream = ClassLoader.class.getResourceAsStream(truststoreLocation);
            keystore.load(inputStream, password);

            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);

            trustManagers = trustManagerFactory.getTrustManagers();
        }
        return trustManagers;
    }
}
