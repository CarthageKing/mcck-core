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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;

// This is copied from Spring's ProxyCachingConfiguration and modified. The reason we
// override it is to provide our own CacheInterceptor implementation
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class McckProxyCachingConfiguration extends ProxyCachingConfiguration {

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		// don't look for the enablecaching annotation
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	@Override
	public CacheInterceptor cacheInterceptor(CacheOperationSource cacheOperationSource) {
		McckCacheInterceptor interceptor = new McckCacheInterceptor();
		interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
		interceptor.setErrorHandler(interceptor);
		interceptor.setCacheOperationSource(cacheOperationSource);
		return interceptor;
	}
}
