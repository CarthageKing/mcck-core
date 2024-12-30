package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	public WebMvcConfig() {
		// noop
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**", "/js/**")
			.addResourceLocations("/WEB-INF/css/", "/WEB-INF/js/");
	}
}
