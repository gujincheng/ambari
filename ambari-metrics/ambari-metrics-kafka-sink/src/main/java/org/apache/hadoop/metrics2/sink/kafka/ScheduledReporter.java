/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.metrics2.sink.kafka;

import java.io.Closeable;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.ConsoleReporter;
import com.yammer.metrics.reporting.CsvReporter;

/**
 * The abstract base class for all scheduled reporters (i.e., reporters which
 * process a registry's metrics periodically).
 *
 * @see ConsoleReporter
 * @see CsvReporter
 * @see Slf4jReporter
 */
public abstract class ScheduledReporter implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(ScheduledReporter.class);

  /**
   * A simple named thread factory.
   */
  @SuppressWarnings("NullableProblems")
  private static class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    private NamedThreadFactory(String name) {
      final SecurityManager s = System.getSecurityManager();
      this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = "metrics-" + name + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
      final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      t.setDaemon(true);
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }
  }

  private static final AtomicInteger FACTORY_ID = new AtomicInteger();

  private final MetricsRegistry registry;
  private final ScheduledExecutorService executor;
  private final double durationFactor;
  private final String durationUnit;
  private final double rateFactor;
  private final String rateUnit;

  /**
   * Creates a new {@link ScheduledReporter} instance.
   *
   * @param registry
   *          the {@link com.codahale.metrics.MetricRegistry} containing the
   *          metrics this reporter will report
   * @param name
   *          the reporter's name
   * @param rateUnit
   *          a unit of time
   * @param durationUnit
   *          a unit of time
   */
  protected ScheduledReporter(MetricsRegistry registry, String name, TimeUnit rateUnit, TimeUnit durationUnit) {
    this(registry, rateUnit, durationUnit, Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name + '-'
        + FACTORY_ID.incrementAndGet())));
  }

  /**
   * Creates a new {@link ScheduledReporter} instance.
   *
   * @param registry
   *          the {@link com.codahale.metrics.MetricRegistry} containing the
   *          metrics this reporter will report
   * @param executor
   *          the executor to use while scheduling reporting of metrics.
   */
  protected ScheduledReporter(MetricsRegistry registry, TimeUnit rateUnit, TimeUnit durationUnit,
      ScheduledExecutorService executor) {
    this.registry = registry;
    this.executor = executor;
    this.rateFactor = rateUnit.toSeconds(1);
    this.rateUnit = calculateRateUnit(rateUnit);
    this.durationFactor = 1.0 / durationUnit.toNanos(1);
    this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
  }

  /**
   * Starts the reporter polling at the given period.
   *
   * @param period
   *          the amount of time between polls
   * @param unit
   *          the unit for {@code period}
   */
  public void start(long period, TimeUnit unit) {
    executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          report();
        } catch (RuntimeException ex) {
          LOG.error("RuntimeException thrown from {}#report. Exception was suppressed.", ScheduledReporter.this
              .getClass().getSimpleName(), ex);
        }
      }
    }, period, period, unit);
  }

  /**
   * Stops the reporter and shuts down its thread of execution.
   *
   * Uses the shutdown pattern from
   * http://docs.oracle.com/javase/7/docs/api/java
   * /util/concurrent/ExecutorService.html
   */
  public void stop() {
    executor.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
        executor.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
          System.err.println(getClass().getSimpleName() + ": ScheduledExecutorService did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Stops the reporter and shuts down its thread of execution.
   */
  @Override
  public void close() {
    stop();
  }

  /**
   * Report the current values of all metrics in the registry.
   */
  public void report() {
    synchronized (this) {
      report(registry.allMetrics().entrySet());
    }
  }

  /**
   * Called periodically by the polling thread. Subclasses should report all the
   * given metrics.
   *
   * @param metrics
   *          all the metrics in the registry
   */
  public abstract void report(Set<Entry<MetricName, Metric>> metrics);

  protected String getRateUnit() {
    return rateUnit;
  }

  protected String getDurationUnit() {
    return durationUnit;
  }

  protected double convertDuration(double duration) {
    return duration * durationFactor;
  }

  protected double convertRate(double rate) {
    return rate * rateFactor;
  }

  private String calculateRateUnit(TimeUnit unit) {
    final String s = unit.toString().toLowerCase(Locale.US);
    return s.substring(0, s.length() - 1);
  }
}
