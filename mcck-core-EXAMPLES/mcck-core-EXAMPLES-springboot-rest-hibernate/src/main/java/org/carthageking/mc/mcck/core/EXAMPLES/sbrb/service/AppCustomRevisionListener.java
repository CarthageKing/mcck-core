package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service;

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
