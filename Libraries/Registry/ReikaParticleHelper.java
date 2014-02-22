/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.Random;

import net.minecraft.world.World;

public enum ReikaParticleHelper {

	SMOKE("smoke"),
	CRITICAL("crit"),
	ENCHANTMENT("magicCrit"),
	FLAME("flame"),
	REDSTONE("reddust"),
	BONEMEAL("happyVillager"),
	BUBBLE("bubble"),
	VOID("townaura"),
	LARGESMOKE("largesmoke"),
	SNOWBALL("snowballpoof"),
	PORTAL("portal"), //and Ender
	RAIN("splash"),
	DRIPWATER("dripWater"),
	DRIPLAVA("dripLava"),
	EXPLODE("hugeexplosion"),
	HEART("heart"),
	CLOUD("cloud"),
	NOTE("note"),
	SGA("enchantmenttable"),
	LAVA("lava"),
	SPRINT("footstep"),
	SLIME("slime"),
	FIREWORK("fireworksSpark"),
	SUSPEND("suspended"),
	MOBSPELL("mobSpell"),
	AMBIENTMOBSPELL("mobSpellAmbient"),
	SPELL("spell"),
	INSTANTSPELL("instantSpell"),
	WITCH("witchMagic"),
	SNOWSHOVEL("snowshovel"),
	ANGRY("angryVillager");

	private String name;
	private static final Random rand = new Random();

	private ReikaParticleHelper(String sg) {
		name = sg;
	}

	public void spawnAt(World world, double x, double y, double z, double vx, double vy, double vz) {
		world.spawnParticle(name, x, y, z, vx, vy, vz);
	}

	public void spawnAt(World world, double x, double y, double z) {
		this.spawnAt(world, x, y, z, 0, 0, 0);
	}

	public void spawnAroundBlock(World world, int x, int y, int z, int number) {
		this.spawnAroundBlock(world, x, y, z, 0, 0, 0, number);
	}

	public void spawnAroundBlockWithOutset(World world, int x, int y, int z, int number, double outset) {
		this.spawnAroundBlockWithOutset(world, x, y, z, 0, 0, 0, number, outset);
	}

	public void spawnAroundBlock(World world, int x, int y, int z, double vx, double vy, double vz, int number) {
		for (int i = 0; i < number; i++) {
			world.spawnParticle(name, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), vx, vy, vz);
		}
	}

	public void spawnAroundBlockWithOutset(World world, int x, int y, int z, double vx, double vy, double vz, int number, double outset) {
		for (int i = 0; i < number; i++) {
			world.spawnParticle(name, x-outset+rand.nextDouble()*(1+outset), y-outset+rand.nextDouble()*(1+outset), z-outset+rand.nextDouble()*(1+outset), vx, vy, vz);
		}
	}

	public static void spawnColoredParticles(World world, int x, int y, int z, double r, double g, double b, int number) {
		REDSTONE.spawnAroundBlock(world, x, y, z, r, g, b, number);
	}

	public static void spawnColoredParticlesWithOutset(World world, int x, int y, int z, double r, double g, double b, int number, double outset) {
		REDSTONE.spawnAroundBlockWithOutset(world, x, y, z, r, g, b, number, outset);
	}

	public static void spawnColoredParticleAt(World world, double x, double y, double z, double r, double g, double b) {
		REDSTONE.spawnAt(world, x, y, z, r, g, b);
	}

}
