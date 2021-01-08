package Reika.DragonAPI.Instantiable.IO;

import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset.RemoteSourcedAssetRepository;
import Reika.DragonAPI.Interfaces.Registry.DynamicSound;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;

public class DynamicSoundLoader extends SoundLoader {

	private final RemoteSourcedAssetRepository dataSource;

	public DynamicSoundLoader(Class<? extends DynamicSound> c, RemoteSourcedAssetRepository repo) {
		super(c);
		dataSource = repo;
	}

	public DynamicSoundLoader(DynamicSound[] sounds, RemoteSourcedAssetRepository repo) {
		super(sounds);
		dataSource = repo;
	}

	@Override
	protected void onRegister(SoundEnum e, String p) {
		DirectResourceManager.getInstance().registerDynamicAsset(p, dataSource.createAsset(((DynamicSound)e).getRelativePath()));
	}

}
