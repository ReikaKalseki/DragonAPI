/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;


public class LookInfoCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		Vec3 vec = ep.getLookVec();
		HashSet<Entity> entities = new HashSet();
		for (double d = 0; d < 16; d += 0.25) {
			double dx = ep.posX+vec.xCoord*d;
			double dy = ep.posY+1.62-ep.yOffset+vec.yCoord*d;
			double dz = ep.posZ+vec.zCoord*d;
			int x = MathHelper.floor_double(dx);
			int y = MathHelper.floor_double(dy);
			int z = MathHelper.floor_double(dz);
			entities.addAll(ep.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(dx, dy, dz, dx, dy, dz).expand(1, 1, 1)));
			Block b = ep.worldObj.getBlock(x, y, z);
			if (b.isAir(ep.worldObj, x, y, z))
				continue;
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Data for block at "+x+", "+y+", "+z+":");
			this.sendChatToSender(ics, "Block ID: "+Block.getIdFromBlock(b)+" = '"+b.getUnlocalizedName()+"', C="+b.getClass());
			this.sendChatToSender(ics, "Metadata: "+ep.worldObj.getBlockMetadata(x, y, z));
			TileEntity te = ep.worldObj.getTileEntity(x, y, z);
			if (te != null) {
				this.sendChatToSender(ics, "TileEntity: "+te.getClass());
				if (ReikaPlayerAPI.isAdmin(ep)) {
					this.sendChatToSender(ics, "Tile NBT: "+te.getClass());
					NBTTagCompound tag = new NBTTagCompound();
					te.writeToNBT(tag);
					ArrayList<String> li = ReikaNBTHelper.parseNBTAsLines(tag);
					for (String s : li)
						ReikaChatHelper.sendChatToPlayer(ep, s);
				}
			}
			else {
				this.sendChatToSender(ics, "No TileEntity.");
			}
			break;
		}
		entities.remove(ep);
		if (args.length > 0 && args[0].equalsIgnoreCase("entity")) {
			if (entities.isEmpty()) {
				this.sendChatToSender(ics, "No entities found.");
			}
			else {
				this.sendChatToSender(ics, entities.size()+" entities found:");
				for (Entity e : entities) {
					EntityRegistration er = EntityRegistry.instance().lookupModSpawn(e.getClass(), true);
					this.sendChatToSender(ics, (er != null ? er.getContainer().getName() : "[No Mod]")+" : "+e.getClass().getName());
				}
			}
		}
	}

	@Override
	public String getCommandString() {
		return "blockinfo";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
