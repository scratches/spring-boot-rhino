/*
 *  Copyright 2006 Hannes Wallnoefer <hannes@helma.at>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.boot.rhino.util;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NativeJavaMap extends NativeJavaObject {

	private static final long serialVersionUID = 3686149074336728382L;

	boolean reflect = true;
	Map map;
	final static String CLASSNAME = "NativeMap";

	public NativeJavaMap(Scriptable scope, Map map) {
		super(scope, map, null);
		this.map = map;
		initPrototype(scope);
	}

	/**
	 * Set the prototype to the Array prototype so we can use array methds such
	 * as push, pop, shift, slice etc.
	 * 
	 * @param scope
	 *            the global scope for looking up the Array constructor
	 */
	protected void initPrototype(Scriptable scope) {
		Scriptable arrayProto = ScriptableObject.getClassPrototype(scope,
				"Object");
		if (arrayProto != null) {
			this.setPrototype(arrayProto);
		}
	}

	@Override
	public Object get(String name, Scriptable start) {
		if (map == null || (reflect && super.has(name, start))) {
			return super.get(name, start);
		}
		return getInternal(name);
	}

	@Override
	public Object get(int index, Scriptable start) {
		if (map == null) {
			return super.get(index, start);
		}
		return getInternal(new Integer(index));
	}

	private Object getInternal(Object key) {
		Object value = map.get(key);
		if (value == null) {
			return Scriptable.NOT_FOUND;
		}
		return Context.javaToJS(value, getParentScope());
	}

	@Override
	public boolean has(String name, Scriptable start) {
		if (map == null || (reflect && super.has(name, start))) {
			return super.has(name, start);
		} else {
			return map.containsKey(name);
		}
	}

	@Override
	public boolean has(int index, Scriptable start) {
		if (map == null) {
			return super.has(index, start);
		} else {
			return map.containsKey(new Integer(index));
		}
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (map == null || (reflect && super.has(name, start))) {
			super.put(name, start, value);
		} else {
			putInternal(name, value);
		}
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		if (map == null) {
			super.put(index, start, value);
		} else {
			putInternal(new Integer(index), value);
		}
	}

	private void putInternal(Object key, Object value) {
		try {
			map.put(key, Context.jsToJava(value, Object.class));
		} catch (RuntimeException e) {
			Context.throwAsScriptRuntimeEx(e);
		}
	}

	@Override
	public void delete(String name) {
		if (map != null) {
			try {
				map.remove(name);
			} catch (RuntimeException e) {
				Context.throwAsScriptRuntimeEx(e);
			}
		} else {
			super.delete(name);
		}
	}

	@Override
	public void delete(int index) {
		if (map != null) {
			try {
				map.remove(new Integer(index));
			} catch (RuntimeException e) {
				Context.throwAsScriptRuntimeEx(e);
			}
		} else {
			super.delete(index);
		}
	}

	@Override
	public Object[] getIds() {
		if (map == null) {
			return super.getIds();
		} else {
			return map.keySet().toArray();
		}
	}

	@Override
	public String toString() {
		if (map == null)
			return super.toString();
		return map.toString();
	}

	@Override
	public Object getDefaultValue(Class typeHint) {
		return toString();
	}

	@Override
	public Object unwrap() {
		return map==null ? map : this.javaObject;
	}

	public Map getMap() {
		return map;
	}

	public String getClassName() {
		return CLASSNAME;
	}
}