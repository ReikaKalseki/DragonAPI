/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.math.BigDecimal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;


public class BoundedValue<N extends Number> {

	private final Class<N> typeClass;

	private final boolean isDecimalType;

	private double value;

	private double step;

	private final double minValue;
	private final double maxValue;

	public BoundedValue(N min, N max) {
		this(min, max, (min.doubleValue()+max.doubleValue())/2D);
	}

	public BoundedValue(N min, N max, N init) {
		this(min, max, init.doubleValue());
	}

	private BoundedValue(N min, N max, double init) {
		this.typeClass = (Class<N>)min.getClass();
		this.minValue = min.doubleValue();
		this.maxValue = max.doubleValue();
		this.value = MathHelper.clamp_double(init, minValue, maxValue);
		this.isDecimalType = (min instanceof Double || min instanceof Float || min instanceof BigDecimal);
	}

	private BoundedValue(double min, double max, double init, double step, boolean dec, Class type) {
		this.minValue = min;
		this.maxValue = max;
		this.value = init;
		this.step = step;
		this.isDecimalType = dec;
		this.typeClass = type;
	}

	public BoundedValue setStep(N step) {
		this.step = this.isDecimalType ? step.doubleValue() : 1;
		return this;
	}

	public boolean increase() {
		if (this.value+step <= this.maxValue) {
			this.value += step;
			return true;
		}
		return false;
	}

	public boolean decrease() {
		if (this.value-step >= this.minValue) {
			this.value -= step;
			return true;
		}
		return false;
	}

	public void setValue(N val) {
		this.value = MathHelper.clamp_double(val.doubleValue(), minValue, maxValue);
	}

	public void setFraction(double f) {
		value = this.minValue+f*(this.maxValue-this.minValue);
	}

	public float getFraction() {
		return (float)((this.value-this.minValue)/(this.maxValue-this.minValue));
	}

	public double getValue() {
		return value;
	}

	public double getStep() {
		return step;
	}

	public double getMinValue() {
		return this.minValue;
	}

	public double getMaxValue() {
		return this.maxValue;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("type", this.typeClass.getName());
		tag.setBoolean("decimal", isDecimalType);
		tag.setDouble("val", value);
		tag.setDouble("min", minValue);
		tag.setDouble("max", maxValue);
		tag.setDouble("step", step);
	}

	public static BoundedValue readFromNBT(NBTTagCompound tag) {
		try {
			return new BoundedValue(tag.getDouble("min"), tag.getDouble("max"), tag.getDouble("val"), tag.getDouble("step"), tag.getBoolean("decimal"), Class.forName(tag.getString("type")));
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "["+this.minValue+" > "+this.maxValue+"] @ "+this.value+"x"+this.step+", "+this.isDecimalType;
	}

}
