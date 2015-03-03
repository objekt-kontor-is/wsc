package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PipelineMultiplexerChannel extends AbstractChannel {

    private final AbstractChannel origin;

    public PipelineMultiplexerChannel(Channel origin) {
        super(origin.parent());
        this.origin = (AbstractChannel) origin;
    }

    @Override
    public ChannelFuture write(Object message, ChannelPromise promise) {
        return origin.pipeline().write(message, new PromiseDelegate(promise));
    }

    @Override
    public Channel flush() {
        origin.pipeline().flush();
        return this;
    }
    @Override
    public ChannelConfig config() {
        return origin.config();
    }

    @Override
    public boolean isOpen() {
        return origin.isOpen();
    }

    @Override
    public boolean isActive() {
        return origin.isActive();
    }

    @Override
    public ChannelMetadata metadata() {
        return origin.metadata();
    }

    @Override
    public EventLoop eventLoop() {
        return origin.eventLoop();
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new MultiplexerUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return origin.isCompatible(loop);
    }

    @Override
    protected SocketAddress localAddress0() {
        return origin.localAddress0();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return origin.remoteAddress0();
    }

    @Override
    protected void doRegister() throws Exception {
        origin.doRegister();
    }

    @Override
    protected void doDeregister() throws Exception {
        origin.doDeregister();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        origin.doBind(localAddress);
    }

    @Override
    protected void doDisconnect() throws Exception {
        origin.doDisconnect();
    }

    @Override
    protected void doClose() throws Exception {
        origin.doClose();
    }

    @Override
    protected void doBeginRead() throws Exception {
        origin.doBeginRead();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        origin.doWrite(in);
    }

    private class MultiplexerUnsafe extends AbstractUnsafe {

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            origin.connect(remoteAddress, localAddress, promise);
        }
    }

    private class PromiseDelegate implements ChannelPromise {

        private final ChannelPromise delegate;

        public PromiseDelegate(ChannelPromise delegate) {
            this.delegate = delegate;
        }

        @Override
        public Channel channel() {
            return origin;
        }

        @Override
        public boolean isSuccess() {
            return delegate.isSuccess();
        }

        @Override
        public ChannelPromise setSuccess(Void result) {
            return delegate.setSuccess(result);
        }

        @Override
        public boolean trySuccess(Void result) {
            return delegate.trySuccess(result);
        }

        @Override
        public ChannelPromise setSuccess() {
            return delegate.setSuccess();
        }

        @Override
        public boolean trySuccess() {
            return delegate.trySuccess();
        }

        @Override
        public boolean isCancellable() {
            return delegate.isCancellable();
        }

        @Override
        public ChannelPromise setFailure(Throwable cause) {
            return delegate.setFailure(cause);
        }

        @Override
        public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
            return delegate.addListener(listener);
        }

        @Override
        public Throwable cause() {
            return delegate.cause();
        }

        @Override
        public ChannelPromise addListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends Future<? super Void>>... listeners) {
            return delegate.addListeners(listeners);
        }

        @Override
        public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
            return delegate.removeListener(listener);
        }

        @Override
        public ChannelPromise removeListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends Future<? super Void>>... listeners) {
            return delegate.removeListeners(listeners);
        }

        @Override
        public boolean tryFailure(Throwable cause) {
            return delegate.tryFailure(cause);
        }

        @Override
        public ChannelPromise sync() throws InterruptedException {
            return delegate.sync();
        }

        @Override
        public ChannelPromise syncUninterruptibly() {
            return delegate.syncUninterruptibly();
        }

        @Override
        public ChannelPromise await() throws InterruptedException {
            return delegate.await();
        }

        @Override
        public ChannelPromise awaitUninterruptibly() {
            return delegate.awaitUninterruptibly();
        }

        @Override
        public boolean setUncancellable() {
            return delegate.setUncancellable();
        }

        @Override
        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return delegate.await(timeout, unit);
        }

        @Override
        public boolean isCancelled() {
            return delegate.isCancelled();
        }

        @Override
        public boolean await(long timeoutMillis) throws InterruptedException {
            return delegate.await(timeoutMillis);
        }

        @Override
        public boolean isDone() {
            return delegate.isDone();
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return delegate.get();
        }

        @Override
        public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
            return delegate.awaitUninterruptibly(timeout, unit);
        }

        @Override
        public boolean awaitUninterruptibly(long timeoutMillis) {
            return delegate.awaitUninterruptibly(timeoutMillis);
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.get(timeout, unit);
        }

        @Override
        public Void getNow() {
            return delegate.getNow();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return delegate.cancel(mayInterruptIfRunning);
        }
    }
}
