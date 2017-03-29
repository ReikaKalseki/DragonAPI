package Reika.DragonAPI.Command;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;


public class BlockInfoCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		Vec3 vec = ep.getLookVec();
		for (double d = 0; d < 16; d += 0.25) {
			int x = MathHelper.floor_double(ep.posX+vec.xCoord*d);
			int y = MathHelper.floor_double(ep.posY+1.62-ep.yOffset+vec.yCoord*d);
			int z = MathHelper.floor_double(ep.posZ+vec.zCoord*d);
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
