package Reika.DragonAPI.ASM;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.SpecialDayTracker;
import Reika.DragonAPI.Extras.ReikaShader;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.Instantiable.Event.Client.RenderCursorStackEvent;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ASMCallsClient {

	public static void renderCursorStack(GuiContainer gui, ItemStack is, int x, int y, String subtext, RenderItem ir, FontRenderer font, ItemStack drag) {
		//gui.drawItemStack(is, x, y, subtext);
		RenderCursorStackEvent evt = new RenderCursorStackEvent(gui, is, x, y);
		MinecraftForge.EVENT_BUS.post(evt);
		is = evt.itemToRender;

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		ir.zLevel = 200.0F;
		if (is != null) {
			FontRenderer f = is.getItem().getFontRenderer(is);
			if (f != null)
				font = f;
		}
		ir.renderItemAndEffectIntoGUI(font, gui.mc.getTextureManager(), is, x, y);
		ir.renderItemOverlayIntoGUI(font, gui.mc.getTextureManager(), is, x, y - (drag == null ? 0 : 8), subtext);
		ir.zLevel = 0.0F;
	}

	public static IIcon getLiquidIconForRenderBlocks(RenderBlocks rb, Block b, int s, int meta, int x, int y, int z) {
		return rb.getBlockIcon(b, rb.blockAccess, x, y, z, s);
	}

	public static void addRainParticlesAndSound(EntityRenderer er) {
		if (SpecialDayTracker.instance.loadXmasTextures())
			return;
		if (SpecialDayTracker.instance.getXmasWeatherStrength(Minecraft.getMinecraft().theWorld) >= 0.5)
			return;
		if (DragonOptions.NORAINFX.getState())
			return;
		er.addRainParticles();
	}

	public static boolean shouldRenderRainInsteadOfSnow(WorldClient world, BiomeGenBase biome, int x, int y, int z, float biomeTemp, int precipHeight) {
		if (SpecialDayTracker.instance.loadXmasTextures())
			return false;
		if (SpecialDayTracker.instance.getXmasWeatherStrength(world) >= 0.5)
			return false;
		return world.getWorldChunkManager().getTemperatureAtHeight(biomeTemp, precipHeight) >= 0.15F;
	}

	public static void onCallChunkRenderLists(RenderGlobal rg, int pass, double ptick) {
		rg.renderAllRenderLists(pass, ptick);
	}

	public static void onRenderWorld(EntityRenderer er, float ptick, long systime) {
		er.renderWorld(ptick, systime);
		Minecraft mc = Minecraft.getMinecraft();
		ReikaShader.instance.render(mc);
		ShaderRegistry.runShaderDomain(mc.getFramebuffer(), mc.displayWidth, mc.displayHeight, ShaderDomain.GLOBALNOGUI);
	}

	public static void onRenderFrameBuffer(Framebuffer fb, int w, int h) {
		ShaderRegistry.runShaderDomain(fb, w, h, ShaderDomain.GLOBAL);
		ReikaRenderHelper.setRenderTarget(null);
		fb.framebufferRender(w, h);
	}

	public static void preTessellatorStart() {
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 2/255F);
	}
}
