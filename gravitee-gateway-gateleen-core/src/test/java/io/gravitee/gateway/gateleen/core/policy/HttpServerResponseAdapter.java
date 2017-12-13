/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.gateleen.core.policy;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.gravitee.gateway.api.stream.ReadStream;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class HttpServerResponseAdapter implements HttpServerResponse {

    private int status;
    private MultiMap headers = new CaseInsensitiveHeaders();
    private Queue<Buffer> bufferQueue = new LinkedList<>();
    private io.gravitee.gateway.api.handler.Handler<io.gravitee.gateway.api.buffer.Buffer> bodyHandler;
    private io.gravitee.gateway.api.handler.Handler<Void> endHandler;
    private Handler<Void> vertxEndHandler;
    private boolean paused;
    private boolean ended;
    private boolean ready;
    private Handler<Void> drainHandler;
    private int bytesWritten = 0;
    private io.gravitee.gateway.api.handler.Handler<ProxyResponse> proxyResponseHandler;

    public HttpServerResponseAdapter(io.gravitee.gateway.api.handler.Handler<ProxyResponse> invoker) {
        proxyResponseHandler = invoker;
    }

    @Override
    public HttpServerResponse exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    @Override
    public HttpServerResponse write(Buffer data) {
        ready();
        bytesWritten += data.length();
        if (paused) {
            bufferQueue.add(data);
        } else {
            bodyHandler.handle(io.gravitee.gateway.api.buffer.Buffer.buffer(data.getBytes()));
        }
        return this;
    }

    @Override
    public HttpServerResponse setWriteQueueMaxSize(int maxSize) {
        return this;
    }

    @Override
    public HttpServerResponse drainHandler(Handler<Void> handler) {
        drainHandler = handler;
        return this;
    }

    @Override
    public int getStatusCode() {
        return status;
    }

    @Override
    public HttpServerResponse setStatusCode(int statusCode) {
        this.status = statusCode;
        return this;
    }

    @Override
    public String getStatusMessage() {
        return null;
    }

    @Override
    public HttpServerResponse setStatusMessage(String statusMessage) {
        return this;
    }

    @Override
    public HttpServerResponse setChunked(boolean chunked) {
        return this;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public MultiMap headers() {
        return headers;
    }

    @Override
    public HttpServerResponse putHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public HttpServerResponse putHeader(CharSequence name, CharSequence value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public HttpServerResponse putHeader(String name, Iterable<String> values) {
        headers.add(name, values);
        return this;
    }

    @Override
    public HttpServerResponse putHeader(CharSequence name, Iterable<CharSequence> values) {
        headers.add(name, values);
        return this;
    }

    @Override
    public MultiMap trailers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse putTrailer(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse putTrailer(CharSequence name, CharSequence value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse putTrailer(String name, Iterable<String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse putTrailer(CharSequence name, Iterable<CharSequence> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse closeHandler(@Nullable Handler<Void> handler) {
        return this;
    }

    @Override
    public HttpServerResponse endHandler(@Nullable Handler<Void> handler) {
        vertxEndHandler = handler;
        return this;
    }

    @Override
    public HttpServerResponse write(String chunk, String enc) {
        return write(Buffer.buffer(chunk, enc));
    }

    @Override
    public HttpServerResponse write(String chunk) {
        return write(Buffer.buffer(chunk));
    }

    @Override
    public HttpServerResponse writeContinue() {
        return this;
    }

    @Override
    public void end(String chunk) {
        write(chunk);
        end();
    }

    @Override
    public void end(String chunk, String enc) {
        write(chunk, enc);
        end();
    }

    @Override
    public void end(Buffer chunk) {
        write(chunk);
        end();
    }

    @Override
    public void end() {
        ready();
        ended = true;
        if (endHandler != null) {
            endHandler.handle(null);
        }
        if (vertxEndHandler != null) {
            vertxEndHandler.handle(null);
        }
    }

    @Override
    public HttpServerResponse sendFile(String filename, long offset, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse sendFile(String filename, long offset, long length, Handler<AsyncResult<Void>> resultHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        end();
    }

    @Override
    public boolean ended() {
        return ended;
    }

    @Override
    public boolean closed() {
        return ended;
    }

    @Override
    public boolean headWritten() {
        return true;
    }

    @Override
    public HttpServerResponse headersEndHandler(@Nullable Handler<Void> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse bodyEndHandler(@Nullable Handler<Void> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long bytesWritten() {
        return bytesWritten;
    }

    @Override
    public int streamId() {
        return -1;
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String host, String path, Handler<AsyncResult<HttpServerResponse>> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String path, MultiMap headers, Handler<AsyncResult<HttpServerResponse>> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String path, Handler<AsyncResult<HttpServerResponse>> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String host, String path, MultiMap headers, Handler<AsyncResult<HttpServerResponse>> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset(long code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpServerResponse writeCustomFrame(int type, int flags, Buffer payload) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean writeQueueFull() {
        return paused;
    }

    private void start() {
        if (drainHandler != null) {
            drainHandler.handle(null);
        }
        flush();
        paused = false;
    }

    private void stop() {
        paused = true;
    }

    private void ready() {
        if (!ready) {
            proxyResponseHandler.handle(new ProxyResponse() {
                @Override
                public int status() {
                    return getStatusCode();
                }

                @Override
                public HttpHeaders headers() {
                    return headers.entries().stream().collect(HttpHeaders::new, (h, e) -> h.add(e.getKey(), e.getValue()), HttpHeaders::putAll);
                }

                @Override
                public ProxyResponse bodyHandler(io.gravitee.gateway.api.handler.Handler<io.gravitee.gateway.api.buffer.Buffer> handler) {
                    bodyHandler = handler;
                    start();
                    return this;
                }

                @Override
                public ProxyResponse endHandler(io.gravitee.gateway.api.handler.Handler<Void> handler) {
                    if (ended) {
                        handler.handle(null);
                    } else {
                        endHandler = handler;
                    }
                    return this;
                }

                @Override
                public ProxyResponse pause() {
                    stop();
                    return this;
                }

                @Override
                public ProxyResponse resume() {
                    start();
                    return this;
                }
            });
            ready = true;
        }
    }

    private void flush() {
        bufferQueue.forEach(this::write);
    }
}
