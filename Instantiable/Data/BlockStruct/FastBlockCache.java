/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class FastBlockCache {

	private final HashSet<Coordinate> data = new HashSet();

	public Collection<Coordinate> getBlocks() {
		return Collections.unmodifiableSet(data);
	}

	public boolean removeBlock(Coordinate c) {
		return c != null && data.remove(c);
	}

	public boolean removeBlock(int x, int y, int z) {
		return this.removeBlock(new Coordinate(x, y, z));
	}

	public boolean addBlock(Coordinate c) {
		return c != null && data.add(c);
	}

	public boolean addBlock(int x, int y, int z) {
		return this.addBlock(new Coordinate(x, y, z));
	}

	public boolean containsBlock(Coordinate c) {
		return c != null && data.contains(c);
	}

	public boolean containsBlock(int x, int y, int z) {
		return this.containsBlock(new Coordinate(x, y, z));
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList li = new NBTTagList();
		for (Coordinate c : data) {
			NBTTagCompound dat = c.writeToTag();
			li.appendTag(dat);
		}
		tag.setTag("data", li);
	}

	public void readFromNBT(NBTTagCompound tag) {
		data.clear();
		NBTTagList li = tag.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound dat = (NBTTagCompound)o;
			Coordinate c = Coordinate.readTag(dat);
			if (c != null) {
				data.add(c);
			}
		}
	}

}
