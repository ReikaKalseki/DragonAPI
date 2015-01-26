/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.IO.SyncPacket;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper.TypeSelector;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Strippable(value = {"dan200.computercraft.api.peripheral.IPeripheral", "li.cil.oc.api.network.Environment",
"li.cil.oc.api.network.ManagedPeripheral"})
public abstract class TileEntityBase extends TileEntity implements IPeripheral, Environment, ManagedPeripheral {

	protected static final Random rand = new Random();
	private int pseudometa;
	protected boolean shutDown;
	protected String placer;
	private String placerUUID;
	private int ticksExisted;
	private FakePlayer fakePlayer;

	private final StepTimer updateTimer;
	//private final StepTimer packetTimer;
	private final StepTimer fullSyncTimer;
	private boolean forceSync = true;

	private int updateDelay = 0;

	private final TileEntity[] adjTEMap = new TileEntity[6];

	protected final ForgeDirection[] dirs = ForgeDirection.values();

	public abstract Block getTileEntityBlockID();

	public abstract void updateEntity(World world, int x, int y, int z, int meta);

	protected abstract void animateWithTick(World world, int x, int y, int z);

	public abstract int getRedstoneOverride();

	private final SyncPacket syncTag = new SyncPacket();

	public TileEntityBase() {
		super();
		updateTimer = new StepTimer(this.getBlockUpdateDelay());
		//packetTimer = new StepTimer(this.getPacketDelay());
		fullSyncTimer = new StepTimer(1200);
		fullSyncTimer.setTick(rand.nextInt(1200));
	}

	public int getTicksExisted() {
		return ticksExisted;
	}

	public int getPacketDelay() {
		return 5;
	}

	public void animateItem() {
		if (worldObj == null) {
			this.animateWithTick(null, 0, 0, 0);
		}
	}

	public final boolean isPlacer(EntityPlayer ep) {
		if (placer == null || placerUUID == null || placer.isEmpty() || placerUUID.isEmpty())
			return false;
		return ep.getCommandSenderName().equals(placer) && ep.getUniqueID().toString().equals(placerUUID.toString());
	}

	public final Block getTEBlock() {
		Block id = this.getTileEntityBlockID();
		if (id == Blocks.air)
			ReikaJavaLibrary.pConsole("TileEntity "+this+" tried to register ID 0!");
		if (id == null) {
			ReikaJavaLibrary.pConsole(id+" is an invalid block ID for "+this+"!");
			return null;
		}
		return id;
	}

	public static final boolean isStandard8mReach(EntityPlayer ep, TileEntity te) {
		double dist = ReikaMathLibrary.py3d(te.xCoord+0.5-ep.posX, te.yCoord+0.5-ep.posY, te.zCoord+0.5-ep.posZ);
		return (dist <= 8);
	}

