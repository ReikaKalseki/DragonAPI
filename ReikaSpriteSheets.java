package Reika.DragonAPI;

import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.*;

public final class ReikaSpriteSheets {
	
	private ReikaSpriteSheets() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	/** Call this from a registered ItemRenderer class that implements IItemRenderer to actually render the item.
	 * It will automatically compensate for being used for inventory/entity/held items.
	 * Args: Texture Int (as given by setupTextures), Sprite Index, ItemRenderType, ItemStack, Data */
	public static void renderItem(int tex, int index, ItemRenderType type, ItemStack item, Object... data) {
		if (item == null)
			return;
		int row = index/16;
		int col = index-row*16;		
		//ModLoader.getMinecraftInstance().entityRenderer.disableLightmap(1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		if (type == type.INVENTORY)
			GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GL11.glTranslatef((float)1, (float)1 + 2.0F, (float)1 + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glPopMatrix();
		Tessellator v5 = new Tessellator();
		if (type == type.INVENTORY) {
			if (v5.isDrawing)
				v5.draw();
			v5.startDrawingQuads();
			v5.setTranslation(-1.125F, -1.375F, 0);
			v5.addVertexWithUV(0, 0, 0, 0.0625F*col, 0.0625F+0.0625F*row);
			v5.setTranslation(0.125F, -0.46875F, 0);
			v5.addVertexWithUV(1, 0, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
			v5.setTranslation(0.125F, 0.375F, 0);
			v5.addVertexWithUV(1, 1, 0, 0.0625F+0.0625F*col, 0.0625F*row);
			v5.setTranslation(-1.125F, -0.53125F, 0);
			v5.addVertexWithUV(0, 1, 0, 0.0625F*col, 0.0625F*row);
			v5.draw();
		}
		if (type == type.EQUIPPED || type == type.ENTITY) {
			//Args: x-hi, z-lo, x-lo, z-hi, sheet width, sheet height, thickness
			if (type == type.EQUIPPED) {
				GL11.glTranslatef(0, 1.25F, 0.3125F);
				GL11.glRotatef(60, 0, 1, 0);
				GL11.glRotatef(65, 0, 0, 1);
				GL11.glTranslatef(-0.625F, 0F, 0);
				GL11.glScalef(1.5F, 1.5F, 1.5F);
				GL11.glRotatef(-90, 0, 0, 1);
				GL11.glTranslatef(-1, 0, 0);
				GL11.glTranslatef(0.5F, 0, 0.25F);
				GL11.glRotatef(-10, 0, 1, 0);
				GL11.glTranslatef(-0.125F, -0.125F, 0F);
			}
			else {
				GL11.glTranslatef(-0.5F, 0, 0);
			}
			float thick = 0.0625F;
			ItemRenderer.renderItemIn2D(v5, 0.0625F+0.0625F*col, 0.0625F*row, 0.0625F*col, 0.0625F+0.0625F*row, 256, 256, thick);
		}
        GL11.glEnable(GL11.GL_LIGHTING);
		//ModLoader.getMinecraftInstance().entityRenderer.enableLightmap(1);
	}
	
}
