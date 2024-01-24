/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation;

import com.google.auto.service.AutoService;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@AutoService(AutoConfigurationCustomizerProvider.class)
public class GlovoOTelExtension implements AutoConfigurationCustomizerProvider {

  @Override
  public void customize(AutoConfigurationCustomizer customizer) {
    customizer.addTracerProviderCustomizer((t, u) -> t.addSpanProcessor(new TraceAnalyzer()));
  }
}

class TraceAnalyzer implements SpanProcessor {

  private static final AttributeKey<Long> QUERY_COUNT_KEY = AttributeKey.longKey("sql_query_count");
  private static final AttributeKey<String> DB_SYSTEM_KEY = AttributeKey.stringKey("db.system");

  private final Map<String, Pair<Long, Instant>> sqlQueryCounters = new HashMap<>();

  @Override
  public void onStart(Context parentContext, ReadWriteSpan span) {
    span.setAttribute("visited_by_dani", "true");
    Span rootSpan = LocalRootSpan.fromContext(Context.current());
    rootSpan.setAttribute("labeled_root_onStart", "true");
  }

  @Override
  public boolean isStartRequired() {
    return true;
  }

  @Override
  public void onEnd(ReadableSpan span) {
    Span rootSpan = LocalRootSpan.fromContext(Context.current());
    rootSpan.setAttribute("labeled_root_onEnd", "true");
    if ("mysql".equals(span.getAttribute(DB_SYSTEM_KEY))) {
      if (!sqlQueryCounters.containsKey(rootSpan.getSpanContext().getTraceId())) {
        sqlQueryCounters.put(rootSpan.getSpanContext().getTraceId(), Pair.of(0L, Instant.now()));
        // figure a way for this not to grow out of indefinitely/out of control
      }
      Pair<Long, Instant> cacheItem = sqlQueryCounters.get(rootSpan.getSpanContext().getTraceId());
      Long currentQueryCount = cacheItem.first + 1;
      sqlQueryCounters.put(
          rootSpan.getSpanContext().getTraceId(),
          Pair.of(
              currentQueryCount, cacheItem.second)); // keep created? store both created + updated?
      rootSpan.setAttribute(QUERY_COUNT_KEY, currentQueryCount);
    }
  }

  @Override
  public boolean isEndRequired() {
    return true;
  }
}

class Pair<T, U> {
  public final T first;
  public final U second;

  private Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  public static <T, U> Pair<T, U> of(T first, U second) {
    return new Pair<>(first, second);
  }
}
