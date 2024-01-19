package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.config;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-rest-hibernate
 * %%
 * Copyright (C) 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;

public class McckCacheInterceptor extends CacheInterceptor implements CacheErrorHandler {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(McckCacheInterceptor.class);

	private static final long serialVersionUID = -4240425015720609606L;

	private volatile int failCount;
	private volatile boolean down;

	@Value("${mcck.spring.caching.max_fail_count:1}")
	private int maxFailCount = 1;

	@Value("${mcck.spring.caching.max_downtime_millis:10000}")
	private long maxDowntimeMillis;

	private long lastDowntimeMillis;

	public McckCacheInterceptor() {
		// noop
	}

	@Override
	protected void doClear(Cache cache, boolean immediate) {
		if (down) {
			if ((Instant.now().toEpochMilli() - lastDowntimeMillis) < maxDowntimeMillis) {
				return;
			}
			down = false;
			failCount = 0;
		}
		super.doClear(cache, immediate);
	}

	@Override
	protected void doEvict(Cache cache, Object key, boolean immediate) {
		if (down) {
			if ((Instant.now().toEpochMilli() - lastDowntimeMillis) < maxDowntimeMillis) {
				return;
			}
			down = false;
			failCount = 0;
		}
		super.doEvict(cache, key, immediate);
	}

	@Override
	protected ValueWrapper doGet(Cache cache, Object key) {
		if (down) {
			if ((Instant.now().toEpochMilli() - lastDowntimeMillis) < maxDowntimeMillis) {
				return null;
			}
			down = false;
			failCount = 0;
		}
		return super.doGet(cache, key);
	}

	@Override
	protected void doPut(Cache cache, Object key, Object value) {
		if (down) {
			if ((Instant.now().toEpochMilli() - lastDowntimeMillis) < maxDowntimeMillis) {
				return;
			}
			down = false;
			failCount = 0;
		}
		super.doPut(cache, key, value);
	}

	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		LOG.error("cache get error", exception);
		markDown();
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		LOG.error("cache evict error", exception);
		markDown();
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		LOG.error("cache clear error", exception);
		markDown();
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		LOG.error("cache put error", exception);
		markDown();
	}

	private void markDown() {
		if (down) {
			return;
		}
		failCount++;
		if (failCount >= maxFailCount) {
			down = true;
			long currDowntimeMillis = Instant.now().toEpochMilli();
			lastDowntimeMillis = currDowntimeMillis;
			LOG.warn("Caching is marked as down and will be disabled until {} (approx. {} from now)", new Timestamp(currDowntimeMillis + maxDowntimeMillis), Duration.ofMillis(maxDowntimeMillis));
		}
	}
}
