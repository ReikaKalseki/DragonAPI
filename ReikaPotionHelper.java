package Reika.DragonAPI;

import Reika.DragonAPI.*;

import net.minecraft.*;
import net.minecraft.src.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.client.model.*;
import net.minecraft.block.material.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.particle.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.*;
import net.minecraft.profiler.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.layer.*;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.storage.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.command.*;
import net.minecraft.crash.*;
import net.minecraft.creativetab.*;
import net.minecraft.dispenser.*;
import net.minecraft.item.crafting.*;
import net.minecraft.network.packet.*;
import net.minecraft.server.gui.*;
import net.minecraft.village.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.*;

import cpw.mods.*;
import cpw.mods.fml.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.client.modloader.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.asm.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.functions.*;
import cpw.mods.fml.server.*;

import java.util.*;
import java.lang.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ReikaPotionHelper {
	
	
	/** Returns a potion ID from the damage value. Returns -1 if invalid damage value. */
	public static int getPotionID(int dmg) {
        List effects = Item.potion.getEffects(dmg);
        if (effects != null && !effects.isEmpty()) {
        	Iterator potioneffects = effects.iterator();
            while (potioneffects.hasNext()) {
            	PotionEffect effect = (PotionEffect)potioneffects.next();
            	return effect.getPotionID();
            }
		}
        return -1;
	}

}
