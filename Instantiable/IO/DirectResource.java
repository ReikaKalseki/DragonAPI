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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class DirectResource implements IResource {

	public final String path;
	private byte[] data;

	public DirectResource(String path) {
		this.path = path;
	}

	@Override
	public InputStream getInputStream() {
		if (data == null) {
			data = ReikaJavaLibrary.streamToBytes(DragonAPIInit.class.getClassLoader().getResourceAsStream(path));
		}
		return new ByteArrayInputStream(data);
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
