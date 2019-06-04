/*
 * Copyright © 2017-2019 Cask Data, Inc.
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

package io.cdap.http.internal;

import io.cdap.http.HttpHandler;

/**
 * Contains information about {@link HttpHandler} method.
 */
public class HandlerInfo {
  private final String handlerName;
  private final String methodName;

  public HandlerInfo(String handlerName, String methodName) {
    this.handlerName = handlerName;
    this.methodName = methodName;
  }

  public String getHandlerName() {
    return handlerName;
  }

  public String getMethodName() {
    return methodName;
  }
}
