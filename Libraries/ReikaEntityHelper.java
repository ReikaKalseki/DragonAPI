/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.TameHostile;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartItemHandler;

public final class ReikaEntityHelper extends DragonAPICore {

	public static final IEntitySelector itemSelector = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity e) {
			return e instanceof EntityItem;
		}
	};

	/** provides a mapping between an Entity Class and an entity ID */
	private static Map<Class, Integer> classToIDMapping = new HashMap();

	/** Maps entity names to their numeric identifiers */
	private static Map<String, Integer> stringToIDMapping = new HashMap();

	public static void loadMappings()
	{
		if (!classToIDMapping.isEmpty())
			return;
		try {
			Map map = EntityList.stringToIDMapping;
			for (Object key : EntityList.stringToClassMapping.keySet()) {
				String name = (String)key;
				Class c = (Class)EntityList.stringToClassMapping.get(name);
				//ReikaJavaLibrary.pConsole(name+":"+c);
				if (map.containsKey(name)) {
					int id = (Integer)map.get(name);
					classToIDMapping.put(c, id);
					stringToIDMapping.put(name, id);
				}
			}
			//ReikaJavaLibrary.pConsole(map);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static int[] mobColorArray = new int[201]; //Highest Entity ID (endercrystal)+1

	private static void setMobColors() {
		mobColorArray[50] = ReikaColorAPI.RGBtoHex(65, 183, 54);
		mobColorArray[51] = ReikaColorAPI.GStoHex(207); //Skeleton
		mobColorArray[52] = ReikaColorAPI.RGBtoHex(90, 71, 43); //Spider
		mobColorArray[53] = ReikaColorAPI.RGBtoHex(67, 109, 53); //Giant
		mobColorArray[54] = ReikaColorAPI.RGBtoHex(67, 109, 53); //Zombie
		mobColorArray[55] = ReikaColorAPI.RGBtoHex(90, 162, 68); //Slime
		mobColorArray[56] = ReikaColorAPI.GStoHex(240); //Ghast
		mobColorArray[57] = ReikaColorAPI.RGBtoHex(181, 131, 131); //PigZombie
		mobColorArray[58] = ReikaColorAPI.RGBtoHex(204, 15, 248); //Enderman
		mobColorArray[59] = ReikaColorAPI.RGBtoHex(18, 77, 90); //Cave Spider
		mobColorArray[60] = ReikaColorAPI.GStoHex(140); //Silverfish
		mobColorArray[61] = ReikaColorAPI.RGBtoHex(235, 180, 26); //Blaze
		mobColorArray[62] = ReikaColorAPI.RGBtoHex(84, 14, 0); //LavaSlime
		mobColorArray[63] = ReikaColorAPI.RGBtoHex(224, 121, 250); //Dragon
		mobColorArray[64] = ReikaColorAPI.GStoHex(79); //Wither
		mobColorArray[65] = ReikaColorAPI.RGBtoHex(118, 100, 61); //Bat
		mobColorArray[66] = ReikaColorAPI.RGBtoHex(163, 148, 131); //Witch

		mobColorArray[90] = ReikaColorAPI.RGBtoHex(238, 158, 158); //Pig
		mobColorArray[91] = ReikaColorAPI.GStoHex(214); //Sheep
		mobColorArray[92] = ReikaColorAPI.RGBtoHex(67, 53, 37); //Cow
		mobColorArray[93] = ReikaColorAPI.RGBtoHex(193, 147, 67); //Chicken
		mobColorArray[94] = ReikaColorAPI.RGBtoHex(83, 108, 127); //Squid
		mobColorArray[95] = ReikaColorAPI.RGBtoHex(183, 179, 180); //Wolf
		mobColorArray[96] = ReikaColorAPI.RGBtoHex(151, 3, 4); //Mooshroom
		mobColorArray[97] = ReikaColorAPI.RGBtoHex(226, 143, 34); //Snow Golem
		mobColorArray[98] = ReikaColorAPI.RGBtoHex(242, 197, 110); //Ocelot
		mobColorArray[99] = ReikaColorAPI.RGBtoHex(208, 185, 168); //Iron Golem

		mobColorArray[120] = ReikaColorAPI.RGBtoHex(178, 122, 98); //Villager


	}

	/** Returns true if the mob is a hostile one. Args: EntityLivingBase mob */
	public static boolean isHostile(EntityLivingBase mob) {
		if (mob instanceof TameHostile)
			return false;
		if (mob instanceof EntityMob)
			return true;
		if (mob instanceof IMob)
			return true;
		if (mob instanceof EntityGhast)
			return true;
		if (mob instanceof EntitySlime)
			return true;
		if (mob instanceof EntityWitch)
			return true;
		if (mob instanceof EntityDragon)
			return true;
		if (mob instanceof EntityWither)
			return true;
		if (mob.getClass().getSimpleName().toLowerCase().contains("wisp"))
			return true;
		if (mob.getClass().getSimpleName().toLowerCase().contains("pech"))
			return true;
		return false;
	}

	public static boolean isHostile(Class<? extends EntityLiving> mob) {
		if (TameHostile.class.isAssignableFrom(mob))
			return false;
		if (EntityMob.class.isAssignableFrom(mob))
			return true;
		if (EntityGhast.class.isAssignableFrom(mob))
			return true;
		if (EntitySlime.class.isAssignableFrom(mob))
			return true;
		if (EntityWitch.class.isAssignableFrom(mob))
			return true;
		if (EntityDragon.class.isAssignableFrom(mob))
			return true;
		if (EntityWither.class.isAssignableFrom(mob))
			return true;
		if (mob.getSimpleName().toLowerCase().contains("wisp"))
			return true;
		if (mob.getSimpleName().toLowerCase().contains("pech"))
			return true;
		return false;
	}

	/** Converts a string mobname to its respective id. Args: Name */
	public static int mobNameToID(String name) {
		return stringToIDMapping.get(name);
	}

	/** Converts a string mobname to its respective class file. Args: Name */
	public static Class mobNameToClass(String name) {
		return (Class)EntityList.stringToClassMapping.get(name);
	}

	/** Returns the number of mob-type entities in MineCraft. Args: World */
	public static int getNumberMobsInMC(World world) {
		int highestid = 0;
		int numberentities = EntityList.IDtoClassMapping.size();
		Object[] entityKeys = EntityList.IDtoClassMapping.keySet().toArray();
		int[] entityIDs = new int[entityKeys.length];
		for (int i = 0; i < entityKeys.length; i++) {
			entityIDs[i] = Integer.valueOf(String.valueOf(entityKeys[i]));
		}
		for (int i = 0; i < entityIDs.length; i++) {
			if (entityIDs[i] > highestid) {
				highestid = entityIDs[i];
			}
		}
		int numbermobs = 0;
		for (int id = 0; id <= highestid; id++) {
			if (EntityList.IDtoClassMapping.containsKey(id) && id != 48 && id != 49) { //ID 48,49 is "Mob","Monster" -> EntityLivingBase.class, EntityMob.class
				Entity ent = EntityList.createEntityByID(id, world);
				if (ent instanceof EntityLivingBase)
					numbermobs++;
			}
		}
		return numbermobs;
	}

	/** Returns the mass (in kg) of the entity. Args: Entity */
	public static double getEntityMass(Entity ent) {
		if (ent instanceof EntityItem || ent instanceof EntityXPOrb)
			return 0.25;
		if (ent instanceof EntityCreeper)
			return 100; //220 lbs; TNT is heavy
		if (ent instanceof EntitySkeleton)
			return 30;	//66 lbs
		if (ent instanceof EntityZombie || ent instanceof EntityPlayer || ent instanceof EntityVillager || ent instanceof EntityWitch)
			return 70; // 180 lbs
		if (ent instanceof EntityPigZombie)
			return 90;
		if (ent instanceof EntitySpider)
			return 60;	//
		if (ent instanceof EntityPig)
			return 100;
		if (ent instanceof EntityCow || ent instanceof EntityMooshroom)
			return 350;
		if (ent instanceof EntityGhast)
			return 20; //spirit creature
		if (ent instanceof EntityBlaze)
			return 300;
		if (ent instanceof EntityMagmaCube) {
			EntityMagmaCube cube = (EntityMagmaCube)ent;
			return 400*cube.getSlimeSize()*cube.getSlimeSize();
		}
		if (ent instanceof EntitySlime) {
			EntitySlime cube = (EntitySlime)ent;
			return 200*cube.getSlimeSize()*cube.getSlimeSize();
		}
		if (ent instanceof EntityEnderman)
			return 40;
		if (ent instanceof EntitySilverfish)
			return 1;
		if (ent instanceof EntityChicken)
			return 2;
		if (ent instanceof EntityCaveSpider)
			return 30;
		if (ent instanceof EntityDragon)
			return 10000; //really conjectural
		if (ent instanceof EntityWither)
			return 3000;  //even more conjectural
		if (ent instanceof EntityWolf)
			return 50;
		if (ent instanceof EntityOcelot)
			return 15;
		if (ent instanceof EntityIronGolem)
			return 32000; //iron = 8g/cc, 4m^3 of it
		if (ent instanceof EntityGolem)
			return 100;
		if (ent instanceof EntitySheep)
			return 150;
		if (ent instanceof EntitySquid)
			return 120;
		if (ent instanceof EntityBat)
			return 0.5;
		if (ent instanceof EntityMinecart)
			return 400;
		if (ent instanceof EntityBoat)
			return 70;
		if (ent instanceof EntityTNTPrimed)
			return 2700; //2.7 g/cc
		if (ent instanceof EntityFallingBlock)
			return 2000; //2 g/cc
		return 100;
	}

	/** Returns an itemstack (size 1 item) of the entity's breeding Items. Args: Entity */
	public static ItemStack getBreedItem(EntityAnimal e) {
		if (e instanceof EntitySheep) {
			return new ItemStack(Items.wheat);
		}
		else if (e instanceof EntityCow) {
			return new ItemStack(Items.wheat);
		}
		else if (e instanceof EntityPig) {
			return new ItemStack(Items.carrot);
		}
		else if (e instanceof EntityChicken) {
			return new ItemStack(Items.wheat_seeds);
		}
		else if (e instanceof EntityWolf) {
			return new ItemStack(Items.porkchop);
		}
		return null;
	}

	/** Converts a mob ID to a color, based off the mob's color. Players return bright red.
	 * Args: Mob ID */
	public static int mobToColor(EntityLivingBase ent) {
		int id = EntityList.getEntityID(ent);
		if (ent instanceof EntityPlayer)
			return 0xffff0000;
		setMobColors();
		return mobColorArray[id];
	}

	/** Returns true if the given pitch falls within the given creature's hearing range. */
	public static boolean isHearingRange(long freq, EntityLivingBase ent) {
		if (ent instanceof EntityPlayer || ent instanceof EntityWitch || ent instanceof EntityZombie) {
			if (freq < 20)
				return false;
			if (freq > 20000)
				return false;
		}
		if (ent instanceof EntitySlime)
			return false; //deaf
		if (ent instanceof EntityZombie || ent instanceof EntitySkeleton) {
			if (freq < 20)
				return false;
			if (freq > 5000) //high-frequency hearing loss
				return false;
		}
		if (ent instanceof EntitySpider) {
			if (freq < 1000)
				return false;
			if (freq > 100000)
				return false;
		}
		if (ent instanceof EntityCreeper) {
			if (freq < 500)
				return false;
			if (freq > 40000)
				return false;
		}
		if (ent instanceof EntityGhast) {
			if (freq < 200)
				return false;
			if (freq > 10000)
				return false;
		}
		if (ent instanceof EntityPigZombie) { //Overlap of pig and zombie hearing ranges
			if (freq < 64)
				return false;
			if (freq > 5000)
				return false;
		}
		if (ent instanceof EntityEnderman) {
			if (freq < 5)
				return false;
			if (freq > 2000)
				return false;
		}
		if (ent instanceof EntityBlaze) {
			if (freq > 500)
				return false;
		}
		if (ent instanceof EntitySilverfish) {
			if (freq < 1000)
				return false;
			if (freq > 35000)
				return false;
		}
		if (ent instanceof EntityDragon) {
			if (freq < 5)
				return false;
			if (freq > 8000)
				return false;
		}
		if (ent instanceof EntityWither) {
			if (freq < 2)
				return false;
			if (freq > 10000)
				return false;
		}
		if (ent instanceof EntityPig) {
			if (freq < 64)
				return false;
			if (freq > 32000)
				return false;
		}
		if (ent instanceof EntityCow || ent instanceof EntityMooshroom) {
			if (freq < 23)
				return false;
			if (freq > 35000)
				return false;
		}
		if (ent instanceof EntityChicken) {
			if (freq < 125)
				return false;
			if (freq > 2000)
				return false;
		}
		if (ent instanceof EntitySheep) {
			if (freq < 100)
				return false;
			if (freq > 30000)
				return false;
		}
		if (ent instanceof EntityBat) {
			if (freq < 2000)
				return false;
			if (freq > 110000)
				return false;

		}
		if (ent instanceof EntityOcelot) {
			if (freq < 45)
				return false;
			if (freq > 64000)
				return false;
		}
		if (ent instanceof EntityWolf) {
			if (freq < 67)
				return false;
			if (freq > 45000)
				return false;
		}
		if (ent instanceof EntitySquid)
			if (freq > 500)
				return false;
		return true;
	}

	/** Knocks one entity away from another. Args: Attacker, target, power */
	public static void knockbackEntity(Entity a, Entity b, double power) {
		knockbackEntityFromPos(a.posX, a.posY, a.posZ, b, power);
	}

	/** Knocks an entity away from a position. Args: x, y, z, entity, power */
	public static void knockbackEntityFromPos(double x, double y, double z, Entity ent, double power) {
		double dx = x-ent.posX;
		//double dy = y-ent.posY;
		double dz = z-ent.posZ;
		double dd = ReikaMathLibrary.py3d(dx, 0, dz);
		ent.motionX -= dx/dd/2*power;
		//ent.motionY -= dy/10;
		if (ent.onGround || ent.posY > y)
			ent.motionY += 0.4*power;
		ent.motionZ -= dz/dd/2*power;
		//if (!ent.worldObj.isRemote)
		ent.velocityChanged = true;
	}

	/** Returns true if all EntityLivingBase within the list are dead. Args: List
	 * [The list MUST be of EntityLivingBase (or subclass) - any other type WILL cause
	 * a classcast exception!], test isDead only yes/no */
	public static boolean allAreDead(List mobs, boolean isDeadOnly) {
		for (int i = 0; i < mobs.size(); i++) {
			EntityLivingBase ent = (EntityLivingBase)mobs.get(i);
			if ((!ent.isDead && ent.getHealth() > 0) || (!ent.isDead && isDeadOnly))
				return false;
		}
		return true;
	}

	/** Adds a small velocity in a random direction (akin to items' speeds when dropped) */
	public static void addRandomDirVelocity(Entity ent, double max) {/*
    	ent.motionX = -max+2*max*rand.nextFloat();
    	ent.motionZ = -max+2*max*rand.nextFloat();
    	ent.motionY = 4*max*rand.nextFloat();*/
	}

	/** Drop an entity's head. Args: EntityLivingBase */
	public static void dropHead(EntityLivingBase e) {
		if (e == null)
			return;
		ItemStack is = null;
		if (e instanceof EntitySkeleton) {
			EntitySkeleton ek = (EntitySkeleton)e;
			if (ek.getSkeletonType() == 1) //Wither Skeleton
				is = new ItemStack(Items.skull, 1, 1);
			else
				is = new ItemStack(Items.skull, 1, 0);
		}
		if (e instanceof EntityZombie) {
			if (!(((EntityZombie)e).isVillager() || e instanceof EntityPigZombie))
				is = new ItemStack(Items.skull, 1, 2);
		}
		if (e instanceof EntityPlayer)
			is = new ItemStack(Items.skull, 1, 3);
		if (e instanceof EntityCreeper)
			is = new ItemStack(Items.skull, 1, 4);
		if (is == null)
			return;
		ReikaItemHelper.dropItem(e.worldObj, e.posX, e.posY+0.2, e.posZ, is);
	}

	public static ItemStack getFoodItem(EntityLivingBase e) {
		if (e instanceof EntityCow) {
			return new ItemStack(Items.beef);
		}
		else if (e instanceof EntityPig) {
			return new ItemStack(Items.porkchop);
		}
		else if (e instanceof EntityChicken) {
			return new ItemStack(Items.chicken);
		}
		else if (e instanceof EntitySheep) {
			if (ModList.DARTCRAFT.isLoaded()) {
				Item id = DartItemHandler.getInstance().meatID;
				if (id != null)
					return new ItemStack(id, 1, 0);
			}
		}
		else if (e instanceof EntityZombie) {
			return new ItemStack(Items.rotten_flesh);
		}
		else if (e instanceof EntityHorse) {
			return new ItemStack(Items.beef);
		}
		return null;
	}

	/** Spawns a bunch of particles around an entity. Args: Particle Type, Entity, number of particles */
	public static void spawnParticlesAround(String part, Entity e, int num) {
		for (int k = 0; k < num; k++)
			e.worldObj.spawnParticle(part, e.posX-0.6+1.2*rand.nextDouble(), e.posY+e.height/2-0.6+1.2*rand.nextDouble(), e.posZ-0.6+1.2*rand.nextDouble(), -0.2+0.4*rand.nextDouble(), 0.4*rand.nextDouble(), -0.2+0.4*rand.nextDouble());
	}

	/** Returns the Entity ID from entity class. Args: Entity Class */
	public static int getEntityIDByClass(Class cl) {
		String name = (String)EntityList.classToStringMapping.get(cl);
		return mobNameToID(name);
	}

	public static int getEntityID(Entity e) {
		if (e instanceof EntityPlayer) {
			return -1;
		}
		return EntityList.getEntityID(e);
	}

	public static boolean burnsInSun(EntityLivingBase e) {
		return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	/** If the entity is wearing a any piece of this armor material. */
	public static boolean isEntityWearingArmorOf(EntityLivingBase e, ArmorMaterial type) {
		for (int i = 1; i <= 4; i++) {
			ItemStack is = e.getEquipmentInSlot(i);
			if (is != null && is.getItem() instanceof ItemArmor) {
				ItemArmor a = (ItemArmor)is.getItem();
				if (a.getArmorMaterial() == type)
					return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	public static boolean isEntityWearingFullSuitOf(EntityLivingBase e, ArmorMaterial type) {
		for (int i = 1; i <= 4; i++) {
			ItemStack is = e.getEquipmentInSlot(i);
			if (is != null && is.getItem() instanceof ItemArmor) {
				ItemArmor a = (ItemArmor)is.getItem();
				if (a.getArmorMaterial() != type)
					return false;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public static Render getEntityRenderer(Class entityClass) {
		return (Render)RenderManager.instance.entityRenderMap.get(entityClass);
	}

	public static void setNoPotionParticles(EntityLivingBase e) {
		e.getDataWatcher().updateObject(7, 0);
	}

	public static void setInvulnerable(Entity e, boolean invuln) {
		NBTTagCompound NBT = new NBTTagCompound();
		e.writeToNBT(NBT);
		NBT.setBoolean("Invulnerable", invuln);
		e.readFromNBT(NBT);
	}

	public static boolean isLivingMob(String mob, boolean allowPlayers) {
		Class c = allowPlayers ? EntityLivingBase.class : EntityLiving.class;
		return c.isAssignableFrom((Class)EntityList.stringToClassMapping.get(mob));
	}

	public static boolean hasID(String mob) {
		return stringToIDMapping.containsKey(mob);
	}

	public static boolean hasID(Class<? extends Entity> c) {
		return classToIDMapping.containsKey(c);
	}

	public static String getEntityDisplayName(String name) {
		return StatCollector.translateToLocal("entity."+name+".name");
	}

	public static boolean isTameHostile(String mob) {
		return TameHostile.class.isAssignableFrom((Class)EntityList.stringToClassMapping.get(mob));
	}

	public static void overrideEntity(Class mobClass, String name, int entityID) {
		EntityEggInfo info = (EntityEggInfo)EntityList.entityEggs.get(entityID);
		removeEntityMapping(mobClass, name, entityID);
		EntityList.addMapping(mobClass, name, entityID);
		if (info != null) {
			EntityList.entityEggs.put(entityID, info);
		}
	}

	private static void removeEntityMapping(Class mobClass, String name, int entityID) {
		EntityList.stringToClassMapping.remove(name);
		EntityList.entityEggs.remove(entityID);
		EntityList.IDtoClassMapping.remove(entityID);
	}

	private static EntityCreeper dummy;

	public static EntityLivingBase getDummyMob(World world) {
		if (dummy == null)
			dummy = new EntityCreeper(world);
		return dummy;
	}

	public static void sortEntityListByDistance(List<Entity> li, double x, double y, double z) {
		Collections.sort(li, new EntityDistanceComparator(x, y, z));
	}

	public static final class EntityDistanceComparator implements Comparator<Entity> {

		public final double posX;
		public final double posY;
		public final double posZ;

		public EntityDistanceComparator(double x, double y, double z) {
			posX = x;
			posY = y;
			posZ = z;
		}

		@Override
		public int compare(Entity e1, Entity e2) {
			double dd1 = ReikaMathLibrary.py3d(e1.posX-posX, e1.posY-posY, e1.posZ-posZ);
			double dd2 = ReikaMathLibrary.py3d(e2.posX-posX, e2.posY-posY, e2.posZ-posZ);
			return dd1 > dd2 ? 1 : dd1 < dd2 ? -1 : 0;
		}

	}

	public static boolean isBossMob(EntityLiving e) {
		if (e instanceof EntityWither || e instanceof EntityDragon)
			return true;
		String name = e.getClass().getName().toLowerCase();
		if (name.contains("voidmonster"))
			return false;
		if (name.startsWith("twilightforest.entity.boss"))
			return true;
		return false;
	}

	public static void transferEntityToDimension(Entity e, int to_dim) {
		transferEntityToDimension(e, to_dim, null);
	}

	public static void transferEntityToDimension(Entity e, int to_dim, Teleporter t) {
		if (!e.isDead) {
			if (e instanceof EntityPlayerMP) {
				transferPlayerToDimension((EntityPlayerMP)e, to_dim, t);
			}
			else {
				if (!e.worldObj.isRemote) {
					e.worldObj.theProfiler.startSection("changeDimension");
					MinecraftServer ms = MinecraftServer.getServer();
					int from_dim = e.dimension;
					WorldServer from = ms.worldServerForDimension(from_dim);
					WorldServer to = ms.worldServerForDimension(to_dim);
					e.dimension = to_dim;
					e.worldObj.removeEntity(e);
					e.isDead = false;
					e.worldObj.theProfiler.startSection("reposition");
					ms.getConfigurationManager().transferEntityToWorld(e, from_dim, from, to, t != null ? t : new Teleporter(to));
					e.worldObj.theProfiler.endStartSection("reloading");
					Entity copy = EntityList.createEntityByName(EntityList.getEntityString(e), to);

					if (copy != null) {
						copy.copyDataFrom(e, true);
						to.spawnEntityInWorld(copy);
					}

					e.isDead = true;
					e.worldObj.theProfiler.endSection();
					from.resetUpdateEntityTick();
					to.resetUpdateEntityTick();
					e.worldObj.theProfiler.endSection();
				}
				else {
					e.dimension = to_dim;
				}
			}
		}
	}

	private static void transferPlayerToDimension(EntityPlayerMP ep, int to_dim, Teleporter t) {
		MinecraftServer mcs = ep.mcServer;
		if (ep.timeUntilPortal > 0)
			ep.timeUntilPortal = 10;
		mcs.getConfigurationManager().transferPlayerToDimension(ep, to_dim, t != null ? t : new Teleporter(mcs.worldServerForDimension(to_dim)));
	}

	public static <T extends Entity> T getNearestEntityOfSameType(T e, int r) {
		return (T)e.worldObj.findNearestEntityWithinAABB(e.getClass(), ReikaAABBHelper.getEntityCenteredAABB(e, 24), e);
	}

	public static void decrEntityItemStack(EntityItem ei, int rem) {
		ItemStack is = ei.getEntityItem();
		is.stackSize -= rem;
		if (is.stackSize <= 0)
			ei.setDead();
	}

}
