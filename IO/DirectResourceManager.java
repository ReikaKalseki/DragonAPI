/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.IO.DirectResource;
import Reika.DragonAPI.Instantiable.IO.DynamicDirectResource;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DirectResourceManager implements IResourceManager, IResourceManagerReloadListener {

	private final HashMap<String, SoundEventAccessorComposite> accessors = new HashMap();
	private final HashMap<String, RemoteSourcedAsset> dynamicAssets = new HashMap();
	private final HashSet<String> streamedPaths = new HashSet();

	private static final DirectResourceManager instance = new DirectResourceManager();

	private static final String TAG = "custom_path";

	private DirectResourceManager() {
		super();
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
		String path = loc.getResourcePath();
		RemoteSourcedAsset rem = dynamicAssets.get(path);
		DirectResource ret = rem != null ? new DynamicDirectResource(rem) : new DirectResource(path);
		if (streamedPaths.contains(ret.path))
			ret.cacheData = false;
		return ret;
	}

	public void registerDynamicAsset(String path, RemoteSourcedAsset a) {
		dynamicAssets.put(path, a);
	}

	public void registerCustomPath(String path, SoundCategory cat, boolean streaming) {
		ResourceLocation rl = new ResourceLocation(TAG, path);
		SoundPoolEntry spe = new SoundPoolEntry(rl, 1, 1, streaming);
		SoundEventAccessor pos = new SoundEventAccessor(spe, 1);
		SoundEventAccessorComposite cmp = new SoundEventAccessorComposite(rl, 1, 1, cat);
		cmp.addSoundToEventPool(pos);
		accessors.put(path, cmp);
		if (streaming) {
			streamedPaths.add(path);
		}
	}

	public void initToSoundRegistry() {
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

}
