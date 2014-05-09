package org.springframework.boot.rhino.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class NativeJavaScriptable implements Scriptable, Serializable {

	private static final long serialVersionUID = 8183800133024111117L;

	private Scriptable scriptable;

	@SuppressWarnings("rawtypes")
	public NativeJavaScriptable(Scriptable scope, Object obj) {
		if (obj instanceof Wrapper) {
			this.scriptable = new NativeJavaScriptable(scope,
					((Wrapper) obj).unwrap());
		} else if (obj == null || obj == Undefined.instance) {
			this.scriptable = new NativeJavaMap(scope, new HashMap());
		} else if (obj instanceof Map) {
			this.scriptable = new NativeJavaMap(scope, (Map) obj);
		} else if (obj instanceof Collection) {
			this.scriptable = new NativeJavaCollection(scope, (Collection) obj);
		} else if (obj instanceof Scriptable) {
			this.scriptable = (Scriptable) obj;
		} else {
			this.scriptable = new NativeJavaObject(scope, obj, null);
		}
	}

	public String getClassName() {
		return scriptable.getClassName();
	}

	public Object get(String name, Scriptable start) {
		return scriptable.get(name, start);
	}

	public Object get(int index, Scriptable start) {
		return scriptable.get(index, start);
	}

	public boolean has(String name, Scriptable start) {
		return scriptable.has(name, start);
	}

	public boolean has(int index, Scriptable start) {
		return scriptable.has(index, start);
	}

	public void put(String name, Scriptable start, Object value) {
		scriptable.put(name, start, value);
	}

	public void put(int index, Scriptable start, Object value) {
		scriptable.put(index, start, value);
	}

	public void delete(String name) {
		scriptable.delete(name);
	}

	public void delete(int index) {
		scriptable.delete(index);
	}

	public Scriptable getPrototype() {
		return scriptable.getPrototype();
	}

	public void setPrototype(Scriptable prototype) {
		scriptable.setPrototype(prototype);
	}

	public Scriptable getParentScope() {
		return scriptable.getParentScope();
	}

	public void setParentScope(Scriptable parent) {
		scriptable.setParentScope(parent);
	}

	public Object[] getIds() {
		return scriptable.getIds();
	}

	public Object getDefaultValue(Class<?> hint) {
		return scriptable.getDefaultValue(hint);
	}

	public boolean hasInstance(Scriptable instance) {
		return scriptable.hasInstance(instance);
	}

}
