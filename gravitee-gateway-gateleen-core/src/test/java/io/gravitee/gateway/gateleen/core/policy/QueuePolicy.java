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

import io.gravitee.gateway.gateleen.core.self.SelfInvoker;
import io.gravitee.gateway.gateleen.core.vertx.HttpClientAdapter;
import io.gravitee.gateway.gateleen.core.vertx.VertxBreakPolicyInvoker;
import io.vertx.core.http.HttpServerRequest;

import java.util.function.Function;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class QueuePolicy extends AbstractBreakPolicy {

    SelfInvoker invoker = new SelfInvoker(null);

    @Override
    protected BreakPolicyInvoker createInvoker() {
        return new VertxBreakPolicyInvoker(handler, invoker);
    }

    private static Function<HttpServerRequest, Boolean> handler =
            (httpServerRequest) -> {
                if (httpServerRequest.headers().contains("x-queue")) {
                    httpServerRequest.bodyHandler((buffer) -> {
                        new HttpClientAdapter();
                    });
                    return true;
                } else {
                    return false;
                }
            };
}
