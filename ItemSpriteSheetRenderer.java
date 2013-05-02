package Reika.DragonAPI;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.IItemRenderer;

public final class ItemSpriteSheetRenderer implements IItemRenderer {	
	
	protected int spritesheet;
	protected int index;
	
	public ItemSpriteSheetRenderer(Class root, String file, String backup) {
		//this.spritesheet = ReikaSpriteSheets.setupTextures(root, file);
		String filename;/*
		if (backup == null)
			backup = "";
		if (file == null || root == null)
			return;
		//if (root.getResource(file) == null && root.getResource(backup) == null)
			//return;
		if (root.getResource(file) == null)
			filename = backup;
		else
			filename = root.getResource(file).getPath();*/
		
        if (root == null)
        	return;
        if (root.getResource(".") == null)
        	filename = "";
        else {
	        String base = root.getResource(".").getPath();
	        String path = base.substring(1, base.length()-1);
	        filename = path+file;
        }
        //ReikaJavaLibrary.pConsole("ITEM @ "+filename+" from "+file+" Exists: ");
		this.spritesheet = ModLoader.getMinecraftInstance().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(root, file, backup));
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
		Item cls = item.getItem();
		if (cls instanceof IndexedItemSprites) {
			IndexedItemSprites iis = (IndexedItemSprites)cls;
			index = iis.getItemSpriteIndex(item);
			ReikaSpriteSheets.renderItem(spritesheet, index, type, item, data);
		}
	}
}
