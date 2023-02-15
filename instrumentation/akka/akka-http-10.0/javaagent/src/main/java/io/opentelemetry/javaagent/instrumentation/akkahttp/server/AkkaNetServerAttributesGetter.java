/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.akkahttp.server;

import akka.http.scaladsl.model.HttpRequest;
import io.opentelemetry.instrumentation.api.instrumenter.net.NetServerAttributesGetter;
import javax.annotation.Nullable;

// TODO (trask) capture net attributes?
class AkkaNetServerAttributesGetter implements NetServerAttributesGetter<HttpRequest> {

  @Nullable
  @Override
  public String getTransport(HttpRequest httpRequest) {
    return null;
  }

  @Nullable
  @Override
  public String getHostName(HttpRequest httpRequest) {
    return null;
  }

  @Nullable
  @Override
  public Integer getHostPort(HttpRequest httpRequest) {
    return null;
  }
}