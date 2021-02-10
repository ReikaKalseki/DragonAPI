package Reika.DragonAPI.Instantiable.IO;

import net.minecraft.client.audio.SoundCategory;

import Reika.DragonAPI.Interfaces.Registry.SoundEnum;

public abstract class SoundVariant<S extends SoundEnum> implements SoundEnum {

	public final S root;
	protected final String key;

	private final String name;
	private final String path;

	protected SoundVariant(S s, String k, String p) {
		key = k;
		root = s;

		name = s.getName()+"_"+k;
		path = p;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getPath() {
		return path;
	}

	@Override
	public final SoundCategory getCategory() {
		return root.getCategory();
	}

	@Override
	public final int ordinal() {
		return root.ordinal();
	}

	@Override
	public final boolean canOverlap() {
		return root.canOverlap();
	}

	@Override
	public final boolean attenuate() {
		return root.attenuate();
	}

	@Override
	public final float getModulatedVolume() {
		return root.getModulatedVolume();
	}

	@Override
	public final boolean preload() {
		return root.preload();
	}

	@Override
	public final String toString() {
		return root.toString()+"_"+this.key;
	}

}
