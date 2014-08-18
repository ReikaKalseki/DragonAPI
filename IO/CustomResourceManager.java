/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import Reika.DragonAPI.Instantiable.IO.DirectResource;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class CustomResourceManager extends SimpleReloadableResourceManager {

	private final SimpleReloadableResourceManager original;
	private final HashMap<String, Class> classes = new HashMap();
	private final HashMap<String, SoundEventAccessorComposite> accessors = new HashMap();

	public CustomResourceManager(SimpleReloadableResourceManager mg) {
		super(mg.rmMetadataSerializer);
		original = mg;
		//this.registerReloadListener(this);
	}

	public static CustomResourceManager getRegisteredInstance() {
		return (CustomResourceManager)Minecraft.getMinecraft().mcResourceManager;
	}

	@Override
	public IResource getResource(ResourceLocation loc) throws IOException {
		String dom = loc.getResourceDomain();
		if (dom.equals("custom_path")) {
			String path = loc.getResourcePath();
			Class c = this.getClassFor(path);
			return new DirectResource(c, path);
		}
		else {
			return original.getResource(loc);
		}
	}

	public void registerCustomPath(Class root, String path, SoundCategory cat) {
		classes.put(path, root);

		ResourceLocation rl = new ResourceLocation("custom_path", path);
		SoundPoolEntry spe = new SoundPoolEntry(rl, 1, 1, false);
		SoundEventAccessor pos = new SoundEventAccessor(spe, 1);
		SoundEventAccessorComposite cmp = new SoundEventAccessorComposite(rl, 1, 1, cat);
		cmp.addSoundToEventPool(pos);
		accessors.put(path, cmp);
	}

	public void registerSound(String domain, String path, SoundCategory cat) {
		ResourceLocation rl = new ResourceLocation(domain, path);
		SoundPoolEntry spe = new SoundPoolEntry(rl, 1, 1, false);
		SoundEventAccessor pos = new SoundEventAccessor(spe, 1);
		SoundEventAccessorComposite cmp = new SoundEventAccessorComposite(rl, 1, 1, cat);
		cmp.addSoundToEventPool(pos);
		accessors.put(path, cmp);
	}

	public void initToSoundRegistry() {
		for (String path : accessors.keySet()) {
			Minecraft.getMinecraft().getSoundHandler().sndRegistry.registerSound(accessors.get(path));
		}
	}

	private Class getClassFor(String path) {
		return classes.get(path);
	}
	/*
	@Override
	public void onResourceManagerReload(IResourceManager rm) {
		this.initToSoundRegistry();
	}*/

	@Override
	public void notifyReloadListeners() {
		super.notifyReloadListeners();
		original.notifyReloadListeners();
		this.initToSoundRegistry();
	}

}
