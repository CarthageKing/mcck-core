package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model;

import java.util.Optional;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util.AppCustomConstants;
import org.slf4j.MDC;
import org.springframework.data.domain.AuditorAware;

public class AppCustomAuditorAware implements AuditorAware<String> {

	public AppCustomAuditorAware() {
		// noop
	}

	@Override
	public Optional<String> getCurrentAuditor() {
		String username = MDC.get(AppCustomConstants.MDC_KEY_USERNAME);
		return Optional.ofNullable(username);
	}
}
