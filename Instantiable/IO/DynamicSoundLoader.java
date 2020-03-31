package Reika.DragonAPI.Instantiable.IO;

import java.util.Collection;

import Reika.DragonAPI.Interfaces.Registry.SoundEnum;

public class DynamicSoundLoader extends SoundLoader {

	public DynamicSoundLoader(Collection<SoundEnum> sounds) {
		super(sounds);
	}

	public DynamicSoundLoader(SoundEnum... sounds) {
		super(sounds);
	}

}
