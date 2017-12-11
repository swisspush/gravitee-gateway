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

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Invoker;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.proxy.ProxyConnection;
import io.gravitee.gateway.api.stream.ReadStream;
import io.gravitee.gateway.gateleen.core.policy.BreakPolicyInvoker;
import io.vertx.core.http.HttpServerRequest;

import java.util.function.Function;

/**
 * A break policy invoker that delegates to a Vert.x handler.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class VertxBreakPolicyInvoker implements BreakPolicyInvoker {

    private Function<HttpServerRequest, Boolean> handler;
    private Invoker wrappedInvoker;

    public VertxBreakPolicyInvoker(Function<HttpServerRequest, Boolean> handler, Invoker wrappedInvoker) {
        this.handler = handler;
        this.wrappedInvoker = wrappedInvoker;
    }

    @Override
    public boolean isBreaking(Request request) {
        return handler.apply(new HttpServerRequestAdapter(request));
    }

    @Override
    public Request invoke(ExecutionContext executionContext,
                          Request request,
                          ReadStream<Buffer> readStream,
                          io.gravitee.gateway.api.handler.Handler<ProxyConnection> handler) {



        return request;
    }
}
