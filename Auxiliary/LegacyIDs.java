/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class LegacyIDs {

	private static final BiMap<Integer, String> blockIDs = HashBiMap.create();
	private static final BiMap<Integer, String> itemIDs = HashBiMap.create();

	public static String getBlockNameFromInt(int id) {
		return blockIDs.get(id);
	}

	public static int getBlockIntFromName(String name) {
		return blockIDs.inverse().get(name);
	}

	public static String getItemNameFromInt(int id) {
		return itemIDs.get(id);
	}

	public static int getItemIntFromName(String name) {
		return itemIDs.inverse().get(name);
	}

	public static boolean matchBlocks(World world, int x, int y, int z, Block b) {
		int id = world.getBlockId(x, y, z);
		return id == b.blockID;
		/* for 1.7

		 String sg = world.getBlockId(x, y, z);
		 return b.blockID.equals(sg);

		 */
	}

	public static int getBlockId(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return id;
		/* for 1.7

		String name = world.getBlockId(x, y, z);
		return getBlockIntFromName(name);

		 */
	}

	public static String getBlockID(Block b) {
		int id = IDMap.getIntegerForBlock(b);
		return getBlockNameFromInt(id);
	}

	public static Block getBlockFromName(String name) {
		int id = getBlockIntFromName(name);
		return IDMap.getBlockFromInt(id);
	}

	static {
		blockIDs.put(0, "minecraft:air");
		blockIDs.put(1, "minecraft:stone");
		blockIDs.put(2, "minecraft:grass");
		blockIDs.put(3, "minecraft:dirt");
		blockIDs.put(4, "minecraft:cobblestone");
		blockIDs.put(5, "minecraft:planks");
		blockIDs.put(6, "minecraft:sapling");
		blockIDs.put(7, "minecraft:bedrock");
		blockIDs.put(8, "minecraft:flowing_water");
		blockIDs.put(9, "minecraft:water");
		blockIDs.put(10, "minecraft:flowing_lava");
		blockIDs.put(11, "minecraft:lava");
		blockIDs.put(12, "minecraft:sand");
		blockIDs.put(13, "minecraft:gravel");
		blockIDs.put(14, "minecraft:gold_ore");
		blockIDs.put(15, "minecraft:iron_ore");
		blockIDs.put(16, "minecraft:coal_ore");
		blockIDs.put(17, "minecraft:log");
		blockIDs.put(18, "minecraft:leaves");
		blockIDs.put(19, "minecraft:sponge");
		blockIDs.put(20, "minecraft:glass");
		blockIDs.put(21, "minecraft:lapis_ore");
		blockIDs.put(22, "minecraft:lapis_block");
		blockIDs.put(23, "minecraft:dispenser");
		blockIDs.put(24, "minecraft:sandstone");
		blockIDs.put(25, "minecraft:noteblock");
		blockIDs.put(26, "minecraft:bed");
		blockIDs.put(27, "minecraft:golden_rail");
		blockIDs.put(28, "minecraft:detector_rail");
		blockIDs.put(29, "minecraft:sticky_piston");
		blockIDs.put(30, "minecraft:web");
		blockIDs.put(31, "minecraft:tallgrass");
		blockIDs.put(32, "minecraft:deadbush");
		blockIDs.put(33, "minecraft:piston");
		blockIDs.put(34, "minecraft:piston_head");
		blockIDs.put(35, "minecraft:wool");
		blockIDs.put(36, "minecraft:piston_extension");
		blockIDs.put(37, "minecraft:yellow_flower");
		blockIDs.put(38, "minecraft:red_flower");
		blockIDs.put(39, "minecraft:brown_mushroom");
		blockIDs.put(40, "minecraft:red_mushroom");
		blockIDs.put(41, "minecraft:gold_block");
		blockIDs.put(42, "minecraft:iron_block");
		blockIDs.put(43, "minecraft:double_stone_slab");
		blockIDs.put(44, "minecraft:stone_slab");
		blockIDs.put(45, "minecraft:brick_block");
		blockIDs.put(46, "minecraft:tnt");
		blockIDs.put(47, "minecraft:bookshelf");
		blockIDs.put(48, "minecraft:mossy_cobblestone");
		blockIDs.put(49, "minecraft:obsidian");
		blockIDs.put(50, "minecraft:torch");
		blockIDs.put(51, "minecraft:fire");
		blockIDs.put(52, "minecraft:mob_spawner");
		blockIDs.put(53, "minecraft:oak_stairs");
		blockIDs.put(54, "minecraft:chest");
		blockIDs.put(55, "minecraft:redstone_wire");
		blockIDs.put(56, "minecraft:diamond_ore");
		blockIDs.put(57, "minecraft:diamond_block");
		blockIDs.put(58, "minecraft:crafting_table");
		blockIDs.put(59, "minecraft:wheat");
		blockIDs.put(60, "minecraft:farmland");
		blockIDs.put(61, "minecraft:furnace");
		blockIDs.put(62, "minecraft:lit_furnace");
		blockIDs.put(63, "minecraft:standing_sign");
		blockIDs.put(64, "minecraft:wooden_door");
		blockIDs.put(65, "minecraft:ladder");
		blockIDs.put(66, "minecraft:rail");
		blockIDs.put(67, "minecraft:stone_stairs");
		blockIDs.put(68, "minecraft:wall_sign");
		blockIDs.put(69, "minecraft:lever");
		blockIDs.put(70, "minecraft:stone_pressure_plate");
		blockIDs.put(71, "minecraft:iron_door");
		blockIDs.put(72, "minecraft:wooden_pressure_plate");
		blockIDs.put(73, "minecraft:redstone_ore");
		blockIDs.put(74, "minecraft:lit_redstone_ore");
		blockIDs.put(75, "minecraft:unlit_redstone_torch");
		blockIDs.put(76, "minecraft:redstone_torch");
		blockIDs.put(77, "minecraft:stone_button");
		blockIDs.put(78, "minecraft:snow_layer");
		blockIDs.put(79, "minecraft:ice");
		blockIDs.put(80, "minecraft:snow");
		blockIDs.put(81, "minecraft:cactus");
		blockIDs.put(82, "minecraft:clay");
		blockIDs.put(83, "minecraft:reeds");
		blockIDs.put(84, "minecraft:jukebox");
		blockIDs.put(85, "minecraft:fence");
		blockIDs.put(86, "minecraft:pumpkin");
		blockIDs.put(87, "minecraft:netherrack");
		blockIDs.put(88, "minecraft:soul_sand");
		blockIDs.put(89, "minecraft:glowstone");
		blockIDs.put(90, "minecraft:portal");
		blockIDs.put(91, "minecraft:lit_pumpkin");
		blockIDs.put(92, "minecraft:cake");
		blockIDs.put(93, "minecraft:unpowered_repeater");
		blockIDs.put(94, "minecraft:powered_repeater");
		blockIDs.put(95, "minecraft:chest_locked_aprilfools_super_old_legacy_we_should_not_even_have_this");
		blockIDs.put(96, "minecraft:trapdoor");
		blockIDs.put(97, "minecraft:monster_egg");
		blockIDs.put(98, "minecraft:stonebrick");
		blockIDs.put(99, "minecraft:brown_mushroom_block");
		blockIDs.put(100, "minecraft:red_mushroom_block");
		blockIDs.put(101, "minecraft:iron_bars");
		blockIDs.put(102, "minecraft:glass_pane");
		blockIDs.put(103, "minecraft:melon_block");
		blockIDs.put(104, "minecraft:pumpkin_stem");
		blockIDs.put(105, "minecraft:melon_stem");
		blockIDs.put(106, "minecraft:vine");
		blockIDs.put(107, "minecraft:fence_gate");
		blockIDs.put(108, "minecraft:brick_stairs");
		blockIDs.put(109, "minecraft:stone_brick_stairs");
		blockIDs.put(110, "minecraft:mycelium");
		blockIDs.put(111, "minecraft:waterlily");
		blockIDs.put(112, "minecraft:nether_brick");
		blockIDs.put(113, "minecraft:nether_brick_fence");
		blockIDs.put(114, "minecraft:nether_brick_stairs");
		blockIDs.put(115, "minecraft:nether_wart");
		blockIDs.put(116, "minecraft:enchanting_table");
		blockIDs.put(117, "minecraft:brewing_stand");
		blockIDs.put(118, "minecraft:cauldron");
		blockIDs.put(119, "minecraft:end_portal");
		blockIDs.put(120, "minecraft:end_portal_frame");
		blockIDs.put(121, "minecraft:end_stone");
		blockIDs.put(122, "minecraft:dragon_egg");
		blockIDs.put(123, "minecraft:redstone_lamp");
		blockIDs.put(124, "minecraft:lit_redstone_lamp");
		blockIDs.put(125, "minecraft:double_wooden_slab");
		blockIDs.put(126, "minecraft:wooden_slab");
		blockIDs.put(127, "minecraft:cocoa");
		blockIDs.put(128, "minecraft:sandstone_stairs");
		blockIDs.put(129, "minecraft:emerald_ore");
		blockIDs.put(130, "minecraft:ender_chest");
		blockIDs.put(131, "minecraft:tripwire_hook");
		blockIDs.put(132, "minecraft:tripwire");
		blockIDs.put(133, "minecraft:emerald_block");
		blockIDs.put(134, "minecraft:spruce_stairs");
		blockIDs.put(135, "minecraft:birch_stairs");
		blockIDs.put(136, "minecraft:jungle_stairs");
		blockIDs.put(137, "minecraft:command_block");
		blockIDs.put(138, "minecraft:beacon");
		blockIDs.put(139, "minecraft:cobblestone_wall");
		blockIDs.put(140, "minecraft:flower_pot");
		blockIDs.put(141, "minecraft:carrots");
		blockIDs.put(142, "minecraft:potatoes");
		blockIDs.put(143, "minecraft:wooden_button");
		blockIDs.put(144, "minecraft:skull");
		blockIDs.put(145, "minecraft:anvil");
		blockIDs.put(146, "minecraft:trapped_chest");
		blockIDs.put(147, "minecraft:light_weighted_pressure_plate");
		blockIDs.put(148, "minecraft:heavy_weighted_pressure_plate");
		blockIDs.put(149, "minecraft:unpowered_comparator");
		blockIDs.put(150, "minecraft:powered_comparator");
		blockIDs.put(151, "minecraft:daylight_detector");
		blockIDs.put(152, "minecraft:redstone_block");
		blockIDs.put(153, "minecraft:quartz_ore");
		blockIDs.put(154, "minecraft:hopper");
		blockIDs.put(155, "minecraft:quartz_block");
		blockIDs.put(156, "minecraft:quartz_stairs");
		blockIDs.put(157, "minecraft:activator_rail");
		blockIDs.put(158, "minecraft:dropper");
		blockIDs.put(159, "minecraft:stained_hardened_clay");
		blockIDs.put(170, "minecraft:hay_block");
		blockIDs.put(171, "minecraft:carpet");
		blockIDs.put(172, "minecraft:hardened_clay");
		blockIDs.put(173, "minecraft:coal_block");
		blockIDs.put(174, "minecraft:packed_ice");
		blockIDs.put(175, "minecraft:double_plant");

		itemIDs.put(256, "minecraft:iron_shovel");
		itemIDs.put(257, "minecraft:iron_pickaxe");
		itemIDs.put(258, "minecraft:iron_axe");
		itemIDs.put(259, "minecraft:flint_and_steel");
		itemIDs.put(260, "minecraft:apple");
		itemIDs.put(261, "minecraft:bow");
		itemIDs.put(262, "minecraft:arrow");
		itemIDs.put(263, "minecraft:coal");
		itemIDs.put(264, "minecraft:diamond");
		itemIDs.put(265, "minecraft:iron_ingot");
		itemIDs.put(266, "minecraft:gold_ingot");
		itemIDs.put(267, "minecraft:iron_sword");
		itemIDs.put(268, "minecraft:wooden_sword");
		itemIDs.put(269, "minecraft:wooden_shovel");
		itemIDs.put(270, "minecraft:wooden_pickaxe");
		itemIDs.put(271, "minecraft:wooden_axe");
		itemIDs.put(272, "minecraft:stone_sword");
		itemIDs.put(273, "minecraft:stone_shovel");
		itemIDs.put(274, "minecraft:stone_pickaxe");
		itemIDs.put(275, "minecraft:stone_axe");
		itemIDs.put(276, "minecraft:diamond_sword");
		itemIDs.put(277, "minecraft:diamond_shovel");
		itemIDs.put(278, "minecraft:diamond_pickaxe");
		itemIDs.put(279, "minecraft:diamond_axe");
		itemIDs.put(280, "minecraft:stick");
		itemIDs.put(281, "minecraft:bowl");
		itemIDs.put(282, "minecraft:mushroom_stew");
		itemIDs.put(283, "minecraft:golden_sword");
		itemIDs.put(284, "minecraft:golden_shovel");
		itemIDs.put(285, "minecraft:golden_pickaxe");
		itemIDs.put(286, "minecraft:golden_axe");
		itemIDs.put(287, "minecraft:string");
		itemIDs.put(288, "minecraft:feather");
		itemIDs.put(289, "minecraft:gunpowder");
		itemIDs.put(290, "minecraft:wooden_hoe");
		itemIDs.put(291, "minecraft:stone_hoe");
		itemIDs.put(292, "minecraft:iron_hoe");
		itemIDs.put(293, "minecraft:diamond_hoe");
		itemIDs.put(294, "minecraft:golden_hoe");
		itemIDs.put(295, "minecraft:wheat_seeds");
		itemIDs.put(296, "minecraft:wheat");
		itemIDs.put(297, "minecraft:bread");
		itemIDs.put(298, "minecraft:leather_helmet");
		itemIDs.put(299, "minecraft:leather_chestplate");
		itemIDs.put(300, "minecraft:leather_leggings");
		itemIDs.put(301, "minecraft:leather_boots");
		itemIDs.put(302, "minecraft:chainmail_helmet");
		itemIDs.put(303, "minecraft:chainmail_chestplate");
		itemIDs.put(304, "minecraft:chainmail_leggings");
		itemIDs.put(305, "minecraft:chainmail_boots");
		itemIDs.put(306, "minecraft:iron_helmet");
		itemIDs.put(307, "minecraft:iron_chestplate");
		itemIDs.put(308, "minecraft:iron_leggings");
		itemIDs.put(309, "minecraft:iron_boots");
		itemIDs.put(310, "minecraft:diamond_helmet");
		itemIDs.put(311, "minecraft:diamond_chestplate");
		itemIDs.put(312, "minecraft:diamond_leggings");
		itemIDs.put(313, "minecraft:diamond_boots");
		itemIDs.put(314, "minecraft:golden_helmet");
		itemIDs.put(315, "minecraft:golden_chestplate");
		itemIDs.put(316, "minecraft:golden_leggings");
		itemIDs.put(317, "minecraft:golden_boots");
		itemIDs.put(318, "minecraft:flint");
		itemIDs.put(319, "minecraft:porkchop");
		itemIDs.put(320, "minecraft:cooked_porkchop");
		itemIDs.put(321, "minecraft:painting");
		itemIDs.put(322, "minecraft:golden_apple");
		itemIDs.put(323, "minecraft:sign");
		itemIDs.put(324, "minecraft:wooden_door");
		itemIDs.put(325, "minecraft:bucket");
		itemIDs.put(326, "minecraft:water_bucket");
		itemIDs.put(327, "minecraft:lava_bucket");
		itemIDs.put(328, "minecraft:minecart");
		itemIDs.put(329, "minecraft:saddle");
		itemIDs.put(330, "minecraft:iron_door");
		itemIDs.put(331, "minecraft:redstone");
		itemIDs.put(332, "minecraft:snowball");
		itemIDs.put(333, "minecraft:boat");
		itemIDs.put(334, "minecraft:leather");
		itemIDs.put(335, "minecraft:milk_bucket");
		itemIDs.put(336, "minecraft:brick");
		itemIDs.put(337, "minecraft:clay_ball");
		itemIDs.put(338, "minecraft:reeds");
		itemIDs.put(339, "minecraft:paper");
		itemIDs.put(340, "minecraft:book");
		itemIDs.put(341, "minecraft:slime_ball");
		itemIDs.put(342, "minecraft:chest_minecart");
		itemIDs.put(343, "minecraft:furnace_minecart");
		itemIDs.put(344, "minecraft:egg");
		itemIDs.put(345, "minecraft:compass");
		itemIDs.put(346, "minecraft:fishing_rod");
		itemIDs.put(347, "minecraft:clock");
		itemIDs.put(348, "minecraft:glowstone_dust");
		itemIDs.put(349, "minecraft:fish");
		itemIDs.put(350, "minecraft:cooked_fished");
		itemIDs.put(351, "minecraft:dye");
		itemIDs.put(352, "minecraft:bone");
		itemIDs.put(353, "minecraft:sugar");
		itemIDs.put(354, "minecraft:cake");
		itemIDs.put(355, "minecraft:bed");
		itemIDs.put(356, "minecraft:repeater");
		itemIDs.put(357, "minecraft:cookie");
		itemIDs.put(358, "minecraft:filled_map");
		itemIDs.put(359, "minecraft:shears");
		itemIDs.put(360, "minecraft:melon");
		itemIDs.put(361, "minecraft:pumpkin_seeds");
		itemIDs.put(362, "minecraft:melon_seeds");
		itemIDs.put(363, "minecraft:beef");
		itemIDs.put(364, "minecraft:cooked_beef");
		itemIDs.put(365, "minecraft:chicken");
		itemIDs.put(366, "minecraft:cooked_chicken");
		itemIDs.put(367, "minecraft:rotten_flesh");
		itemIDs.put(368, "minecraft:ender_pearl");
		itemIDs.put(369, "minecraft:blaze_rod");
		itemIDs.put(370, "minecraft:ghast_tear");
		itemIDs.put(371, "minecraft:gold_nugget");
		itemIDs.put(372, "minecraft:nether_wart");
		itemIDs.put(373, "minecraft:potion");
		itemIDs.put(374, "minecraft:glass_bottle");
		itemIDs.put(375, "minecraft:spider_eye");
		itemIDs.put(376, "minecraft:fermented_spider_eye");
		itemIDs.put(377, "minecraft:blaze_powder");
		itemIDs.put(378, "minecraft:magma_cream");
		itemIDs.put(379, "minecraft:brewing_stand");
		itemIDs.put(380, "minecraft:cauldron");
		itemIDs.put(381, "minecraft:ender_eye");
		itemIDs.put(382, "minecraft:speckled_melon");
		itemIDs.put(383, "minecraft:spawn_egg");
		itemIDs.put(384, "minecraft:experience_bottle");
		itemIDs.put(385, "minecraft:fire_charge");
		itemIDs.put(386, "minecraft:writable_book");
		itemIDs.put(387, "minecraft:written_book");
		itemIDs.put(388, "minecraft:emerald");
		itemIDs.put(389, "minecraft:item_frame");
		itemIDs.put(390, "minecraft:flower_pot");
		itemIDs.put(391, "minecraft:carrot");
		itemIDs.put(392, "minecraft:potato");
		itemIDs.put(393, "minecraft:baked_potato");
		itemIDs.put(394, "minecraft:poisonous_potato");
		itemIDs.put(395, "minecraft:map");
		itemIDs.put(396, "minecraft:golden_carrot");
		itemIDs.put(397, "minecraft:skull");
		itemIDs.put(398, "minecraft:carrot_on_a_stick");
		itemIDs.put(399, "minecraft:nether_star");
		itemIDs.put(400, "minecraft:pumpkin_pie");
		itemIDs.put(401, "minecraft:fireworks");
		itemIDs.put(402, "minecraft:firework_charge");
		itemIDs.put(403, "minecraft:enchanted_book");
		itemIDs.put(404, "minecraft:comparator");
		itemIDs.put(405, "minecraft:netherbrick");
		itemIDs.put(406, "minecraft:quartz");
		itemIDs.put(407, "minecraft:tnt_minecart");
		itemIDs.put(408, "minecraft:hopper_minecart");
		itemIDs.put(417, "minecraft:iron_horse_armor");
		itemIDs.put(418, "minecraft:golden_horse_armor");
		itemIDs.put(419, "minecraft:diamond_horse_armor");
		itemIDs.put(420, "minecraft:lead");
		itemIDs.put(421, "minecraft:name_tag");
		itemIDs.put(422, "minecraft:command_block_minecart");
		itemIDs.put(2256, "minecraft:record_13");
		itemIDs.put(2257, "minecraft:record_cat");
		itemIDs.put(2258, "minecraft:record_blocks");
		itemIDs.put(2259, "minecraft:record_chirp");
		itemIDs.put(2260, "minecraft:record_far");
		itemIDs.put(2261, "minecraft:record_mall");
		itemIDs.put(2262, "minecraft:record_mellohi");
		itemIDs.put(2263, "minecraft:record_stal");
		itemIDs.put(2264, "minecraft:record_strad");
		itemIDs.put(2265, "minecraft:record_ward");
		itemIDs.put(2266, "minecraft:record_11");
		itemIDs.put(2267, "minecraft:record_wait");
	}
}
