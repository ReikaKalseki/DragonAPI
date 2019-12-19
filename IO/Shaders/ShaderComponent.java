package Reika.DragonAPI.IO.Shaders;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

public class ShaderComponent {

	private float intensity;
	private final HashMap<String, Object> variables = new HashMap();

	protected ShaderComponent() {

	}

	ShaderComponent(HashMap<String, Object> vars) {
		this();
		this.setVariables(vars);
	}

	public ShaderComponent setIntensity(float f) {
		intensity = f;
		return this;
	}

	public float getIntensity() {
		return intensity;
	}

	void setVariables(HashMap<String, Object> vars) {
		variables.putAll(vars);
	}

	public void setVariable(String field, Object o) {
		variables.put(field, o);
	}

	Collection<Entry<String, Object>> getVariables() {
		return Collections.unmodifiableCollection(variables.entrySet());
	}

	boolean hasVariables() {
		return !variables.isEmpty();
	}

}
