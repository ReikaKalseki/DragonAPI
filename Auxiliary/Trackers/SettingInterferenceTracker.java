package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Extras.EnvironmentPackager;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent.ProfileEventWatcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** This class is for registering handlers for when certain user-specified settings might cause functional problems ingame, but where the developer does not
 * wish to simply override the setting value (be that due to concerns over practical effects, an ideological stance, or otherwise).
 *
 * This will monitor the state of such settings and will notify the player appropriately, once with a message on login and with a small icon to both act as a
 * continuous reminder and a watermark of sorts (so that complaints resulting from said settings can be appropriately identified and treated accordingly).
 */
@SideOnly(Side.CLIENT)
public class SettingInterferenceTracker implements ProfileEventWatcher {

	public static final SettingInterferenceTracker instance = new SettingInterferenceTracker();

	public static final SettingInterference muteInterference = new SettingInterference() {

		private final EnumSet<SoundCategory> importantSounds = EnumSet.of(SoundCategory.MASTER, SoundCategory.AMBIENT, SoundCategory.BLOCKS, SoundCategory.PLAYERS);

		@Override
		public boolean isCurrentlyRelevant() {
			return true;
		}

		@Override
		public boolean isSetToInterfere() {
			GameSettings gs = Minecraft.getMinecraft().gameSettings;
			for (SoundCategory s : importantSounds) {
				if (gs.getSoundLevel(s) < 0.1) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void drawIcon(Tessellator v5, int x, int y, int size) {
			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "Resources/mutewarn.png");
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			v5.addVertexWithUV(x, y+size, 0, 0, 1);
			v5.addVertexWithUV(x+size, y+size, 0, 1, 1);
			v5.addVertexWithUV(x+size, y, 0, 1, 0);
			v5.addVertexWithUV(x, y, 0, 0, 0);
			v5.draw();
		}

		@Override
		public String getDescription() {
			return "Sounds are muted, despite being used as indicators or warnings in many situations.";
		}

	};

	private final ArrayList<SettingInterference> settings = new ArrayList();

	private SettingInterferenceTracker() {
		//ProfileEvent.registerHandler("gui", this);
	}

	public void registerSettingHandler(SettingInterference s) {
		if (!settings.contains(s))
			settings.add(s);
	}

	public void onLogin(EntityPlayer ep) {
		ArrayList<String> li = new ArrayList();
		for (SettingInterference si : settings) {
			if (si.isSetToInterfere()) {
				li.add(si.getDescription());
			}
		}
		if (!li.isEmpty()) {
			String s0 = "You have one or more game settings configured in a manner that is likely to cause gameplay problems in some situations; consider changing them, and please do not report any issues that would not have arisen without that setting. See the next messages for more details.";
			PopupWriter.instance.addMessage(s0);
			DragonAPICore.log(s0);
			for (String s : li) {
				PopupWriter.instance.addMessage(s);
				DragonAPICore.log(s);
			}
		}
	}

	public void onCall(String tag) {
		if (settings.isEmpty())
			return;
		switch(tag) {
			case "gui": {
				this.onRender();
				break;
			}
		}
	}

	public void onRender() {
		if (!settings.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
			ReikaRenderHelper.disableEntityLighting();
			ReikaRenderHelper.disableLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
			BlendMode.DEFAULT.apply();
			Tessellator v5 = Tessellator.instance;
			int x = 2;
			int y = 2;
			int size = 16;
			for (SettingInterference s : settings) {
				if (s.isSetToInterfere() && s.isCurrentlyRelevant()) {
					s.drawIcon(v5, x, y, size);
					x += size+4;
				}
			}
			GL11.glPopAttrib();
		}
	}

	public static interface SettingInterference {

		/** Whether the influence of the setting is relevant <i>at this exact second</i>. */
		public boolean isCurrentlyRelevant();

		/** Whether the setting is configured such that it might interfere under some circumstances, not necessarily at this instant. */
		public boolean isSetToInterfere();

		/** Draw the icon at the specified position and size. The tessellator is not running, in case you want to bind your own textures. */
		@SideOnly(Side.CLIENT)
		public void drawIcon(Tessellator v5, int x, int y, int size);

		public String getDescription();

	}

	public static enum WarningPersistence {
		EVERYLOAD(),
		SETTINGVALS(),
		VERSION(),
		ONCE();

		public boolean isActive() {
			switch(this) {
				case EVERYLOAD:
				default:
					return true;
				case VERSION:
					return VersionTransitionTracker.instance.haveModsUpdated();
				case SETTINGVALS:
					return EnvironmentPackager.instance.checkAndUpdateSettingsCache();
				case ONCE:
					return !EnvironmentPackager.instance.hasSettingsCache();
			}
		}
	}

}
