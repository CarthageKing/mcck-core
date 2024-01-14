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

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.AppCustomRevisionEntity;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util.AppCustomConstants;
import org.hibernate.envers.RevisionListener;
import org.slf4j.MDC;

public class AppCustomRevisionListener implements RevisionListener {

	public AppCustomRevisionListener() {
		// noop
	}

	@Override
	public void newRevision(Object revisionEntity) {
		// id and timestamp will be populated by Envers. we need to populate
		// the other fields
		String username = MDC.get(AppCustomConstants.MDC_KEY_USERNAME);
		AppCustomRevisionEntity re = (AppCustomRevisionEntity) revisionEntity;
		re.setUsername(username);
	}
}
