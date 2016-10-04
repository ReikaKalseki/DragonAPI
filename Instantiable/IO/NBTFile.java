/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public abstract class NBTFile {

	public final String name;

	private final String filepath;
	private final Class reference;

	public boolean compressData = false;
	public boolean encryptData = false;

	public NBTFile(File f) {
		this(f.getName(), f.getAbsolutePath(), null);
	}

	public NBTFile(String name, String path, Class c) {
		this.name = name;
		filepath = path;
		reference = c;
	}

	private InputStream encryptStreamData(InputStream in, boolean unpack) throws IOException {
		ArrayList<Byte> data = new ArrayList();
		int dat = in.read();
		while (dat != -1) {
			data.add((byte)dat);
			dat = in.read();
		}
		in.close();
		//ReikaJavaLibrary.cycleList(data, unpack ? -8 : 8);
		Collections.reverse(data);
		byte[] arr = new byte[data.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = data.get(i);
		}
		return new ByteArrayInputStream(arr);
	}

	private void encryptFileData(File f, boolean unpack) throws IOException {
		InputStream in = new FileInputStream(f);
		ArrayList<Byte> data = new ArrayList();
		int dat = in.read();
		while (dat != -1) {
			data.add((byte)dat);
			dat = in.read();
		}
		in.close();
		//ReikaJavaLibrary.cycleList(data, unpack ? -8 : 8);
		Collections.reverse(data);
		OutputStream out = new FileOutputStream(f);
		for (byte b : data) {
			out.write(b);
		}
		out.flush();
		out.close();
	}

	public final void load() throws IOException {
		if (reference != null) {
			InputStream in = reference.getResourceAsStream(filepath);

			if (encryptData)
				in = this.encryptStreamData(in, true);

			NBTTagCompound tag = compressData ? CompressedStreamTools.readCompressed(in) : ReikaFileReader.readUncompressedNBT(in);
			this.setDataFromLines(tag);
		}
		else {
			File f = new File(filepath);
			if (!f.exists())
				return;

			if (encryptData)
				this.encryptFileData(f, true);

			NBTTagCompound tag = compressData ? CompressedStreamTools.readCompressed(new FileInputStream(f)) : ReikaFileReader.readUncompressedNBT(f);
			this.setDataFromLines(tag);
		}
	}

	public final void save() throws IOException {
		File f = new File(filepath);
		f.getParentFile().mkdirs();
		f.delete();
		f.createNewFile();
		NBTTagCompound tag = this.getDataAsLines();

		if (compressData)
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(f));
		else
			ReikaFileReader.writeUncompressedNBT(tag, f);

		if (encryptData)
			this.encryptFileData(f, false);
	}

	private NBTTagCompound getDataAsLines() {
		NBTTagCompound dat = new NBTTagCompound();
		NBTTagCompound header = new NBTTagCompound();
		this.writeHeader(header);

		NBTTagCompound extra = this.writeExtraData();
		if (extra != null)
			header.setTag("extra", extra);

		dat.setTag("header", header);

		NBTTagList li = new NBTTagList();
		this.writeData(li);
		dat.setTag("data", li);
		return dat;
	}

	private void setDataFromLines(NBTTagCompound tag) {
		NBTTagCompound header = tag.getCompoundTag("header");
		this.readHeader(header);

		NBTTagCompound extra = header.getCompoundTag("extra");
		this.readExtraData(extra);

		NBTTagList li = tag.getTagList("data", NBTTypes.COMPOUND.ID);
		this.readData(li);
	}

	protected abstract void readHeader(NBTTagCompound header);

	/** Is a list of NBTTagCompounds! */
	protected abstract void readData(NBTTagList li);

	protected abstract void readExtraData(NBTTagCompound extra);

	protected abstract void writeHeader(NBTTagCompound header);

	/** Write a list of NBTTagCompounds! */
	protected abstract void writeData(NBTTagList li);

	protected abstract NBTTagCompound writeExtraData();

}
