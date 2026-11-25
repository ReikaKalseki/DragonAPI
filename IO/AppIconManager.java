package Reika.DragonAPI.IO;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Objects;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;

import com.google.common.base.Strings;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;

import scala.actors.threadpool.Arrays;

public final class AppIconManager {

	private static ByteBuffer[] originalIcons;

	private static AppIcon defaultSmallIcon;
	private static AppIcon defaultLargeIcon;
	private static final HashMap<String, AppIcon> smallIcons = new HashMap();
	private static final HashMap<String, AppIcon> largeIcons = new HashMap();

	private static final boolean IS_WINDOWS = LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_WINDOWS;
	private static final int WM_SETICON = 0x0080;
	private static final long ICON_SMALL = 0L;
	private static final long ICON_BIG = 1L;

	private static Object windowsDisplay;
	private static long hwnd;
	private static Method createIconMethod;
	private static Method sendMessageMethod;
	private static Field smallIconField;
	private static Field largeIconField;

	private static AppIcon currentLargeIcon;
	private static AppIcon currentSmallIcon;
	private static int currentSmallIconIndex;
	private static int currentLargeIconIndex;

	private static String defaultTitle = "Minecraft 1.7.10";
	private static String currentTitle;

	private static final Thread lwjglThread = Thread.currentThread();

	static {
		if (IS_WINDOWS) {
			try {
				Field implField = Display.class.getDeclaredField("display_impl");
				implField.setAccessible(true);
				windowsDisplay = implField.get(null);

				Class<?> winCls = Class.forName("org.lwjgl.opengl.WindowsDisplay");

				Method getHwnd = winCls.getDeclaredMethod("getHwnd");
				getHwnd.setAccessible(true);
				hwnd = (Long)getHwnd.invoke(windowsDisplay);

				createIconMethod = winCls.getDeclaredMethod("createIcon", int.class, int.class, IntBuffer.class);
				createIconMethod.setAccessible(true);

				sendMessageMethod = winCls.getDeclaredMethod("sendMessage", long.class, long.class, long.class, long.class);
				sendMessageMethod.setAccessible(true);

				smallIconField = winCls.getDeclaredField("small_icon");
				smallIconField.setAccessible(true);

				largeIconField = winCls.getDeclaredField("large_icon");
				largeIconField.setAccessible(true);
			}
			catch (Exception e) {
				throw new RegistrationException(DragonAPIInit.instance, "Failed to initialize AppIcon hooks", e);
			}
		}
	}

	public static void registerIcon(DragonAPIMod mod, AppIcon ico) {
		if (originalIcons == null) {
			try {
				Field f = Display.class.getDeclaredField("cached_icons");
				f.setAccessible(true);
				setDefaultIcons(mod, (ByteBuffer[]) f.get(null));
			}
			catch (Exception e) {
				throw new RegistrationException(DragonAPIInit.instance, "Failed to fetch default game icons", e);
			}
		}

		try {
			HashMap<String, AppIcon> map = ico.size == 32 ? largeIcons : smallIcons;
			if (map.containsKey(ico.iconID))
				throw new RegistrationException(mod, "Could not register custom app icon "+ico+": ID already taken");
			map.put(ico.iconID, ico.bake());
		}
		catch (Exception e) {
			throw new RegistrationException(mod, "Failed to register custom app icon "+ico, e);
		}
	}

	public static void setDefaultIcons(DragonAPIMod mod, ByteBuffer[] buffers) {
		originalIcons = buffers;
		try {
			defaultSmallIcon = new AppIcon(null, 16, originalIcons[0]).bake();
			defaultLargeIcon = new AppIcon(null, 32, originalIcons[1]).bake();
		}
		catch (Exception e) {
			throw new RegistrationException(mod, "Failed to override default app icons", e);
		}
		setCurrentIcon(null, null);
	}

	public static void setCurrentIcon(String smallID, String largeID) {
		setCurrentIcon(smallID, largeID, 0, 0);
	}

	public static void setCurrentIcon(String smallID, String largeID, int smallIndex, int largeIndex) {
		if (Thread.currentThread() != lwjglThread) {
			return;
		}
		try {
			AppIcon hLarge = largeID == null ? defaultLargeIcon : largeIcons.get(largeID);
			AppIcon hSmall = smallID == null ? defaultSmallIcon : smallIcons.get(smallID);
			if (IS_WINDOWS) {
				if (hLarge != null) {
					if (!isSame(currentLargeIcon, currentLargeIconIndex, hLarge, largeIndex)) {
						largeIconField.setLong(windowsDisplay, hLarge.getHandle(largeIndex));
						sendMessageMethod.invoke(null, hwnd, WM_SETICON, ICON_BIG, hLarge.getHandle(largeIndex));
						currentLargeIcon = hLarge;
						currentLargeIconIndex = largeIndex;
					}
				}
				else {
					DragonAPICore.logError("Failed to set app large icon '"+largeID+"'; no such icon");
				}
				if (hSmall != null) {
					if (!isSame(currentSmallIcon, currentSmallIconIndex, hSmall, smallIndex)) {
						smallIconField.setLong(windowsDisplay, hSmall.getHandle(smallIndex));
						sendMessageMethod.invoke(null, hwnd, WM_SETICON, ICON_SMALL, hSmall.getHandle(smallIndex));
						currentSmallIcon = hSmall;
						currentSmallIconIndex = smallIndex;
					}
				}
				else {
					DragonAPICore.logError("Failed to set app small icon '"+smallID+"'; no such icon");
				}
			}
			else {
				if (!isSame(currentSmallIcon, currentSmallIconIndex, hSmall, smallIndex) || !isSame(currentLargeIcon, currentLargeIconIndex, hLarge, largeIndex)) {
					Display.setIcon(new ByteBuffer[] {hSmall == null ? defaultSmallIcon.getBuffer(smallIndex) : hSmall.getBuffer(smallIndex), hLarge == null ? defaultLargeIcon.getBuffer(largeIndex) : hLarge.getBuffer(largeIndex)});
					currentSmallIcon = hSmall;
					currentLargeIcon = hLarge;
					currentLargeIconIndex = largeIndex;
					currentSmallIconIndex = smallIndex;
				}
			}
		}
		catch (Exception e) {
			DragonAPICore.logError("Failed to set app icon '"+smallID+"'/'"+largeID+"'");
			e.printStackTrace();
		}
	}

