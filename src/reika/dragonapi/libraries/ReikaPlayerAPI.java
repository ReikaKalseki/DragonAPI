/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.APIPacketHandler.PacketIDs;
import reika.dragonapi.instantiable.data.blockstruct.BlockArray;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.instantiable.event.GetPlayerLookEvent;
import reika.dragonapi.instantiable.event.PlayerHasItemEvent;
import reika.dragonapi.instantiable.event.RemovePlayerItemEvent;
import reika.dragonapi.instantiable.io.PacketTarget;
import reika.dragonapi.instantiable.io.PacketTarget.PlayerTarget;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaPlayerAPI extends DragonAPICore {

	private static final HashMap<String, FakePlayer> fakePlayers = new HashMap();
	private static final HashMap<String, UUID> uuidMap = new HashMap();

	/** Transfers a player's entire inventory to an inventory. Args: Player, Inventory */
	public static void transferInventoryToChest(EntityPlayer ep, ItemStack[] inv) {
		int num = ReikaInventoryHelper.getTotalUniqueStacks(ep.inventory.mainInventory);
		if (num >= inv.length)
			return;
	}

	/** Clears a player's hotbar. Args: Player */
	public static void clearHotbar(EntityPlayer ep) {
		for (int i = 0; i < 9; i++)
			ep.inventory.mainInventory[i] = null;
	}

	/** Clears a player's inventory. Args: Player */
	public static void clearInventory(EntityPlayer ep) {
		for (int i = 0; i < ep.inventory.mainInventory.length; i++)
			ep.inventory.mainInventory[i] = null;
	}

	/** Sorts a player's inventory. Args: Player, boolean hotbar only */
	public static void cleanInventory(EntityPlayer ep, boolean hotbar) {

	}

	/** Get the block a player is looking at. Args: Player, Range, Detect 'soft' blocks yes/no *//*
	public static MovingObjectPosition getLookedAtBlock(EntityPlayer ep, int range, boolean hitSoft) {/*
		Vec3 norm = ep.getLookVec();
		World world = ep.worldObj;
		for (float i = 0; i <= range; i += 0.2) {
			int[] xyz = ReikaVectorHelper.getPlayerLookBlockCoords(ep, i);
			Block b = worldc.getBlock();
			if (b != Blocks.air) {
				boolean isSoft = ReikaWorldHelper.softBlocks(world, c.xCoord, c.yCoord, c.zCoord);
				if (hitSoft || !isSoft) {
					return new MovingObjectPosition(c.xCoord, c.yCoord, c.zCoord, 0, norm);
				}
			}
		}
		return null;*//*
		return getLookedAtBlock()
	}*/

	public static MovingObjectPosition getLookedAtBlock(EntityPlayer ep, double reach, boolean liq) {
		Vec3 vec = Vec3.createVectorHelper(ep.posX, (ep.posY + 1.62) - ep.yOffset, ep.posZ);
		Vec3 vec2 = ep.getLook(1.0F);
		Vec3 vec3 = vec.addVector(vec2.xCoord*reach, vec2.yCoord*reach, vec2.zCoord*reach);
		MovingObjectPosition hit = ep.worldObj.rayTraceBlocks(vec, vec3, liq);
		GetPlayerLookEvent evt = new GetPlayerLookEvent(ep, hit, vec, vec3);
		MinecraftForge.EVENT_BUS.post(evt);
		hit = evt.newLook;
		if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK)
			return hit;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static MovingObjectPosition getLookedAtBlockClient(double reach, boolean liq) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		return getLookedAtBlock(ep, reach, liq);
	}

	public static FakePlayer getFakePlayerByNameAndUUID(WorldServer world, String name, UUID uuid) {
		FakePlayer fp = fakePlayers.get(name);
		if (fp == null) {
			fp = FakePlayerFactory.get(world, new GameProfile(uuid, name));
			fakePlayers.put(name, fp);
		}
		return fp;
	}

	private static boolean isAdmin(WorldServer world, String name, UUID uuid) {
		FakePlayer fp = getFakePlayerByNameAndUUID(world, name, uuid);
		return isAdmin(fp);
	}

	public static boolean isAdmin(EntityPlayerMP ep) {
		return MinecraftServer.getServer().getConfigurationManager().func_152596_g(ep.getGameProfile());
	}

	/** Hacky, but it works */
	public static void setPlayerWalkSpeed(EntityPlayer ep, float speed) {
		PlayerCapabilities pc = ep.capabilities;
		NBTTagCompound nbt = new NBTTagCompound();
		pc.writeCapabilitiesToNBT(nbt);
		nbt.setFloat("walkSpeed", speed);
		pc.readCapabilitiesFromNBT(nbt);
	}

	/** Returns true if the player has the given ID and metadata in their inventory, or is in creative mode.
	 * Args: Player, stack */
	public static boolean playerHasOrIsCreative(EntityPlayer ep, ItemStack is) {
		if (ep.capabilities.isCreativeMode)
			return true;
		PlayerHasItemEvent evt = new PlayerHasItemEvent(ep, is);
		switch (evt.getResult()) {
			case DENY:
				return false;
			case ALLOW:
				return true;
			default:
				break;
		}
		ItemStack[] ii = ep.inventory.mainInventory;
		if (is.getItem().getHasSubtypes() && is.getItemDamage() != OreDictionary.WILDCARD_VALUE)
			return ReikaInventoryHelper.checkForItemStack(is, ii, false);
		else
			return ReikaInventoryHelper.checkForItem(is.getItem(), ii);
	}

	public static boolean playerHasOrIsCreative(EntityPlayer ep, Item id) {
		return playerHasOrIsCreative(ep, new ItemStack(id));
	}

	public static boolean playerHasOrIsCreative(EntityPlayer ep, Block id, int meta) {
		return playerHasOrIsCreative(ep, new ItemStack(id, 1, meta));
	}

	public static void setFoodLevel(EntityPlayer ep, int level) {
		NBTTagCompound NBT = new NBTTagCompound();
		ep.getFoodStats().writeNBT(NBT);
		NBT.setInteger("foodLevel", level);
		ep.getFoodStats().readNBT(NBT);
	}

	public static void setSaturationLevel(EntityPlayer ep, int level) {
		NBTTagCompound NBT = new NBTTagCompound();
		ep.getFoodStats().writeNBT(NBT);
		NBT.setFloat("foodSaturationLevel", level);
		ep.getFoodStats().readNBT(NBT);
	}

	public static boolean playerCanBreakAt(WorldServer world, BlockArray b, EntityPlayerMP ep) {
		for (int i = 0; i < b.getSize(); i++) {
			Coordinate c = b.getNthBlock(i);
			if (!playerCanBreakAt(world, c.xCoord, c.yCoord, c.zCoord, ep))
				return false;
		}
		return true;
	}

	public static boolean playerCanBreakAt(WorldServer world, int x, int y, int z, EntityPlayerMP ep) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return playerCanBreakAt(world, x, y, z, b, meta, ep);
	}

	public static boolean playerCanBreakAt(WorldServer world, int x, int y, int z, Block id, int meta, EntityPlayerMP ep) {
		if (ep == null) {
			DragonAPICore.logError("Cannot check permissions of a null player!");
			return false;
		}
		if (DragonAPICore.isSinglePlayer())
			return true;
		if (isAdmin(ep) && DragonOptions.ADMINPERMBYPASS.getState())
			return true;
		if (MinecraftServer.getServer().isBlockProtected(world, x, y, z, ep))
			return false;
		BreakEvent evt = new BreakEvent(x, y, z, world, id, meta, ep);
		MinecraftForge.EVENT_BUS.post(evt);
		return !evt.isCanceled();
	}

	public static boolean playerCanBreakAt(WorldServer world, int x, int y, int z, Block id, int meta, String name, UUID uuid) {
		if (name == null) {
			DragonAPICore.logError("Cannot check permissions of a null player!");
			return false;
		}
		if (DragonAPICore.isSinglePlayer())
			return true;
		if (isAdmin(world, name, uuid) && DragonOptions.ADMINPERMBYPASS.getState())
			return true;
		FakePlayer fp = getFakePlayerByNameAndUUID(world, name, uuid);
		if (MinecraftServer.getServer().isBlockProtected(world, x, y, z, fp))
			return false;
		BreakEvent evt = new BreakEvent(x, y, z, world, id, meta, fp);
		MinecraftForge.EVENT_BUS.post(evt);
		return !evt.isCanceled();
	}

	public static boolean playerCanBreakAt(WorldServer world, int x, int y, int z, String name, UUID uuid) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return playerCanBreakAt(world, x, y, z, b, meta, name, uuid);
	}

	public static void removeExperience(EntityPlayer ep, int xp) {
		while (xp > 0 && ep.experienceTotal > 0) {
			ep.addExperience(-1);
			if (ep.experience < 0) {
				ep.addExperienceLevel(-1);
				ep.experience = 0.95F;
			}
			xp--;
		}
	}

	public static void clearExperience(EntityPlayer ep) {
		ep.experience = 0;
		ep.experienceLevel = 0;
		ep.experienceTotal = 0;
	}

	public static void syncCustomData(EntityPlayerMP ep) {
		ReikaPacketHelper.sendNBTPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERDATSYNC.ordinal(), ep.getEntityData(), new PlayerTarget(ep));
	}

	public static void syncAttributes(EntityPlayerMP ep) {
		/*
		NBTTagCompound nbt = new NBTTagCompound();
		ServersideAttributeMap map = (ServersideAttributeMap)ep.getAttributeMap();
		Collection<IAttributeInstance> c = map.getAllAttributes();
		for (IAttributeInstance iai : c) {
			nbt.setDouble(iai.getAttribute().getAttributeUnlocalizedName(), iai.getAttributeValue());
		}
		ReikaPacketHelper.sendNBTPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERATTRSYNC.ordinal(), ep, nbt);*/
		Set set = ((ServersideAttributeMap)ep.getAttributeMap()).getAttributeInstanceSet();
		ep.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(ep.getEntityId(), set));
	}

	@SideOnly(Side.CLIENT)
	public static void syncCustomDataFromClient(EntityPlayer ep) {
		ReikaPacketHelper.sendNBTPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERDATSYNC_CLIENT.ordinal(), ep.getEntityData(), new PacketTarget.ServerTarget());
	}

	public static NBTTagCompound getDeathPersistentNBT(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		ep.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, nbt);
		return nbt;
	}

	public static void notifyAdmins(String s) {
		MinecraftServer ms = MinecraftServer.getServer();
		if (ms != null) {
			Collection<EntityPlayer> ops = getOps();
			for (EntityPlayer ep : ops) {
				ReikaChatHelper.sendChatToPlayer(ep, s);
			}
		}
	}

	public static Collection<EntityPlayer> getOps() {
		WorldServer[] w = MinecraftServer.getServer().worldServers;
		Collection<EntityPlayer> ops = new ArrayList();
		for (int i = 0; i < w.length; i++) {
			WorldServer ws = w[i];
			ops.addAll(getOps(ws));
		}
		return ops;
	}

	public static Collection<EntityPlayer> getOps(World world) {
		Collection<EntityPlayer> ops = new ArrayList();
		for (Object o : world.playerEntities) {
			EntityPlayerMP ep = (EntityPlayerMP)o;
			if (isAdmin(ep))
				ops.add(ep);
		}
		return ops;
	}

	public static EntityPlayer getPlayerByNameAnyWorld(String name) {
		for (World world : DimensionManager.getWorlds()) {
			EntityPlayer ep = world.getPlayerEntityByName(name);
			if (ep != null)
				return ep;
		}
		return null;
	}

	public static boolean isFakeOrNotInteractable(EntityPlayer ep, double x, double y, double z, double dist) {
		return isFake(ep) || ep.getDistanceSq(x, y, z) >= dist*dist;
	}

	public static boolean isFake(EntityPlayer ep) {
		if (ep instanceof FakePlayer)
			return true;
		if (ep.getCommandSenderName().contains("CoFH") || ep.getCommandSenderName().contains("Thaumcraft"))
			return true;
		if (ep.getClass().getName().toLowerCase(Locale.ENGLISH).contains("fake"))
			return true;
		return false;
	}

	public static boolean isReika(EntityPlayer ep) {
		return ep.getUniqueID().equals(DragonAPICore.Reika_UUID);
	}
	/*
	public static UUID getUUIDByUsername(String name) {
		UUID id = uuidMap.get(name);
		if (id == null) {

		}
		return id;
	}
	 */

	public static List<EntityPlayerMP> getPlayersWithin(World world, AxisAlignedBB box) {
		ArrayList<EntityPlayerMP> li = new ArrayList();
		for (Object o : world.playerEntities) {
			if (o instanceof EntityPlayerMP && ((EntityPlayerMP)o).boundingBox.intersectsWith(box)) {
				EntityPlayerMP ep = (EntityPlayerMP)o;
				if (ep.boundingBox.intersectsWith(box)) {
					li.add(ep);
				}
			}
		}
		return li;
	}

	public static void kickPlayer(EntityPlayerMP ep, String reason) {
		ep.playerNetServerHandler.kickPlayerFromServer(reason);
	}

	@SideOnly(Side.CLIENT)
	public static void kickPlayerClientside(EntityPlayer ep, String reason) {
		ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERKICK.ordinal(), reason, new PacketTarget.ServerTarget());
	}

	public static void addOrDropItem(ItemStack is, EntityPlayer ep) {
		if (!ReikaInventoryHelper.addToIInv(is, ep.inventory))
			ReikaItemHelper.dropItem(ep, is);
	}

	public static void findAndDecrItem(EntityPlayer ep, Block b, int meta) {
		findAndDecrItem(ep, new ItemStack(b, 1, meta));
	}

	public static void findAndDecrItem(EntityPlayer ep, Item i, int meta) {
		findAndDecrItem(ep, new ItemStack(i, 1, meta));
	}

	public static void findAndDecrItem(EntityPlayer ep, ItemStack is) {
		if (MinecraftForge.EVENT_BUS.post(new RemovePlayerItemEvent(ep, is)))
			return;
		int slot = ReikaInventoryHelper.locateInInventory(is, ep.inventory.mainInventory, false);
		if (slot != -1) {
			ReikaInventoryHelper.decrStack(slot, ep.inventory.mainInventory);
		}
	}
}