	public boolean isPlayerAccessible(EntityPlayer var1) {
		double dist = ReikaMathLibrary.py3d(xCoord+0.5-var1.posX, yCoord+0.5-var1.posY, zCoord+0.5-var1.posZ);
		return (dist <= 8) && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
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
		if (placerUUID != null && !placerUUID.isEmpty())
			NBT.setString("placeUUID", placerUUID);

		if (ModList.OPENCOMPUTERS.isLoaded()) {
			if (node instanceof Component)
				((Component)node).save(NBT);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
		this.readSyncTag(NBT);

		placer = NBT.getString("place");
		placerUUID = NBT.getString("placeUUID");

		if (ModList.OPENCOMPUTERS.isLoaded()) {
			if (node instanceof Component)
				((Component)node).load(NBT);
		}
	}

	public final void scheduleBlockUpdate(int ticks) {
		updateDelay = ticks;
	}

	private void sendPacketToAllAround(S35PacketUpdateTileEntity p, int r) {
		if (!worldObj.isRemote) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(r, r, r);
			List<EntityPlayerMP> li = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, box);
			for (EntityPlayerMP entityplayermp : li)  {
				entityplayermp.playerNetServerHandler.sendPacket(p);
			}
		}
	}

	public final void syncAllData(boolean fullNBT) {
		if (worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.TILESYNC.ordinal(), this, fullNBT ? 1 : 0);
		}
		else {
			worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
			NBTTagCompound var1 = new NBTTagCompound();
			if (fullNBT)
				this.writeToNBT(var1);
			this.writeSyncTag(var1);
			if (fullNBT)
				var1.setBoolean("fullData", true);
			S35PacketUpdateTileEntity p = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 2, var1);
			this.sendPacketToAllAround(p, this.getUpdatePacketRadius());

			this.syncTankData();

			this.onDataSync(fullNBT);
		}
		if (worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord))
			this.markDirty();
	}

	private void syncTankData() {
		Collection<Field> c = ReikaReflectionHelper.getFields(this.getClass(), new TypeSelector(HybridTank.class));
	}

	protected void onDataSync(boolean fullNBT) {

	}

	@Override
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeSyncTag(nbt);
		this.writeToNBT(nbt);
		nbt.setBoolean("fullData", true);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 2, nbt);
	}

	private boolean shouldFullSync() {
		return forceSync;
	}

	public void forceFullSync() {
		forceSync = true;
	}

	@Override
	public final void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
	{
		if (packet instanceof SyncPacket) {
			SyncPacket p = (SyncPacket)packet;
			if (!p.hasNoData()) {
				NBTTagCompound NBT = new NBTTagCompound();
				this.writeSyncTag(NBT); //so unsent fields do not zero out, we sync the current values in
				p.readForSync(NBT);
				this.readSyncTag(NBT);
			}
		}
		else {
			this.readSyncTag(packet.field_148860_e);
			if (packet.field_148860_e.getBoolean("fullData")) {
				this.readFromNBT(packet.field_148860_e);
			}
		}
	}

	public final void setPlacer(EntityPlayer ep) {
		placer = ep.getCommandSenderName();
		if (ep.getGameProfile().getId() != null)
			placerUUID = ep.getGameProfile().getId().toString();
	}

	public final String getPlacerName() {
		return placer;
	}

	public final EntityPlayer getPlacer() {
		if (placer == null || placer.isEmpty())
			return null;
		EntityPlayer ep = worldObj.getPlayerEntityByName(placer);
		return ep != null ? ep : this.getFakePlacer();
	}

	public final EntityPlayerMP getServerPlacer() {
		EntityPlayer ep = this.getPlacer();
		if (ep instanceof EntityPlayerMP)
			return (EntityPlayerMP)ep;
		else
			throw new MisuseException("Cannot get the serverside player on the client!");
	}

	public final EntityPlayer getFakePlacer() {
		if (placer == null || placer.isEmpty())
			return null;
		if (worldObj.isRemote)
			return null;
		if (fakePlayer == null)
			fakePlayer = ReikaPlayerAPI.getFakePlayerByNameAndUUID((WorldServer)worldObj, placer, placerUUID);
		return fakePlayer;
	}

	public boolean isIDTEMatch() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b == Blocks.air)
			return false;
		;
		if (!b.hasTileEntity(meta))
			return false;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return false;
		if (!(te instanceof TileEntityBase))
			return false;
		TileEntityBase tb = (TileEntityBase)te;
		if (b != tb.getTileEntityBlockID())
			return false;
		return true;
	}

	public final int getPseudoMeta() {
		return pseudometa;
	}

	/** Overwritten to allow use of pseudo-metadata for internal separation with more than 16 variants. Not client-only. */
	@Override
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
			blockType = worldObj.getBlock(xCoord, yCoord, zCoord);
		}
		else {
			return blockType = this.getTileEntityBlockID();
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
		if (this.getTicksExisted() < 20)
			this.syncAllData(true);
		//packetTimer.update();

		fullSyncTimer.update();
		if (fullSyncTimer.checkCap()) {
			this.forceFullSync();
		}

		if (this.shouldSendSyncPacket() || this.shouldFullSync()) {
			if (this.shouldSendSyncPackets()) {
				this.sendSyncPacket();
			}
		}

		if (updateDelay > 0) {
			updateDelay--;
			if (updateDelay == 0) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		/*
		if (worldObj.isRemote && this.needsToCauseBlockUpdates()) {
			updateTimer.update();
			if (updateTimer.checkCap()) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		 */
		ticksExisted++;
	}

	private boolean shouldSendSyncPacket() {
		return worldObj != null && worldObj.getTotalWorldTime()%this.getPacketDelay() == 0;
	}

	private void sendSyncPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeSyncTag(nbt);
		syncTag.setData(this, this.shouldFullSync(), nbt);
		forceSync = false;
		if (!syncTag.isEmpty()) {
			worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
			int r = this.shouldFullSync() ? 128 : this.getUpdatePacketRadius();
			int dim = worldObj.provider.dimensionId;
			//PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, r, dim, syncTag);
			this.sendPacketToAllAround(syncTag, r);
			//DragonAPIInit.instance.getModLogger().debug("Packet "+syncTag+" sent from "+this);
		}
	}

	public int getUpdatePacketRadius() {
		return 32;
	}

	protected boolean shouldRunUpdateCode() {
		return true;//!shutDown && !worldObj.isRemote;
	}

	protected final boolean shouldSendSyncPackets() {
		return !worldObj.isRemote;
	}

	private void writeError(Throwable e) {
		if (DragonOptions.CHATERRORS.getState()) {
			ReikaChatHelper.write(this+" is throwing "+e.getClass()+" on update: "+e.getMessage());
			ReikaChatHelper.write(Arrays.toString(e.getStackTrace()));
			ReikaChatHelper.write("");
		}

		ReikaJavaLibrary.pConsole(this+" is throwing "+e.getClass()+" on update: "+e.getMessage());
		e.printStackTrace();
		ReikaJavaLibrary.pConsole("");
	}

	private final void updateTileEntity() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//rmb = this.getTEModel(worldObj, xCoord, yCoord, zCoord);
		if (worldObj.isRemote)
			this.animateWithTick(worldObj, xCoord, yCoord, zCoord);
		if (this.getTicksExisted() == 0) {
			for (int i = 0; i < 6; i++)
				this.updateCache(dirs[i]);
			if (ModList.OPENCOMPUTERS.isLoaded()) {
				if (node instanceof Component && ((Component)node).network() == null)
					Network.joinOrCreateNetwork(this);
			}
			this.onFirstTick(worldObj, xCoord, yCoord, zCoord);
		}
	}

	protected void onFirstTick(World world, int x, int y, int z) {

	}

	public Random getRandom() {
		return rand;
	}

	/** Returns true if the other TileEntity is the same type at the same coordinates, but not necessarily are the same instance. */
	public final boolean isSameTile(Object o) {
		if (o == this)
			return true;
		if (o.getClass() == this.getClass()) {
			TileEntity te = (TileEntity)o;
			if ((te.worldObj == null && worldObj != null) || (worldObj == null && te.worldObj != null))
				return false;
			return te.worldObj.provider.dimensionId == worldObj.provider.dimensionId && this.matchCoords(te) && te.isInvalid() == this.isInvalid();
		}
		return false;
	}

	@Override
	public final boolean equals(Object o) {
		if (o != this && this.isSameTile(o)) {
			ReikaJavaLibrary.pConsole("TileEntities would be equal functionally but not in identity!"); //debug code
			Thread.dumpStack();
		}
		return super.equals(o);
	}

	@Override
	public final int hashCode() {
		//int base = worldObj != null ? (xCoord + (zCoord << 8) + (yCoord << 16) + (worldObj.provider.dimensionId << 24)) : super.hashCode();
		//return tileEntityInvalid ? -base : base;
		return super.hashCode();
	}

	private boolean matchCoords(TileEntity te) {
		return te.xCoord == xCoord && te.yCoord == yCoord && te.zCoord == zCoord;
	}

	@Override
	public String toString() {
		String base = "Tile Entity "+this.getTEName();
		return base+(this.isInWorld() ? " @ DIM"+worldObj.provider.dimensionId+": "+xCoord+", "+yCoord+", "+zCoord : " (item)");
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

	protected final void delete() {
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
	}

	public final TileEntity getAdjacentTileEntity(ForgeDirection dir) {
		if (this.cachesTEs()) {
			return this.getCachedTE(dir);
		}
		else {
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			if (!ReikaWorldHelper.tileExistsAt(worldObj, dx, dy, dz))
				return null;
			return worldObj.getTileEntity(dx, dy, dz);
		}
	}

	public final TileEntity getTileEntity(int x, int y, int z) {
		if (!ReikaWorldHelper.tileExistsAt(worldObj, x, y, z))
			return null;
		return worldObj.getTileEntity(x, y, z);
	}

	public final boolean isDirectlyAdjacent(int x, int y, int z) {
		return Math.abs(x-xCoord)+Math.abs(y-yCoord)+Math.abs(z-zCoord) == 1;
	}

	private boolean cachesTEs() {
		return this.getBlockType() instanceof BlockTEBase;
	}

	private TileEntity getCachedTE(ForgeDirection dir) {
		return dir != null ? adjTEMap[dir.ordinal()] : null;
	}

	public final void updateCache(ForgeDirection dir) {
		TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		/*if (te instanceof SpaceRift) {
			te = ((SpaceRift)te).getTileEntityFrom(dir);
		}*/
		adjTEMap[dir.ordinal()] = te;
		this.onPlacedNextToThis(te, dir);
	}

	protected void onPlacedNextToThis(TileEntity te, ForgeDirection dir) {

	}

	public final int getObjectID() {
		return System.identityHashCode(this);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		switch(ReikaRenderHelper.getRenderDistance()) {
		case FAR:
			return 6144D;
		case NORMAL:
			return 3364D;
		case SHORT:
			return 1024D;
		case TINY:
			return 256D;
		default:
			return 4096D;
		}
	}


	private final HashMap<Integer, LuaMethod> luaMethods = new HashMap();
	private final HashMap<String, LuaMethod> methodNames = new HashMap();
	private final Object node = this.createNode();

	/** ComputerCraft */
	@Override
	public String[] getMethodNames() {
		ArrayList<LuaMethod> li = new ArrayList();
		Collection<LuaMethod> all = LuaMethod.getMethods();
		for (LuaMethod l : all) {
			if (l.isValidFor(this))
				li.add(l);
		}
		String[] s = new String[li.size()];
		for (int i = 0; i < s.length; i++) {
			LuaMethod l = li.get(i);
			s[i] = l.displayName;
			luaMethods.put(i, l);
			methodNames.put(l.displayName, l);
		}
		return s;
	}

	@ModDependent(ModList.COMPUTERCRAFT)
	public final boolean equals(IPeripheral other) {
		return other == this;
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return luaMethods.containsKey(method) ? luaMethods.get(method).invoke(this, arguments) : null;
	}

	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {}
	@Override
	@ModDependent(ModList.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {}

	@Override
	public String getType() {
		return this.getName().replaceAll(" ", "");
	}

	protected String getName() {
		return this.getTEName();
	}

	/** OpenComputers */
	public String getComponentName() {
		return this.getType();
	}

	@Override
	public String[] methods() {
		return this.getMethodNames();
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		Object[] objs = new Object[args.count()];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = args.checkAny(i);
			if (objs[i] instanceof byte[])
				objs[i] = new String((byte[])objs[i]);
		}
		return methodNames.containsKey(method) ? methodNames.get(method).invoke(this, objs) : null;
	}

	@Override
	public final void onChunkUnload() {
		super.onChunkUnload();

		for (int i = 0; i < 6; i++)
			adjTEMap[i] = null;

		if (ModList.OPENCOMPUTERS.isLoaded()) {
			if (node instanceof Component)
				((Component)node).remove();
		}

		this.onInvalidateOrUnload(worldObj, xCoord, yCoord, zCoord, false);
	}

	@Override
	public final void invalidate() {
		super.invalidate();

		for (int i = 0; i < 6; i++)
			adjTEMap[i] = null;

		if (ModList.OPENCOMPUTERS.isLoaded()) {
			if (node instanceof Component)
				((Component)node).remove();
		}

		this.onInvalidateOrUnload(worldObj, xCoord, yCoord, zCoord, true);
	}

	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {

	}

	private Object createNode() {
		if (ModList.OPENCOMPUTERS.isLoaded())
			return Network.newNode(this, Visibility.Network).withComponent(this.getType(), this.getOCNetworkVisibility()).create();
		else
			return null;
	}

	@ModDependent(ModList.OPENCOMPUTERS)
	protected Visibility getOCNetworkVisibility() {
		return Visibility.Network;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public Node node() {
		return (Node)node;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public void onConnect(Node node) {}
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public void onDisconnect(Node node) {}
	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public void onMessage(Message message) {}
}
