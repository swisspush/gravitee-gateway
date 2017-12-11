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

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Invoker;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.proxy.ProxyConnection;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.gateway.api.stream.WriteStream;
import io.gravitee.gateway.reactor.Reactor;

/**
 * An invoker performing an internal request through the reactor.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class SelfInvoker implements Invoker {

    private Reactor reactor;

    public SelfInvoker(Reactor reactor) {
        this.reactor = reactor;
    }

    protected Request getRequest(Request serverRequest) {
        return serverRequest;
    }

    protected ReadStream getReadStream(ReadStream<Buffer> serverRequestStream) {
        return serverRequestStream;
    }

    @Override
    public Request invoke(ExecutionContext executionContext, Request serverRequest, ReadStream<Buffer> serverRequestStream, Handler<ProxyConnection> connectionHandler) {

        SelfResponse selfResponse = new SelfResponse();

        ProxyConnection proxyConnection = new ProxyConnection() {

            @Override
            public WriteStream<Buffer> write(Buffer buffer) {
                return null;
            }

            @Override
            public void end() {

            }

            @Override
            public ProxyConnection cancel() {
                return null;
            }

            @Override
            public ProxyConnection exceptionHandler(Handler<Throwable> exceptionHandler) {
                return this;
            }

            @Override
            public ProxyConnection responseHandler(Handler<ProxyResponse> handler) {
                selfResponse.setResponseHandler(handler);
                return this;
            }
        };

        connectionHandler.handle(proxyConnection);
        reactor.route(
                new SelfRequest(serverRequest, serverRequestStream),
                selfResponse,
                selfResponse);

        return serverRequest;
    }
}
