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
package io.gravitee.gateway.gateleen.core.policy;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

import java.util.Queue;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class HttpServerResponseAdapter implements HttpServerResponse {

    private int status;
    private MultiMap headers = new CaseInsensitiveHeaders();
    private Queue<Buffer> data;


    @Override
    public HttpServerResponse exceptionHandler(Handler<Throwable> handler) {
        return null;
    }

    @Override
    public HttpServerResponse write(Buffer data) {
        return null;
    }

    @Override
    public HttpServerResponse setWriteQueueMaxSize(int maxSize) {
        return null;
    }

    @Override
    public HttpServerResponse drainHandler(Handler<Void> handler) {
        return null;
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
        return null;
    }

    @Override
    public HttpServerResponse setChunked(boolean chunked) {
        return null;
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
        return null;
    }

    @Override
    public HttpServerResponse putHeader(CharSequence name, CharSequence value) {
        return null;
    }

    @Override
    public HttpServerResponse putHeader(String name, Iterable<String> values) {
        return null;
    }

    @Override
    public HttpServerResponse putHeader(CharSequence name, Iterable<CharSequence> values) {
        return null;
    }

    @Override
    public MultiMap trailers() {
        return null;
    }

    @Override
    public HttpServerResponse putTrailer(String name, String value) {
        return null;
    }

    @Override
    public HttpServerResponse putTrailer(CharSequence name, CharSequence value) {
        return null;
    }

    @Override
    public HttpServerResponse putTrailer(String name, Iterable<String> values) {
        return null;
    }

    @Override
    public HttpServerResponse putTrailer(CharSequence name, Iterable<CharSequence> value) {
        return null;
    }

    @Override
    public HttpServerResponse closeHandler(@Nullable Handler<Void> handler) {
        return null;
    }

    @Override
    public HttpServerResponse endHandler(@Nullable Handler<Void> handler) {
        return null;
    }

    @Override
    public HttpServerResponse write(String chunk, String enc) {
        return null;
    }

    @Override
    public HttpServerResponse write(String chunk) {
        return null;
    }

    @Override
    public HttpServerResponse writeContinue() {
        return null;
    }

    @Override
    public void end(String chunk) {

    }

    @Override
    public void end(String chunk, String enc) {

    }

    @Override
    public void end(Buffer chunk) {

    }

    @Override
    public void end() {

    }

    @Override
    public HttpServerResponse sendFile(String filename, long offset, long length) {
        return null;
    }

    @Override
    public HttpServerResponse sendFile(String filename, long offset, long length, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean ended() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public boolean headWritten() {
        return false;
    }

    @Override
    public HttpServerResponse headersEndHandler(@Nullable Handler<Void> handler) {
        return null;
    }

    @Override
    public HttpServerResponse bodyEndHandler(@Nullable Handler<Void> handler) {
        return null;
    }

    @Override
    public long bytesWritten() {
        return 0;
    }

    @Override
    public int streamId() {
        return 0;
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String host, String path, Handler<AsyncResult<HttpServerResponse>> handler) {
        return null;
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String path, MultiMap headers, Handler<AsyncResult<HttpServerResponse>> handler) {
        return null;
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String path, Handler<AsyncResult<HttpServerResponse>> handler) {
        return null;
    }

    @Override
    public HttpServerResponse push(HttpMethod method, String host, String path, MultiMap headers, Handler<AsyncResult<HttpServerResponse>> handler) {
        return null;
    }

    @Override
    public void reset(long code) {

    }

    @Override
    public HttpServerResponse writeCustomFrame(int type, int flags, Buffer payload) {
        return null;
    }

    @Override
    public boolean writeQueueFull() {
        return false;
    }


    public void setProxyResponseHandler(io.gravitee.gateway.api.handler.Handler<ProxyResponse> handler) {
    }

    private ProxyResponse getProxyResponse() {
        return new ProxyResponse() {
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
                return this;
            }

            @Override
            public ProxyResponse endHandler(io.gravitee.gateway.api.handler.Handler<Void> handler) {
                return this;
            }


        };
    }
}
