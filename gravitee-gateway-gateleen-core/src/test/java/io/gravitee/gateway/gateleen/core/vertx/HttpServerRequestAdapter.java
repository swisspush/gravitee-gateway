/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.gateleen.core.vertx;

import io.gravitee.gateway.api.Request;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

/**
 * Adapts a gravitee request as a vertx request.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class HttpServerRequestAdapter implements HttpServerRequest {
    private Request request;
    private BufferHandlerAdapter bufferHandler;
    private VoidHandlerAdapter endHandler;

    public HttpServerRequestAdapter(Request request) {
        this.request = request;
    }

    public BufferHandlerAdapter getBufferHandler() {
        return bufferHandler;
    }

    public VoidHandlerAdapter getEndHandler() {
        return endHandler;
    }

    @Override
    public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    @Override
    public HttpServerRequest handler(Handler<Buffer> handler) {
        this.bufferHandler = new BufferHandlerAdapter(handler);
        return this;
    }

    @Override
    public HttpServerRequest pause() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerRequest resume() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerRequest endHandler(Handler<Void> handler) {
        this.endHandler = new VoidHandlerAdapter(handler);
        return this;
    }

    @Override
    public HttpVersion version() {
        return HttpVersion.valueOf(request.version().name());
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.valueOf(method().name());
    }

    @Override
    public String rawMethod() {
        return request.method().name();
    }

    @Override
    public boolean isSSL() {
        return false;
    }

    @Override
    public
    String scheme() {
        return "http";
    }

    @Override
    public String uri() {
        return request.uri();
    }

    @Override
    public
    String path() {
        return request.path();
    }

    @Override
    public
    String query() {
        return "";
    }

    @Override
    public
    String host() {
        return "localhost";
    }

    @Override
    public HttpServerResponse response() {
        // TODO
        return null;
    }

    @Override
    public MultiMap headers() {
        return request.headers().entrySet().stream().collect(
                CaseInsensitiveHeaders::new,
                (headers, entry) -> headers.add(entry.getKey(), entry.getValue()),
                CaseInsensitiveHeaders::addAll);
    }

    @Override
    public
    String getHeader(String s) {
        return request.headers().getFirst(s);
    }

    @Override
    public String getHeader(CharSequence charSequence) {
        return request.headers().getFirst(charSequence.toString());
    }

    @Override
    public MultiMap params() {
        return request.parameters().entrySet().stream().collect(
                CaseInsensitiveHeaders::new,
                (headers, entry) -> headers.add(entry.getKey(), entry.getValue()),
                CaseInsensitiveHeaders::addAll);
    }

    @Override
    public
    String getParam(String s) {
        return request.parameters().getFirst(s);
    }

    @Override
    public SocketAddress remoteAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SocketAddress localAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLSession sslSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
        return new X509Certificate[0];
    }

    @Override
    public String absoluteURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NetSocket netSocket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerRequest setExpectMultipart(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExpectMultipart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MultiMap formAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public
    String getFormAttribute(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServerWebSocket upgrade() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerRequest customFrameHandler(Handler<HttpFrame> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpConnection connection() {
        throw new UnsupportedOperationException();
    }
}
