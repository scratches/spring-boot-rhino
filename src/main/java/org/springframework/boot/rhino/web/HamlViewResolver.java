/*
 * Copyright 2012-2013 the original author or authors.
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

package org.springframework.boot.rhino.web;

import java.util.Locale;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.boot.rhino.HamlCompiler;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * @author Dave Syer
 *
 */
public class HamlViewResolver extends UrlBasedViewResolver {

	private HamlCompiler compiler = new HamlCompiler();

	public HamlViewResolver() {
		setViewClass(HamlView.class);
	}

	/**
	 * @param compiler the compiler to set
	 */
	public void setCompiler(HamlCompiler compiler) {
		this.compiler = compiler;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		Resource resource = resolveResource(viewName, locale);
		if (resource == null) {
			return null;
		}
		HamlView view = new HamlView(compiler.compile(resource));
		view.setApplicationContext(getApplicationContext());
		view.setServletContext(getServletContext());
		return view;
	}

	private Resource resolveResource(String viewName, Locale locale) {
		String l10n = "";
		if (locale != null) {
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setValue(locale);
			l10n = "_" + localeEditor.getAsText();
		}
		return resolveFromLocale(viewName, l10n);
	}

	private Resource resolveFromLocale(String viewName, String locale) {
		Resource resource = getApplicationContext().getResource(
				getPrefix() + viewName + locale + getSuffix());
		if (resource == null || !resource.exists()) {
			if (locale.isEmpty()) {
				return null;
			}
			int index = locale.lastIndexOf("_");
			return resolveFromLocale(viewName, locale.substring(0, index));
		}
		return resource;
	}

}
