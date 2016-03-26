/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Deprecated
public class VanillaOnlyResourceManager implements IResourceManager, IResourceManagerReloadListener {

	private static final VanillaOnlyResourceManager instance = new VanillaOnlyResourceManager();

	private static final String TAG = "vanilla_path";

	private VanillaOnlyResourceManager() {
		super();
		//this.registerReloadListener(this);
	}

	public static VanillaOnlyResourceManager getInstance() {
		return instance;
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(TAG, path);
	}

	@Override
	public IResource getResource(ResourceLocation loc) throws IOException {
		IResourcePack iresourcepack = null;

		ResourceLocation resourcelocation1 = getLocationMcmeta(loc);

		IResourcePack iresourcepack1 = Minecraft.getMinecraft().mcDefaultResourcePack;

		if (iresourcepack == null && iresourcepack1.resourceExists(resourcelocation1))
		{
			iresourcepack = iresourcepack1;
		}

		if (iresourcepack1.resourceExists(loc))
		{
			InputStream inputstream = null;

			if (iresourcepack != null)
			{
				inputstream = iresourcepack.getInputStream(resourcelocation1);
			}

			try {
				Field f = Minecraft.class.getDeclaredField("metadataSerializer_");
				f.setAccessible(true);
				IMetadataSerializer frmMetadataSerializer = (IMetadataSerializer)f.get(Minecraft.getMinecraft());
				return new SimpleResource(loc, iresourcepack1.getInputStream(loc), inputstream, frmMetadataSerializer);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new FileNotFoundException(loc.toString());
	}

	static ResourceLocation getLocationMcmeta(ResourceLocation p_110537_0_)
	{
		return new ResourceLocation(p_110537_0_.getResourceDomain(), p_110537_0_.getResourcePath() + ".mcmeta");
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
	@Deprecated
	public void onResourceManagerReload(IResourceManager rm) {
		//((SimpleReloadableResourceManager)rm).domainResourceManagers.put(TAG, this);
	}

}
