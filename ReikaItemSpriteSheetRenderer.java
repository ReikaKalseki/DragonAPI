package Reika.DragonAPI;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.IItemRenderer;
import Reika.DragonAPI.ReikaSpriteSheets;

public class ReikaItemSpriteSheetRenderer implements IItemRenderer {	
	
	protected int spritesheet;
	protected int index;
	
	public ReikaItemSpriteSheetRenderer(Class root, String file, String backup, int ind) {
		//this.spritesheet = ReikaSpriteSheets.setupTextures(root, file);
		String filename;
		if (backup == null)
			backup = "";
		if (file == null || root == null)
			return;
		//if (root.getResource(file) == null && root.getResource(backup) == null)
			//return;
		if (root.getResource(file) == null)
			filename = backup;
		else
			filename = root.getResource(file).getPath();
		this.spritesheet = ModLoader.getMinecraftInstance().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(filename, backup));
		this.index = ind;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (item == null)
			return;
		int textureid = spritesheet;
		ReikaSpriteSheets.renderItem(textureid, index, type, item, data);
	}
}
