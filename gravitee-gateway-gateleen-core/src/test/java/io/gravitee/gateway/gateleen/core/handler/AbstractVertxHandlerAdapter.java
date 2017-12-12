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
package io.gravitee.gateway.gateleen.core.handler;

import io.vertx.core.Handler;

import java.util.function.Function;

/**
 * Connects gravitee handlers to vertx handlers.
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public abstract class AbstractVertxHandlerAdapter<T, U> implements Function<T,U>, Handler<T> {

    private io.gravitee.gateway.api.handler.Handler<U> handler;

    public AbstractVertxHandlerAdapter(io.gravitee.gateway.api.handler.Handler<U> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(T t) {
        handler.handle(apply(t));
    }

}
