/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

public final class ItemMaterial {

	public static final ItemMaterial PLANT = new ItemMaterial("Plant", 0);
	public static final ItemMaterial WOOD = new ItemMaterial("Wood", 0);
	public static final ItemMaterial STONE = new ItemMaterial("Stone", 2700);
	public static final ItemMaterial IRON = new ItemMaterial("Iron", 1560);
	public static final ItemMaterial STEEL = new ItemMaterial("Steel", 1560);
	public static final ItemMaterial GOLD = new ItemMaterial("Gold", 1060);
	public static final ItemMaterial DIAMOND = new ItemMaterial("Diamond", 2700);
	public static final ItemMaterial OBSIDIAN = new ItemMaterial("Obsidian", 800);
	public static final ItemMaterial COAL = new ItemMaterial("Coal", 400);

	public ItemMaterial(String n, int melting) {
		melt = melting;
		name = n;
	}

	private int melt;
	private int TS;
	private int GS;
	private int rho;

	private String name;

	public int getMelting() {
		return melt;
	}

	@Override
	public String toString() {
		return name.toUpperCase();
	}
}
