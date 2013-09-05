/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class ReikaEntityHelper extends DragonAPICore {

	/** provides a mapping between an Entity Class and an entity ID */
	private static Map classToIDMapping = new HashMap();

	/** Maps entity names to their numeric identifiers */
	private static Map stringToIDMapping = new HashMap();

	static
	{
		addMapping(EntityItem.class, "Item", 1);
		addMapping(EntityXPOrb.class, "XPOrb", 2);
		addMapping(EntityPainting.class, "Painting", 9);
		addMapping(EntityArrow.class, "Arrow", 10);
		addMapping(EntitySnowball.class, "Snowball", 11);
		addMapping(EntityLargeFireball.class, "Fireball", 12);
		addMapping(EntitySmallFireball.class, "SmallFireball", 13);
		addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
		addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
		addMapping(EntityPotion.class, "ThrownPotion", 16);
		addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
		addMapping(EntityItemFrame.class, "ItemFrame", 18);
		addMapping(EntityWitherSkull.class, "WitherSkull", 19);
		addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
		addMapping(EntityFallingSand.class, "FallingSand", 21);
		addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
		addMapping(EntityBoat.class, "Boat", 41);
		addMapping(EntityMinecartEmpty.class, "MinecartRideable", 42);
		addMapping(EntityMinecartChest.class, "MinecartChest", 43);
		addMapping(EntityMinecartFurnace.class, "MinecartFurnace", 44);
		addMapping(EntityMinecartTNT.class, "MinecartTNT", 45);
		addMapping(EntityMinecartHopper.class, "MinecartHopper", 46);
		addMapping(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
		addMapping(EntityLiving.class, "Mob", 48);
		addMapping(EntityMob.class, "Monster", 49);
		addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
		addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
		addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
		addMapping(EntityGiantZombie.class, "Giant", 53);
		addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
		addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
		addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
		addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
		addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
		addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
		addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
		addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
		addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
		addMapping(EntityDragon.class, "EnderDragon", 63);
		addMapping(EntityWither.class, "WitherBoss", 64);
		addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
		addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
		addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
		addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
		addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
		addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
		addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
		addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
		addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
		addMapping(EntitySnowman.class, "SnowMan", 97);
		addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
		addMapping(EntityIronGolem.class, "VillagerGolem", 99);
		addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
		addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
	}

	private static void addMapping(Class par0Class, String par1Str, int par2)
	{
		classToIDMapping.put(par0Class, Integer.valueOf(par2));
		stringToIDMapping.put(par1Str, Integer.valueOf(par2));
	}

	private static void addMapping(Class par0Class, String par1Str, int par2, int a, int b)
	{
		addMapping(par0Class, par1Str, par2);
	}

	private static int[] mobColorArray = new int[201]; //Highest Entity ID (endercrystal)+1

	private static void setMobColors() {
		mobColorArray[50] = ReikaColorAPI.RGBtoHex(65, 183, 54);
		mobColorArray[51] = ReikaColorAPI.RGBtoHex(207); //Skeleton
		mobColorArray[52] = ReikaColorAPI.RGBtoHex(90, 71, 43); //Spider
		mobColorArray[53] = ReikaColorAPI.RGBtoHex(67, 109, 53); //Giant
		mobColorArray[54] = ReikaColorAPI.RGBtoHex(67, 109, 53); //Zombie
		mobColorArray[55] = ReikaColorAPI.RGBtoHex(90, 162, 68); //Slime
		mobColorArray[56] = ReikaColorAPI.RGBtoHex(240); //Ghast
		mobColorArray[57] = ReikaColorAPI.RGBtoHex(181, 131, 131); //PigZombie
		mobColorArray[58] = ReikaColorAPI.RGBtoHex(204, 15, 248); //Enderman
		mobColorArray[59] = ReikaColorAPI.RGBtoHex(18, 77, 90); //Cave Spider
		mobColorArray[60] = ReikaColorAPI.RGBtoHex(140); //Silverfish
		mobColorArray[61] = ReikaColorAPI.RGBtoHex(235, 180, 26); //Blaze
		mobColorArray[62] = ReikaColorAPI.RGBtoHex(84, 14, 0); //LavaSlime
		mobColorArray[63] = ReikaColorAPI.RGBtoHex(224, 121, 250); //Dragon
		mobColorArray[64] = ReikaColorAPI.RGBtoHex(79); //Wither
		mobColorArray[65] = ReikaColorAPI.RGBtoHex(118, 100, 61); //Bat
		mobColorArray[66] = ReikaColorAPI.RGBtoHex(163, 148, 131); //Witch

		mobColorArray[90] = ReikaColorAPI.RGBtoHex(238, 158, 158); //Pig
		mobColorArray[91] = ReikaColorAPI.RGBtoHex(214); //Sheep
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

	/** Returns true if the mob is a hostile one. Args: EntityLiving mob */
	public static boolean isHostile(EntityLiving mob) {
		if (mob instanceof EntityMob)
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
		return false;
	}

	/** Converts a string mobname to its respective id. Args: Name, world */
	public static int mobNameToID(String name) {
		return (Integer)stringToIDMapping.get(name);
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
			if (EntityList.IDtoClassMapping.containsKey(id) && id != 48 && id != 49) { //ID 48,49 is "Mob","Monster" -> EntityLiving.class, EntityMob.class
				Entity ent = EntityList.createEntityByID(id, world);
				if (ent instanceof EntityLiving)
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
		if (ent instanceof EntityFallingSand)
			return 2000; //2 g/cc
		return 0.1;
	}

	/** Returns an itemstack (size 1 item) of the entity's breeding item. Args: Entity */
	public static ItemStack getBreedItem(EntityAnimal ent) {
		int id;
		int meta;
		ItemStack item = null;
		for (id = 256; id < Item.itemsList.length; id++) {
			if (ReikaItemHelper.hasMetadata(id)) {
				for (meta = 0; meta < 15; meta++) {
					item = new ItemStack(id, 1, meta);
					if (ent.isBreedingItem(item))
						return item;
				}
			}
			else {
				item = new ItemStack(id, 1, 0);
				if (ent.isBreedingItem(item))
					return item;
			}
		}
		return null;
	}

	/** Converts a mob ID to a color, based off the mob's color. Players return bright red.
	 * Args: Mob ID */
	public static int mobToColor(EntityLiving ent) {
		int id = EntityList.getEntityID(ent);
		if (ent instanceof EntityPlayer)
			return 0xffff0000;
		setMobColors();
		return mobColorArray[id];
	}

	/** Returns true if the given pitch falls within the given creature's hearing range. */
	public static boolean isHearingRange(long freq, EntityLiving ent) {
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

	/** Returns true if all EntityLiving within the list are dead. Args: List
	 * [The list MUST be of EntityLiving (or subclass) - any other type WILL cause
	 * a classcast exception!], test isDead only yes/no */
	public static boolean allAreDead(List mobs, boolean isDeadOnly) {
		for (int i = 0; i < mobs.size(); i++) {
			EntityLiving ent = (EntityLiving)mobs.get(i);
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

	public static void dropHead(EntityLiving e) {
		if (e == null)
			return;
		ItemStack is = null;
		if (e instanceof EntitySkeleton) {
			EntitySkeleton ek = (EntitySkeleton)e;
			if (ek.getSkeletonType() == 1) //Wither Skeleton
				is = new ItemStack(Item.skull.itemID, 1, 1);
			else
				is = new ItemStack(Item.skull.itemID, 1, 0);
		}
		if (e instanceof EntityZombie) {
			if (!((EntityZombie)e).isVillager())
				is = new ItemStack(Item.skull.itemID, 1, 2);
		}
		if (e instanceof EntityPlayer)
			is = new ItemStack(Item.skull.itemID, 1, 3);
		if (e instanceof EntityCreeper)
			is = new ItemStack(Item.skull.itemID, 1, 4);
		if (is == null)
			return;
		ReikaItemHelper.dropItem(e.worldObj, e.posX, e.posY, e.posZ, is);
	}

	public static void spawnParticlesAround(String part, World world, Entity e, int num) {
		for (int k = 0; k < num; k++)
			world.spawnParticle(part, e.posX-0.6+1.2*rand.nextDouble(), e.posY+e.height/2-0.6+1.2*rand.nextDouble(), e.posZ-0.6+1.2*rand.nextDouble(), -0.2+0.4*rand.nextDouble(), 0.4*rand.nextDouble(), -0.2+0.4*rand.nextDouble());
	}

	public static int getEntityIDByClass(Class cl) {
		String name = (String)EntityList.classToStringMapping.get(cl);
		return mobNameToID(name);
	}

}
