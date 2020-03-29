package Reika.DragonAPI.Instantiable.Data;


public class CachedValue<V> {

	private final ValueCalculator<V> calculator;

	private boolean needsRecalc = true;
	private V value;

	public CachedValue(ValueCalculator<V> c) {
		this.calculator = c;
		this.value = c.calculate();
	}

	public void markDirty() {
		needsRecalc = true;
	}

	public V getValue() {
		if (this.needsRecalc) {
			value = calculator.calculate();
			this.needsRecalc = false;
		}
		return value;
	}

	public void setValue(V val) {
		this.value = val;
		this.needsRecalc = false;
	}

	public static interface ValueCalculator<V> {

		V calculate();

	}

}
