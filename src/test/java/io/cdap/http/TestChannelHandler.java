/*
 * Copyright © 2014-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Test ChannelHandler that adds a default header to every response.
 */
public class TestChannelHandler extends ChannelOutboundHandlerAdapter {

  static final String HEADER_FIELD = "testHeaderField";
  static final String HEADER_VALUE = "testHeaderValue";

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (!(msg instanceof HttpResponse)) {
      super.write(ctx, msg, promise);
      return;
    }
    HttpResponse response = (HttpResponse) msg;
    response.headers().add(HEADER_FIELD, HEADER_VALUE);
    super.write(ctx, response, promise);

  }
}
