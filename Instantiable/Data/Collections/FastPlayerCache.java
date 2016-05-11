/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class FastPlayerCache {

	private final HashSet<UUID> data = new HashSet();

	public Collection<UUID> getPlayers() {
		return Collections.unmodifiableSet(data);
	}

	public boolean removePlayer(UUID uid) {
		return uid != null && data.remove(uid);
	}

	public boolean removePlayer(EntityPlayer ep) {
		return this.removePlayer(ep.getUniqueID());
	}

	public boolean addPlayer(UUID uid) {
		return uid != null && data.add(uid);
	}

	public boolean addPlayer(EntityPlayer ep) {
		return this.addPlayer(ep.getUniqueID());
	}

	public boolean containsPlayer(UUID uid) {
		return uid != null && data.contains(uid);
	}

	public boolean containsPlayer(EntityPlayer ep) {
		return this.containsPlayer(ep.getUniqueID());
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList li = new NBTTagList();
		for (UUID uid : data) {
			NBTTagString dat = new NBTTagString(uid.toString());
			li.appendTag(dat);
		}
		tag.setTag("data", li);
	}

	public void readFromNBT(NBTTagCompound tag) {
		data.clear();
		NBTTagList li = tag.getTagList("data", NBTTypes.STRING.ID);
		for (Object o : li.tagList) {
			NBTTagString dat = (NBTTagString)o;
			UUID uid = UUID.fromString(dat.func_150285_a_());
			if (uid != null) {
				data.add(uid);
			}
		}
	}

}
