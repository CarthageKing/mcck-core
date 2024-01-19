package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service;

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

import java.time.Instant;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public abstract class AbstractStatefulRunnable implements Runnable {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractStatefulRunnable.class);

	protected volatile boolean inited;
	protected volatile boolean running;
	protected int counter;

	protected AbstractStatefulRunnable() {
		// noop
	}

	@PostConstruct
	protected void afterPropertiesSet() throws Exception {
		inited = true;
	}

	@PreDestroy
	protected void beforeDestroy() throws Exception {
		inited = false;
	}

	protected org.slf4j.Logger getLog() {
		return LOG;
	}

	protected String getName() {
		return this.toString();
	}

	public void pause() {
		inited = false;
	}

	public void unpause() {
		inited = true;
	}

	@Override
	public void run() {
		long timeNowMillis = Instant.now().toEpochMilli();
		if (!inited) {
			return;
		}
		synchronized (this) {
			if (running) {
				getLog().debug("This {} instance is currently running in another thread", getName());
				return;
			}
			running = true;
		}
		counter++;
		final String finalName = getName() + "-" + counter;
		final String oldName = Thread.currentThread().getName();
		Thread.currentThread().setName(finalName);
		try {
			if (!Thread.currentThread().isInterrupted()) {
				runIt(finalName, timeNowMillis);
			}
		} catch (Exception e) {
			getLog().error("An error occurred", e);
		} finally {
			Thread.currentThread().setName(oldName);
			synchronized (this) {
				running = false;
			}
		}
	}

	protected abstract void runIt(String finalName, long timeNowMillis) throws Exception;
}
