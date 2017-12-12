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

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Invoker;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.proxy.ProxyConnection;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.gateway.api.stream.WriteStream;

/**
 * An invoker that just streams back a proxy response.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class ProxyResponseInvoker implements Invoker, Handler<ProxyResponse> {

    private ProxyResponse response;
    private Handler<ProxyResponse> responseHandler;

    @Override
    public Request invoke(ExecutionContext executionContext,
                          Request request,
                          ReadStream<Buffer> readStream,
                          Handler<ProxyConnection> connectionHandler) {
        ProxyConnection proxyConnection = new ProxyConnection() {
            @Override
            public WriteStream<Buffer> write(Buffer buffer) {
                // ignore
                return this;
            }

            @Override
            public void end() {
                // ignore
            }

            @Override
            public ProxyConnection responseHandler(Handler<ProxyResponse> handler) {
                if(response != null) {
                    handler.handle(response);
                } else {
                    responseHandler = handler;
                }
                return this;
            }
        };
        connectionHandler.handle(proxyConnection);
        return request;
    }

    @Override
    public void handle(ProxyResponse proxyResponse) {
        if(responseHandler != null) {
            responseHandler.handle(proxyResponse);
        } else {
            response = response;
        }
    }
}
