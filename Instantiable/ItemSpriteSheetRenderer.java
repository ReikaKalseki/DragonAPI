/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import Reika.DragonAPI.Auxiliary.ReikaSpriteSheets;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaPNGLoader;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemSpriteSheetRenderer implements IItemRenderer {

	protected final int spritesheet;

	public ItemSpriteSheetRenderer(DragonAPIMod mod, Class root, String file, String backup) {
		//this.spritesheet = ReikaSpriteSheets.setupTextures(root, file);
		if (ReikaTextureHelper.isUsingDefaultTexturePack()) {
			String filename;
			if (root == null) {
				throw new MisuseException("You cannot fetch a render texture with reference to a null class!");
			}
			if (root.getResource(".") == null)
				filename = "";
			else {
				String base = root.getResource(".").getPath();
				String path = base.substring(1, base.length()-1);
				filename = path+file;
			}
			spritesheet = Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(root, file, backup));
		}
		else {
			String filename = "/"+mod.getDisplayName().toLowerCase()+"/"+file;
			int sprite = 0;
			try {
				sprite = Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTexturePackImage(ReikaTextureHelper.getCurrentTexturePack(), filename));
				ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": Found alternate texture pack image in texturepack "+ReikaTextureHelper.getCurrentTexturePack().getTexturePackFileName()+".");
			}
			catch (IOException e) {
				ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": IOException ("+e.getClass().getSimpleName()+") on loading texture pack image variant "+filename+" for texturepack "+ReikaTextureHelper.getCurrentTexturePack().getTexturePackFileName()+". Loading default textures.");
				sprite = Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(root, file, backup));
			}
			finally {
				spritesheet = sprite;
			}
		}
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
			int index = iis.getItemSpriteIndex(item);
			ReikaSpriteSheets.renderItem(spritesheet, index, type, item, data);
		}
	}

	public int getSpritesheet() {
		return spritesheet;
	}
}
