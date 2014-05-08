/*
 * Copyright 2013-2014 the original author or authors.
 *
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
 */

package org.springframework.boot.rhino.autoconfigure;

import org.mozilla.javascript.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.rhino.HamlCompiler;
import org.springframework.boot.rhino.web.HamlViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/**
 * @author Dave Syer
 *
 */
@Configuration
@ConditionalOnClass(Context.class)
@EnableConfigurationProperties(HamlProperties.class)
public class HamlAutoConfiguration {
	
	@Autowired
	private HamlProperties haml;
	
	@Autowired
	private Environment environment;
	
	@Bean
	@ConditionalOnMissingBean(HamlCompiler.class)
	public HamlCompiler hamlCompiler() {
		HamlCompiler compiler = new HamlCompiler();
		compiler.setCharset(haml.getCharSet());
		return compiler;
	}
	
	@Bean
	@ConditionalOnMissingBean(HamlViewResolver.class)
	public HamlViewResolver mustacheViewResolver(HamlCompiler hamlCompiler) {
		HamlViewResolver resolver = new HamlViewResolver();
		resolver.setPrefix(haml.getPrefix());
		resolver.setSuffix(haml.getSuffix());
		resolver.setCache(haml.isCache());
		resolver.setViewNames(haml.getViewNames());
		resolver.setContentType(haml.getContentType());
		resolver.setCompiler(hamlCompiler);
		resolver.setOrder(Ordered.LOWEST_PRECEDENCE-10);
		return resolver;
	}

}