	private static boolean isSame(AppIcon current, int currentIndex, AppIcon put, int idx) {
		return current != null && current.equals(put) && idx == currentIndex;
	}

	public static class AppIcon {

		public final int size;
		public final String iconID;

		private long[] iconWMHandle;
		private final ByteBuffer[] textureBuffer;

		public AppIcon(String id, int size, ByteBuffer... buf) {
			this.size = size;
			iconID = id;
			textureBuffer = buf;

			int expectedBytes = size * size * 4;
			for (int i = 0; i < buf.length; i++) {
				if (buf[i].remaining() != expectedBytes) {
					throw new IllegalArgumentException(String.format("AppIcon '%s' frame %d has %d bytes, expected %d for %dx%d RGBA", id, i, buf[i].remaining(), expectedBytes, size, size));
				}
			}
		}

		public long getHandle(int idx) {
			if (idx < 0 || idx >= iconWMHandle.length)
				idx = 0;
			return iconWMHandle[idx];
		}

		public ByteBuffer getBuffer(int idx) {
			if (idx < 0 || idx >= textureBuffer.length)
				idx = 0;
			return textureBuffer[idx];
		}

		private AppIcon bake() throws Exception {
			if (!IS_WINDOWS)
				return this;
			iconWMHandle = new long[textureBuffer.length];
			int pixels = size * size;
			for (int i = 0; i < textureBuffer.length; i++) {
				ByteBuffer dup = textureBuffer[i].duplicate();
				dup.clear();
				ByteBuffer rgbaBytes = ByteBuffer.allocateDirect(pixels * 4);
				for (int p = 0; p < pixels; p++) {
					rgbaBytes.put(dup.get()); // R
					rgbaBytes.put(dup.get()); // G
					rgbaBytes.put(dup.get()); // B
					rgbaBytes.put(dup.get()); // A
				}
				rgbaBytes.flip();
				iconWMHandle[i] = (Long)createIconMethod.invoke(null, size, size, rgbaBytes.asIntBuffer());
			}
			return this;
		}

		@Override
		public String toString() {
			return String.format("AppIcon '%s' %dx%d: %s", iconID, size, textureBuffer.length, Arrays.toString(iconWMHandle));
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof AppIcon && Objects.equals(((AppIcon)o).iconID, iconID);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(iconID);
		}

	}

	public static ByteBuffer generateIcon(BufferedImage raw) {
		return generateIcon(raw, raw.getWidth(), raw.getHeight(), null, 0, 0);
	}

	private static ByteBuffer generateIcon(BufferedImage raw, int w, int h, int[] scratch, int offsetX, int offsetY) {
		if (scratch == null || scratch.length != w*h)
			scratch = new int[w * h];
		ByteBuffer ret = ByteBuffer.allocateDirect(w * h * 4); // 4 bytes for RGBA
		ret.order(ByteOrder.nativeOrder());
		raw.getRGB(offsetX, offsetY, w, h, scratch, 0, w);
		for (int pixel : scratch) {
			ret.put((byte) ((pixel >> 16) & 0xFF));
			ret.put((byte) ((pixel >> 8) & 0xFF));
			ret.put((byte) (pixel & 0xFF));
			ret.put((byte) ((pixel >> 24) & 0xFF));
		}
		ret.flip();
		return ret;
	}

	public static ByteBuffer[] generateIconFrames(BufferedImage raw, int size) {
		int w = raw.getWidth();
		int h = raw.getHeight();
		int cols = w/size;
		int rows = h/size;
		int frameCount = rows*cols;
		ByteBuffer[] frames = new ByteBuffer[frameCount];
		int[] scratch = new int[size * size];
		for (int i = 0; i < frameCount; i++) {
			int col = i%cols;
			int row = i/cols;
			frames[i] = generateIcon(raw, size, size, scratch, col*size, row*size);
		}
		//return raw.getSubimage(0, 0, size, size);
		return frames;
	}

	public static void setDefaultTitle(String s) {
		defaultTitle = s;
		setCurrentTitle(null);
	}

	public static String getDefaultTitle() {
		return defaultTitle;
	}

	public static void setCurrentTitle(String s) {
		if (Thread.currentThread() != lwjglThread) {
			return;
		}
		if (Strings.isNullOrEmpty(s))
			s = defaultTitle;
		if (currentTitle != null && currentTitle.equals(s))
			return;
		currentTitle = s;
		Display.setTitle(s);
	}
}
