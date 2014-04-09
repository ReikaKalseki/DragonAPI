/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityBase extends TileEntity {

	protected static final Random rand = new Random();
	private int pseudometa;
	protected boolean shutDown;
	public String placer;
	private int ticksExisted;

	private final StepTimer updateTimer;
	private final StepTimer packetTimer;

	private final TileEntity[] adjTEMap = new TileEntity[6];

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	public abstract int getTileEntityBlockID();

	public abstract void updateEntity(World world, int x, int y, int z, int meta);

	protected abstract void animateWithTick(World world, int x, int y, int z);

	public TileEntityBase() {
		super();
		updateTimer = new StepTimer(this.getBlockUpdateDelay());
		packetTimer = new StepTimer(this.getPacketDelay());
	}

	public int getTicksExisted() {
		return ticksExisted;
	}

	public int getPacketDelay() {
		return 1;
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

	protected void writeSyncTag(NBTTagCompound NBT) {
		NBT.setInteger("meta", pseudometa);
	}

	protected void readSyncTag(NBTTagCompound NBT) {
		pseudometa = NBT.getInteger("meta");
		if (pseudometa > 15)
			pseudometa = 15;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
		this.writeSyncTag(NBT);

		if (placer != null && !placer.isEmpty())
			NBT.setString("place", placer);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
		this.readSyncTag(NBT);

		placer = NBT.getString("place");
	}

	public void syncAllData() {
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeSyncTag(var1);
		this.writeToNBT(var1);
		var1.setBoolean("fullData", true);
		Packet132TileEntityData p = new Packet132TileEntityData(xCoord, yCoord, zCoord, 2, var1);
		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, this.getUpdatePacketRadius(), worldObj.provider.dimensionId, p);
	}

	@Override
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeSyncTag(var1);
		Packet132TileEntityData p = new Packet132TileEntityData(xCoord, yCoord, zCoord, 2, var1);
		return p;
	}

	@Override
	public final void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet)
	{
		this.readSyncTag(packet.data);
		if (packet.data.getBoolean("fullData"))
			this.readFromNBT(packet.data);
	}

	public final EntityPlayer getPlacer() {
		return placer != null && !placer.isEmpty() ? worldObj.getPlayerEntityByName(placer) : null;
	}

	public boolean isIDTEMatch() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == 0)
			return false;
		Block b = Block.blocksList[id];
		if (!b.hasTileEntity(meta))
			return false;
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te == null)
			return false;
		if (!(te instanceof TileEntityBase))
			return false;
		TileEntityBase tb = (TileEntityBase)te;
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
		if (this.shouldRunUpdateCode()) {
			try {
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
		if (this.shouldSendSyncPackets()) {
			packetTimer.update();
			if (packetTimer.checkCap()) {
				this.sendSyncPacket();
			}
		}
		if (worldObj.isRemote && this.needsToCauseBlockUpdates()) {
			updateTimer.update();
			if (updateTimer.checkCap()) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		ticksExisted++;
	}

	private void sendSyncPacket() {
		Packet dat = this.getDescriptionPacket();
		if (dat != null)
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, this.getUpdatePacketRadius(), worldObj.provider.dimensionId, dat);
	}

	public int getUpdatePacketRadius() {
		return 16;
	}

	protected boolean shouldRunUpdateCode() {
		return true;//!shutDown && !worldObj.isRemote;
	}

	protected final boolean shouldSendSyncPackets() {
		return !worldObj.isRemote;
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
		if (this.getTicksExisted() == 0) {
			for (int i = 0; i < 6; i++)
				this.updateCache(dirs[i]);
		}
	}

	public Random getRandom() {
		return rand;
	}

	@Override
	public String toString() {
		return "Tile Entity "+this.getTEName()+(this.isInWorld() ? " @ "+xCoord+", "+yCoord+", "+zCoord : " (item)");
	}

	protected abstract String getTEName();

	//public abstract boolean needsDataUpdates();

	/** Do not reference world, x, y, z, etc here, as this is called in the constructor */
	public final int getBlockUpdateDelay() {
		return 20;
	}

	@Override
	public abstract boolean shouldRenderInPass(int pass);

	public Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}

	protected void delete() {
		worldObj.setBlock(xCoord, yCoord, zCoord, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
	}

	public boolean needsToCauseBlockUpdates() {
		return false;
	}

	public TileEntity getAdjacentTileEntity(ForgeDirection dir) {
		if (this.cachesTEs()) {
			return this.getCachedTE(dir);
		}
		else {
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			if (!ReikaWorldHelper.tileExistsAt(worldObj, dx, dy, dz))
				return null;
			return worldObj.getBlockTileEntity(dx, dy, dz);
		}
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		if (!ReikaWorldHelper.tileExistsAt(worldObj, x, y, z))
			return null;
		return worldObj.getBlockTileEntity(x, y, z);
	}

	public final boolean isDirectlyAdjacent(int x, int y, int z) {
		return Math.abs(x-xCoord)+Math.abs(y-yCoord)+Math.abs(z-zCoord) == 1;
	}

	private boolean cachesTEs() {
		return this.getBlockType() instanceof BlockTEBase;
	}

	private TileEntity getCachedTE(ForgeDirection dir) {
		return adjTEMap[dir.ordinal()];
	}

	public void updateCache(ForgeDirection dir) {
		TileEntity te = worldObj.getBlockTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		adjTEMap[dir.ordinal()] = te;
	}
}
