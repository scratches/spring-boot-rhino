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

package org.springframework.boot.rhino;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.boot.rhino.util.NativeIndexableObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * 
 * @author Dave Syer
 *
 */
public class HamlCompiler {

	private final ScriptableObject globalScope;
	private Charset charset;

	public HamlCompiler() {

		Resource haml = new ClassPathResource("haml.js");
		Resource json2 = new ClassPathResource("json2.js");

		Context context = Context.enter();
		try {
			context.setOptimizationLevel(-1);
			globalScope = context.initStandardObjects();
			context.evaluateReader(globalScope,
					new InputStreamReader(haml.getInputStream()), "haml", 0,
					null);
			context.evaluateReader(globalScope,
					new InputStreamReader(json2.getInputStream()), "json2", 0,
					null);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot initialize compiler", e);
		} finally {
			Context.exit();
		}

	}

	public Template compile(String haml) {
		Context context = Context.enter();
		try {
			Scriptable compileScope = context.newObject(globalScope);
			compileScope.setParentScope(globalScope);
			compileScope.put("hamlSource", compileScope, haml);
			compileScope.put("out", compileScope, Context.javaToJS(System.out, compileScope));
			Template compiled = new Template(globalScope, (String) context.evaluateString(compileScope,
					"Haml.optimize(Haml.compile(hamlSource));", "HamlCompiler",
					0, null));
			return compiled;
		} catch (JavaScriptException e) {
			throw new IllegalStateException("Cannot compile", e);
		} finally {
			Context.exit();
		}
	}

	public Template compile(Resource template) {
		String haml;
		try {
			haml = StreamUtils.copyToString(template.getInputStream(), charset);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot load template resource: "
					+ template, e);
		}
		return compile(haml);
	}

	public void setCharset(String charSet) {
		this.charset = Charset.forName(charSet);
	}
	
	public static class Template {

		private String script;
		private Scriptable parentScope;

		public Template(Scriptable scope, String script) {
			this.parentScope = scope;
			this.script = script;
			
		}

		public String getScript() {
			return script;
		}

		public String execute(Object root) {
			Context context = Context.enter();
			try {
				Scriptable scope = new NativeIndexableObject(parentScope, root);
				scope.setParentScope(parentScope);
				String result = (String) context.evaluateString(scope,
						script, "Template",
						0, null);
				return result;
			} catch (JavaScriptException e) {
				throw new RuntimeException("Unable to execute " + script, e);
			} finally {
				Context.exit();
			}
		}

	}

}
