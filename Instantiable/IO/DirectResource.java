/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.InputStream;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;

public class DirectResource implements IResource {

	public final String path;
	public final Class root;

	public DirectResource(Class root, String path) {
		this.path = path;
		this.root = root;
	}

	@Override
	public InputStream getInputStream() {
		return root.getResourceAsStream(path);
	}

	@Override
	public boolean hasMetadata() {
		return false;
	}

	@Override
	public IMetadataSection getMetadata(String p_110526_1_) {
		return null;
	}

}
