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

/**
 * A base class for policies delegating their job to an invoker, skipping next policies.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public abstract class AbstractBreakPolicy {

    protected abstract BreakPolicyInvoker createInvoker();

    @OnRequest
    public void onRequest(Request request, Response response, PolicyChain policyChain, ExecutionContext executionContext) {
        // If not yet broken and break is needed for this request, installs the invoker and breaks.
        BreakPolicyInvoker invoker = createInvoker();
        if(!(executionContext.getAttribute(ExecutionContext.ATTR_INVOKER) instanceof BreakPolicyInvoker) &&
                invoker.isBreaking(request)) {
            executionContext.setAttribute(ExecutionContext.ATTR_INVOKER, invoker);
            policyChain.doNext(request, response); // will be replaced by doBreak
        } else {
            policyChain.doNext(request, response);
        }
    }
}
