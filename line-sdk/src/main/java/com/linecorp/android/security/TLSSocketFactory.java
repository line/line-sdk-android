package com.linecorp.android.security;


import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

// Cf. https://code.google.com/p/android/issues/detail?id=78431
// https://stackoverflow.com/questions/26649389/how-to-disable-sslv3-in-android-for-httpsurlconnection/29946540#29946540
// https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
public class TLSSocketFactory extends SSLSocketFactory {

    private static final String TAG = "TLSSocketFactory";

    private static final int TLS12_ENABLED_API_LEVEL = Build.VERSION_CODES.JELLY_BEAN;

    // removes RC4, DES/3DES, PSK, and non-EC Diffie-Hellman
    private static final String[] UNSAFE_CIPHERS = {"RC4", "DES", "PSK", "_DHE_"};

    @NonNull
    private final SSLSocketFactory sslSocketFactory;
    private boolean removeUnsafeCiphers;

    @NonNull
    private Class<?> openSslSocketClass;
    @Nullable
    private Method setHostnameMethod;

    public TLSSocketFactory(@NonNull SSLSocketFactory sslSocketFactory) {
        this(sslSocketFactory, true);
    }

    public TLSSocketFactory(@NonNull SSLSocketFactory sslSocketFactory, boolean removeUnsafeCiphers) {
        this.sslSocketFactory = sslSocketFactory;
        this.removeUnsafeCiphers = removeUnsafeCiphers;

        initSNI();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "TLSSocketFactory is created.", new Throwable("This is not Error."));
        }
    }

    // SNI for wrapped socket, if supported by plaforrm
    // Cf. Plaform.java in okhttp
    private void initSNI() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "initSNI");
        }

        try {
            try {
                openSslSocketClass = Class.forName("com.android.org.conscrypt.OpenSSLSocketImpl");
            } catch (ClassNotFoundException ignored) {
                // Older platform before being unbundled.
                openSslSocketClass = Class.forName(
                        "org.apache.harmony.xnet.provider.jsse.OpenSSLSocketImpl");
            }

            setHostnameMethod = openSslSocketClass.getMethod("setHostname", String.class);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Found socket class: " + openSslSocketClass.getName());
                Log.d(TAG, "with setHostname() method: " + setHostnameMethod.toString());
            }
        } catch (ClassNotFoundException ignored) {
            // This isn't an Android runtime.
        } catch (NoSuchMethodException ignored) {
            // This isn't Android 2.3 or better.
        }
    }

    // Cf. https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
    private static String[] getProtocols() {
        // prefer TLSv1.2 if supported by platform
        if (Build.VERSION.SDK_INT < TLS12_ENABLED_API_LEVEL) {
            return new String[]{"TLSv1"};
        } else {
            return new String[]{"TLSv1.2"};
        }
    }

    // for debugging
    private static class LoggingHandshakeCompletedListener implements HandshakeCompletedListener {
        @Override
        public void handshakeCompleted(HandshakeCompletedEvent event) {
            SSLSession session = event.getSession();
            String protocol = session.getProtocol();
            String cipherSuite = session.getCipherSuite();

            Log.d(TAG, "Handshake completed", new Throwable("This is not Error."));
            Log.d(TAG, String.format("Connected with: %s/%s", protocol, cipherSuite));
            String peerName = null;

            try {
                peerName = session.getPeerPrincipal().getName();
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, String.format("Peer name: %s\n", peerName));
        }
    }

    // noop if socket doesn't support SNI
    private void setHostname(Socket socket, String host) {
        if (!openSslSocketClass.isInstance(socket) || setHostnameMethod == null) {
            // SNI not supported by platform
            // or, Socket instance is not a native platform class
            return;
        }

        try {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, String.format("setting hostname [%s] on socket %s", host, socket.getClass().getName()));
            }
            setHostnameMethod.invoke(socket, host);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        Socket delegateSocket = sslSocketFactory.createSocket(s, host, port, autoClose);
        setHostname(delegateSocket, host);

        return wrapSocket(delegateSocket);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket delegateSocket = sslSocketFactory.createSocket(host, port);
        setHostname(delegateSocket, host);

        return wrapSocket(delegateSocket);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return wrapSocket(sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return wrapSocket(sslSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        Socket delegateSocket = sslSocketFactory.createSocket(host, port, localHost, localPort);
        setHostname(delegateSocket, host);

        return wrapSocket(delegateSocket);
    }

    @Override
    public Socket createSocket() throws IOException {
        return wrapSocket(sslSocketFactory.createSocket());
    }

    @Override
    public String[] getDefaultCipherSuites() {
        if (removeUnsafeCiphers) {
            return removeUnsafeCiphers(sslSocketFactory.getDefaultCipherSuites());
        }

        return sslSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        if (removeUnsafeCiphers) {
            return removeUnsafeCiphers(sslSocketFactory.getSupportedCipherSuites());
        }

        return sslSocketFactory.getSupportedCipherSuites();
    }

    public Socket wrapSocket(Socket socket) {
        if (socket instanceof SSLSocket) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "create wrapped socket", new Throwable("This is not Error."));
            }

            SSLSocket sslSock = (SSLSocket) socket;
            sslSock.setEnabledProtocols(getProtocols());
            if (removeUnsafeCiphers) {
                String[] safeCiphers = removeUnsafeCiphers(sslSock.getEnabledCipherSuites());
                sslSock.setEnabledCipherSuites(safeCiphers);
            }
            socket = new NoSSLv3SSLSocket(sslSock);

            if (BuildConfig.DEBUG) {
                ((SSLSocket) socket).addHandshakeCompletedListener(new LoggingHandshakeCompletedListener());
            }
        }

        return socket;
    }

    private static String[] removeUnsafeCiphers(String[] ciphers) {
        List<String> safeCiphers = new ArrayList<String>(Arrays.asList(ciphers));
        for (String c : ciphers) {
            for (String unsafe : UNSAFE_CIPHERS) {
                if (c.contains(unsafe)) {
                    //Log.d(TAG, "Removing: " + c);
                    safeCiphers.remove(c);
                }
            }
        }

        return safeCiphers.toArray(new String[safeCiphers.size()]);
    }

    private static class NoSSLv3SSLSocket extends DelegateSSLSocket {

        private NoSSLv3SSLSocket(SSLSocket delegate) {
            super(delegate);

        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "setEnabledProtocols called with: " + Arrays.asList(protocols));
            }

            if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {
                List<String> enabledProtocols = new ArrayList<String>(Arrays.asList(delegate.getEnabledProtocols()));
                if (enabledProtocols.size() > 1) {
                    enabledProtocols.remove("SSLv3");
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "**** Removed SSLv3 from enabled protocols");
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "*** SSL stuck with protocol available for " + String.valueOf(enabledProtocols));
                    }
                }
                protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
            }

            super.setEnabledProtocols(protocols);
        }
    }

    private static class DelegateSSLSocket extends SSLSocket {

        protected final SSLSocket delegate;

        DelegateSSLSocket(SSLSocket delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return delegate.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] suites) {
            delegate.setEnabledCipherSuites(suites);
        }

        @Override
        public String[] getSupportedProtocols() {
            return delegate.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return delegate.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
            delegate.setEnabledProtocols(protocols);
        }

        @Override
        public SSLSession getSession() {
            return delegate.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
            delegate.addHandshakeCompletedListener(listener);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
            delegate.removeHandshakeCompletedListener(listener);
        }

        @Override
        public void startHandshake() throws IOException {
            delegate.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean mode) {
            delegate.setUseClientMode(mode);
        }

        @Override
        public boolean getUseClientMode() {
            return delegate.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean need) {
            delegate.setNeedClientAuth(need);
        }

        @Override
        public void setWantClientAuth(boolean want) {
            delegate.setWantClientAuth(want);
        }

        @Override
        public boolean getNeedClientAuth() {
            return delegate.getNeedClientAuth();
        }

        @Override
        public boolean getWantClientAuth() {
            return delegate.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean flag) {
            delegate.setEnableSessionCreation(flag);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return delegate.getEnableSessionCreation();
        }

        @Override
        public void bind(SocketAddress localAddr) throws IOException {
            delegate.bind(localAddr);
        }

        @Override
        public synchronized void close() throws IOException {
            delegate.close();
        }

        @Override
        public void connect(SocketAddress remoteAddr) throws IOException {
            delegate.connect(remoteAddr);
        }

        @Override
        public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
            delegate.connect(remoteAddr, timeout);
        }

        @Override
        public SocketChannel getChannel() {
            return delegate.getChannel();
        }

        @Override
        public InetAddress getInetAddress() {
            return delegate.getInetAddress();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return delegate.getInputStream();
        }

        @Override
        public boolean getKeepAlive() throws SocketException {
            return delegate.getKeepAlive();
        }

        @Override
        public InetAddress getLocalAddress() {
            return delegate.getLocalAddress();
        }

        @Override
        public int getLocalPort() {
            return delegate.getLocalPort();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return delegate.getLocalSocketAddress();
        }

        @Override
        public boolean getOOBInline() throws SocketException {
            return delegate.getOOBInline();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return delegate.getOutputStream();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException {
            return delegate.getReceiveBufferSize();
        }

        @Override
        public SocketAddress getRemoteSocketAddress() {
            return delegate.getRemoteSocketAddress();
        }

        @Override
        public boolean getReuseAddress() throws SocketException {
            return delegate.getReuseAddress();
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException {
            return delegate.getSendBufferSize();
        }

        @Override
        public int getSoLinger() throws SocketException {
            return delegate.getSoLinger();
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException {
            return delegate.getSoTimeout();
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return delegate.getTcpNoDelay();
        }

        @Override
        public int getTrafficClass() throws SocketException {
            return delegate.getTrafficClass();
        }

        @Override
        public boolean isBound() {
            return delegate.isBound();
        }

        @Override
        public boolean isClosed() {
            return delegate.isClosed();
        }

        @Override
        public boolean isConnected() {
            return delegate.isConnected();
        }

        @Override
        public boolean isInputShutdown() {
            return delegate.isInputShutdown();
        }

        @Override
        public boolean isOutputShutdown() {
            return delegate.isOutputShutdown();
        }

        @Override
        public void sendUrgentData(int value) throws IOException {
            delegate.sendUrgentData(value);
        }

        @Override
        public void setKeepAlive(boolean keepAlive) throws SocketException {
            delegate.setKeepAlive(keepAlive);
        }

        @Override
        public void setOOBInline(boolean oobinline) throws SocketException {
            delegate.setOOBInline(oobinline);
        }

        @Override
        public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
            delegate.setPerformancePreferences(connectionTime, latency, bandwidth);
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException {
            delegate.setReceiveBufferSize(size);
        }

        @Override
        public void setReuseAddress(boolean reuse) throws SocketException {
            delegate.setReuseAddress(reuse);
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException {
            delegate.setSendBufferSize(size);
        }

        @Override
        public void setSoLinger(boolean on, int timeout) throws SocketException {
            delegate.setSoLinger(on, timeout);
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException {
            delegate.setSoTimeout(timeout);
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException {
            delegate.setTcpNoDelay(on);
        }

        @Override
        public void setTrafficClass(int value) throws SocketException {
            delegate.setTrafficClass(value);
        }

        @Override
        public void shutdownInput() throws IOException {
            delegate.shutdownInput();
        }

        @Override
        public void shutdownOutput() throws IOException {
            delegate.shutdownOutput();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }
    }
}
