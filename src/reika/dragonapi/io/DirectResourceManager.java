/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.instantiable.io.DirectResource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class DirectResourceManager implements IResourceManager, IResourceManagerReloadListener {

	private final HashMap<String, SoundEventAccessorComposite> accessors = new HashMap();

	private static final DirectResourceManager instance = new DirectResourceManager();

	private static final String TAG = "custom_path";

	private DirectResourceManager() {
		super();
		//this.registerReloadListener(this);
	}

	public static DirectResourceManager getInstance() {
		return instance;
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(TAG, path);
	}

	@Override
	public IResource getResource(ResourceLocation loc) throws IOException {
		String dom = loc.getResourceDomain();
		//if (dom.equals("custom_path")) {
		String path = loc.getResourcePath();
		return new DirectResource(path);
		//}
		//else {
		//	return original.getResource(loc);
		//}
	}

	public void registerCustomPath(String path, SoundCategory cat, boolean streaming) {
		ResourceLocation rl = new ResourceLocation(TAG, path);
		SoundPoolEntry spe = new SoundPoolEntry(rl, 1, 1, streaming);
		SoundEventAccessor pos = new SoundEventAccessor(spe, 1);
		SoundEventAccessorComposite cmp = new SoundEventAccessorComposite(rl, 1, 1, cat);
		cmp.addSoundToEventPool(pos);
		accessors.put(path, cmp);
	}

	@Deprecated
	public void registerSound(String domain, String path, SoundCategory cat) {
		ResourceLocation rl = new ResourceLocation(domain, path);
		SoundPoolEntry spe = new SoundPoolEntry(rl, 1, 1, false);
		SoundEventAccessor pos = new SoundEventAccessor(spe, 1);
		SoundEventAccessorComposite cmp = new SoundEventAccessorComposite(rl, 1, 1, cat);
		cmp.addSoundToEventPool(pos);
		accessors.put(path, cmp);
	}

	private void initToSoundRegistry() {
		SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
		if (sh == null) {
			DragonAPICore.logError("Attempted to initialize sound entries before the sound handler was created!");
			return;
		}
		SoundRegistry srg = sh.sndRegistry;
		if (srg == null) {
			DragonAPICore.logError("Attempted to initialize sound entries before the sound registry was created!");
			return;
		}
		for (String path : accessors.keySet()) {
			srg.registerSound(accessors.get(path));
		}
	}
	/*
	@Override
	public void onResourceManagerReload(IResourceManager rm) {
		this.initToSoundRegistry();
	}*/
	/*
	@Override
	public void notifyReloadListeners() {
		super.notifyReloadListeners();
		original.notifyReloadListeners();
		this.initToSoundRegistry();
	}*/

	public Set<String> getResourceDomains() {
		return ImmutableSet.of(TAG);
	}

	public List<IResource> getAllResources(ResourceLocation resource) throws IOException {
		return ImmutableList.of(this.getResource(resource));
	}

	@Override
	public void onResourceManagerReload(IResourceManager rm) {
		((SimpleReloadableResourceManager)rm).domainResourceManagers.put(TAG, this);
		this.initToSoundRegistry();
	}

	/*
	public static ResourceLocation getCompletedResourcePath(TextureMap map, ResourceLocation def, int tex) {
		if (def.getResourceDomain().equals(TAG))
			return def;
		if (def.getResourceDomain().equals("vanilla"))
			return def;
		String s = map.getTextureType() == 0 ? "textures/blocks" : "textures/items";
		return tex == 0 ? new ResourceLocation(def.getResourceDomain(), String.format("%s/%s%s", new Object[] {s, def.getResourcePath(), ".png"})): new ResourceLocation(def.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", new Object[] {s, def.getResourcePath(), Integer.valueOf(tex), ".png"}));
	}
	 */

}
