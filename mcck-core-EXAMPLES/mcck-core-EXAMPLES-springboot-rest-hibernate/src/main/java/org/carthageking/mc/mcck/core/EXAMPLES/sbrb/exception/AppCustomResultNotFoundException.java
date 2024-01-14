package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.exception;

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

public class AppCustomResultNotFoundException extends AppCustomException {

	private static final long serialVersionUID = 8485809729088437515L;

	public AppCustomResultNotFoundException() {
		// noop
	}

	public AppCustomResultNotFoundException(String msg) {
		super(msg);
	}

	public AppCustomResultNotFoundException(Throwable cause) {
		super(cause);
	}

	public AppCustomResultNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
