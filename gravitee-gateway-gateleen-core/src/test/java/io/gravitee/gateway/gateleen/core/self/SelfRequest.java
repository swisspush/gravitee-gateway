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
package io.gravitee.gateway.gateleen.core.self;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.common.http.HttpVersion;
import io.gravitee.common.util.MultiValueMap;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.reporter.api.http.Metrics;

import java.time.Instant;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class SelfRequest implements Request {
    private Request serverRequest;
    private ReadStream<Buffer> serverRequestStream;

    public SelfRequest(Request serverRequest, ReadStream<Buffer> serverRequestStream) {
        this.serverRequest = serverRequest;
        this.serverRequestStream = serverRequestStream;
    }

    @Override
    public String id() {
        return serverRequest.id();
    }

    @Override
    public String transactionId() {
        return serverRequest.transactionId();
    }

    @Override
    public String uri() {
        return serverRequest.uri();
    }

    @Override
    public String path() {
        return serverRequest.path();
    }

    @Override
    public String pathInfo() {
        return serverRequest.pathInfo();
    }

    @Override
    public String contextPath() {
        return serverRequest.contextPath();
    }

    @Override
    public MultiValueMap<String, String> parameters() {
        return serverRequest.parameters();
    }

    @Override
    public HttpHeaders headers() {
        return serverRequest.headers();
    }

    @Override
    public HttpMethod method() {
        return serverRequest.method();
    }

    @Override
    public HttpVersion version() {
        return serverRequest.version();
    }

    @Override
    public Instant timestamp() {
        return serverRequest.timestamp();
    }

    @Override
    public String remoteAddress() {
        return serverRequest.remoteAddress();
    }

    @Override
    public String localAddress() {
        return serverRequest.remoteAddress();
    }

    @Override
    public Metrics metrics() {
        return serverRequest.metrics();
    }

    @Override
    public Request bodyHandler(Handler<Buffer> handler) {
        serverRequestStream.bodyHandler(handler);
        return this;
    }

    @Override
    public Request endHandler(Handler<Void> handler) {
        serverRequestStream.endHandler(handler);
        return this;
    }
}
