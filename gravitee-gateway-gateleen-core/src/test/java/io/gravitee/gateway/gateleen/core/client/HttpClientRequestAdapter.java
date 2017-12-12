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
package io.gravitee.gateway.gateleen.core.client;

import io.gravitee.gateway.api.Request;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class HttpClientRequestAdapter implements HttpClientRequest {
    @Override
    public HttpClientRequest exceptionHandler(Handler<Throwable> handler) {
        return null;
    }

    @Override
    public HttpClientRequest write(Buffer data) {
        return null;
    }

    @Override
    public HttpClientRequest setWriteQueueMaxSize(int maxSize) {
        return null;
    }

    @Override
    public HttpClientRequest drainHandler(Handler<Void> handler) {
        return null;
    }

    @Override
    public HttpClientRequest handler(Handler<HttpClientResponse> handler) {
        return null;
    }

    @Override
    public HttpClientRequest pause() {
        return null;
    }

    @Override
    public HttpClientRequest resume() {
        return null;
    }

    @Override
    public HttpClientRequest endHandler(Handler<Void> endHandler) {
        return null;
    }

    @Override
    public HttpClientRequest setFollowRedirects(boolean followRedirects) {
        return null;
    }

    @Override
    public HttpClientRequest setChunked(boolean chunked) {
        return null;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public HttpMethod method() {
        return null;
    }

    @Override
    public String getRawMethod() {
        return null;
    }

    @Override
    public HttpClientRequest setRawMethod(String method) {
        return null;
    }

    @Override
    public String absoluteURI() {
        return null;
    }

    @Override
    public String uri() {
        return null;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String query() {
        return null;
    }

    @Override
    public HttpClientRequest setHost(String host) {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public MultiMap headers() {
        return null;
    }

    @Override
    public HttpClientRequest putHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpClientRequest putHeader(CharSequence name, CharSequence value) {
        return null;
    }

    @Override
    public HttpClientRequest putHeader(String name, Iterable<String> values) {
        return null;
    }

    @Override
    public HttpClientRequest putHeader(CharSequence name, Iterable<CharSequence> values) {
        return null;
    }

    @Override
    public HttpClientRequest write(String chunk) {
        return null;
    }

    @Override
    public HttpClientRequest write(String chunk, String enc) {
        return null;
    }

    @Override
    public HttpClientRequest continueHandler(@Nullable Handler<Void> handler) {
        return null;
    }

    @Override
    public HttpClientRequest sendHead() {
        return null;
    }

    @Override
    public HttpClientRequest sendHead(Handler<HttpVersion> completionHandler) {
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
    public HttpClientRequest setTimeout(long timeoutMs) {
        return null;
    }

    @Override
    public HttpClientRequest pushHandler(Handler<HttpClientRequest> handler) {
        return null;
    }

    @Override
    public boolean reset(long code) {
        return false;
    }

    @Override
    public HttpConnection connection() {
        return null;
    }

    @Override
    public HttpClientRequest connectionHandler(@Nullable Handler<HttpConnection> handler) {
        return null;
    }

    @Override
    public HttpClientRequest writeCustomFrame(int type, int flags, Buffer payload) {
        return null;
    }

    @Override
    public boolean writeQueueFull() {
        return false;
    }

    public Request getRequest() {
        return null;
    }
}
