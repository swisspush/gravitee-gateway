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
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.Response;
import io.gravitee.policy.api.PolicyChain;
import io.gravitee.policy.api.annotations.OnRequest;
import io.vertx.core.http.HttpServerRequest;

/**
 * A base class for policies delegating their job to an Vert.x request handler, optionally skipping next policies.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public abstract class AbstractVertxHandlerPolicy {

    protected abstract boolean handle(HttpServerRequest request);

    @OnRequest
    public void onRequest(Request request, Response response, PolicyChain policyChain, ExecutionContext executionContext) {
        // Skip if another response invoker has been registered
        if (!(executionContext.getAttribute(ExecutionContext.ATTR_INVOKER) instanceof ProxyResponseInvoker)) {
            HttpServerRequestAdapter requestAdapter = new HttpServerRequestAdapter(request);
            ProxyResponseInvoker invoker = new ProxyResponseInvoker();
            requestAdapter.setProxyResponseHandler(invoker);
            if (handle(requestAdapter)) {
                executionContext.setAttribute(ExecutionContext.ATTR_INVOKER, invoker);
                policyChain.doNext(request, response); // will be replaced by doBreak
            }
        } else {
            policyChain.doNext(request, response);
        }
    }
}
