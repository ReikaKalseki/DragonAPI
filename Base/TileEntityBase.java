/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public abstract class TileEntityBase extends TileEntity {

	protected static final Random par5Random = new Random();
	private int pseudometa;
	public boolean shutDown;
	public String placer;

	private final StepTimer updateTimer;

	protected static final ForgeDirection[] dirs = ForgeDirection.values();

	public abstract int getTileEntityBlockID();

	public abstract void updateEntity(World world, int x, int y, int z, int meta);

	public abstract void animateWithTick(World world, int x, int y, int z);

	public TileEntityBase() {
		super();
		updateTimer = new StepTimer(this.getBlockUpdateDelay());
	}

	public final Block getTEBlock() {
		int id = this.getTileEntityBlockID();
		if (id == 0)
			ReikaJavaLibrary.pConsole("TileEntity "+this+" tried to register ID 0!");
		if (id >= Block.blocksList.length) {
			ReikaJavaLibrary.pConsole(id+" is an invalid block ID for "+this+"!");
			return null;
		}
		return Block.blocksList[id];
	}

	public static final boolean isStandard8mReach(EntityPlayer ep, TileEntity te) {
		double dist = ReikaMathLibrary.py3d(te.xCoord+0.5-ep.posX, te.yCoord+0.5-ep.posY, te.zCoord+0.5-ep.posZ);
		return (dist <= 8);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);
		NBT.setInteger("meta", pseudometa);
		if (placer != null && !placer.isEmpty())
			NBT.setString("place", placer);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		pseudometa = NBT.getInteger("meta");
		placer = NBT.getString("place");
	}

	public final EntityPlayer getPlacer() {
		return worldObj.getPlayerEntityByName(placer);
	}

	public boolean isIDTEMatch(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te == null)
			return false;
		if (!(te instanceof TileEntityBase))
			return false;
		TileEntityBase tb = (TileEntityBase)te;
		int id = world.getBlockId(x, y, z);
		if (id != tb.getTileEntityBlockID())
			return false;
		return true;
	}

	public final int getPseudoMeta() {
		return pseudometa;
	}

	@Override //Overwritten to allow use of original code
	public final int getBlockMetadata() {
		return this.getPseudoMeta();
	}

	public final void setBlockMetadata(int meta) {
		pseudometa = meta;
	}

	public final boolean isInWorld() {
		return worldObj != null;
	}

	@Override //To avoid null pointers in inventory
	public final Block getBlockType() {
		//ReikaJavaLibrary.pConsole(this.blockType);
		if (blockType != null)
			return blockType;
		if (this.isInWorld()) {
			blockType = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)];
		}
		else {
			return blockType = Block.blocksList[this.getTileEntityBlockID()];
		}
		return blockType;
	}

	//To avoid null pointers in inventory
	public final int getRealBlockMetadata() {
		if (blockMetadata != -1)
			return blockMetadata;
		if (!this.isInWorld())
			return 0;
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return blockMetadata;
	}

	@Override
	public final void updateEntity() {
		if (shutDown)
			return;
		try {
			if (par5Random.nextInt(20) == 0)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			this.updateTileEntity();
			this.updateEntity(worldObj, xCoord, yCoord, zCoord, this.getBlockMetadata());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			this.writeError(e);
		}
		catch (IndexOutOfBoundsException e) {
			this.writeError(e);
		}
		catch (ArithmeticException e) {
			this.writeError(e);
		}
		catch (NullPointerException e) {
			this.writeError(e);
		}
		catch (ClassCastException e) {
			this.writeError(e);
		}
		catch (IllegalArgumentException e) {
			this.writeError(e);
		}
	}

	private void writeError(Exception e) {
		ReikaChatHelper.write(this+" is throwing "+e.getClass()+" on update: "+e.getMessage());
		ReikaChatHelper.write(Arrays.toString(e.getStackTrace()));
		ReikaChatHelper.write("");

		ReikaJavaLibrary.pConsole(this+" is throwing "+e.getClass()+" on update: "+e.getMessage());
		e.printStackTrace();
		ReikaJavaLibrary.pConsole("");
	}

	private final void updateTileEntity() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//rmb = this.getTEModel(worldObj, xCoord, yCoord, zCoord);
		this.animateWithTick(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeToNBT(var1);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 2, var1);
	}

	@Override
	public final void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet)
	{
		this.readFromNBT(packet.data);
	}

	public Random getRandom() {
		return par5Random;
	}

	@Override
	public String toString() {
		return "Tile Entity "+this.getTEName()+" @ "+xCoord+", "+yCoord+", "+zCoord;
	}

	protected abstract String getTEName();

	public boolean needsBlockUpdates() {
		return true;
	}

	/** Do not reference world, x, y, z, etc here, as this is called in the constructor */
	public int getBlockUpdateDelay() {
		return 20;
	}

	@Override
	public abstract boolean shouldRenderInPass(int pass);
}
