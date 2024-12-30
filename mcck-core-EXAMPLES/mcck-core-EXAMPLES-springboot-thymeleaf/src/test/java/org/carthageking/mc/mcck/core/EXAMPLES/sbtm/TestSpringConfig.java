package org.carthageking.mc.mcck.core.EXAMPLES.sbtm;

import org.carthageking.mc.mcck.core.EXAMPLES.sbtm.controller.GeneralController;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { GeneralController.class })
public class TestSpringConfig {

	public TestSpringConfig() {
		// noop
	}
}
