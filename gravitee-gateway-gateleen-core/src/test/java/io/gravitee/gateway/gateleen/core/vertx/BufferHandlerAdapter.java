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

import io.gravitee.gateway.api.buffer.Buffer;
import io.vertx.core.Handler;

/**
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class BufferHandlerAdapter extends AbstractHandlerAdapter<Buffer, io.vertx.core.buffer.Buffer> {

    public BufferHandlerAdapter(Handler<io.vertx.core.buffer.Buffer> handler) {
        super(handler);
    }

    @Override
    protected io.vertx.core.buffer.Buffer convert(Buffer buffer) {
        Object nativeBuffer = buffer.getNativeBuffer();
        if(nativeBuffer instanceof io.vertx.core.buffer.Buffer) {
            return (io.vertx.core.buffer.Buffer)nativeBuffer;
        } else {
            return io.vertx.core.buffer.Buffer.buffer(buffer.getBytes());
        }
    }
}
