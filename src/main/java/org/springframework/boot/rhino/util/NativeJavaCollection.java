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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NativeJavaCollection extends NativeJavaObject {

	private static final long serialVersionUID = 3686149074336728382L;

	boolean reflect = true;
	Collection collection;
	final static String CLASSNAME = "NativeMap";

	public NativeJavaCollection(Scriptable scope, Collection collection) {
		super(scope, collection, null);
		this.collection = collection;
	}

	@Override
	public boolean has(String id, Scriptable start) {
		return id.equals("length") || super.has(id, start);
	}

	@Override
	public boolean has(int index, Scriptable start) {
		return 0 <= index && index < collection.size();
	}

	@Override
	public Object get(String id, Scriptable start) {
		if (id.equals("length"))
			return Integer.valueOf(collection.size());
		Object result = super.get(id, start);
		if (result == NOT_FOUND
				&& !ScriptableObject.hasProperty(getPrototype(), id)) {
			throw Context.reportRuntimeError("Java member not found "
					+ collection.getClass().getName() + "[" + id + "]");
		}
		return result;
	}

	@Override
	public Object get(int index, Scriptable start) {
		if (0 <= index && index < collection.size()) {
			Context cx = Context.getCurrentContext();
			Object obj = null;
			if (collection instanceof List) {
				obj = ((List) collection).get(index);
			} else {
				Iterator iter = collection.iterator();
				for (int i = 0; i < index; i++) {
					obj = iter.next();
				}
			}
			return cx.getWrapFactory().wrap(cx, this, obj,
					collection.getClass());
		}
		return Undefined.instance;
	}

	@Override
	public void put(String id, Scriptable start, Object value) {
		// Ignore assignments to "length"--it's readonly.
		if (!id.equals("length"))
			throw Context.reportRuntimeError("Java member not found "
					+ collection.getClass().getName() + "[" + id + "]");
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		if (0 <= index && index < collection.size()) {
			if (collection instanceof List) {
				((List) collection).set(index, value);
			} else {
				Iterator iter = collection.iterator();
				Object old = null;
				for (int i = 0; i < index; i++) {
					old = iter.next();
				}
				collection.remove(old);
				collection.add(value);
			}
		} else {
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		if (hint == null || hint == ScriptRuntime.StringClass)
			return collection.toString();
		if (hint == ScriptRuntime.BooleanClass)
			return Boolean.TRUE;
		if (hint == ScriptRuntime.NumberClass)
			return ScriptRuntime.NaNobj;
		return this;
	}

	@Override
	public Object[] getIds() {
		Object[] result = new Object[collection.size()];
		int i = collection.size();
		while (--i >= 0)
			result[i] = Integer.valueOf(i);
		return result;
	}

	@Override
	public boolean hasInstance(Scriptable value) {
		if (!(value instanceof Wrapper))
			return false;
		Object instance = ((Wrapper) value).unwrap();
		if (collection.isEmpty()) {
			return true;
		}
		// Sloppy non-invertible type relation here...
		return collection.iterator().next().getClass().isInstance(instance);
	}

	@Override
	public Scriptable getPrototype() {
		if (prototype == null) {
			prototype = ScriptableObject.getArrayPrototype(this
					.getParentScope());
		}
		return prototype;
	}
}