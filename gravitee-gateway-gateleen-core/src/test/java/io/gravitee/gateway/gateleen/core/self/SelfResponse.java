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
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.proxy.ProxyResponse;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class SelfResponse implements Response, ProxyResponse, Handler<Response> {

    private int status;
    private HttpHeaders headers = new HttpHeaders();
    private Handler<Buffer> bodyHandler;
    private Handler<Void> endHandler;
    private Handler<ProxyResponse> responseHandler;

    @Override
    public Response status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public Response write(Buffer buffer) {
        this.bodyHandler.handle(buffer);
        return this;
    }

    @Override
    public void end() {
        this.endHandler.handle(null);
    }

    @Override
    public ProxyResponse bodyHandler(Handler<Buffer> handler) {
        this.bodyHandler = handler;
        return this;
    }

    @Override
    public ProxyResponse endHandler(Handler<Void> handler) {
        this.endHandler = handler;
        return this;
    }

    @Override
    public void handle(Response response) {
        responseHandler.handle(this);
    }

    public void setResponseHandler(Handler<ProxyResponse> responseHandler) {
        this.responseHandler = responseHandler;
    }

}
