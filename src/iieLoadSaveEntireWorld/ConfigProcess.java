package iieLoadSaveEntireWorld;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	//STATIC
	private static final Main plugin = new Main();
	private static final FileConfiguration config = plugin.getConfig();
	
	static final boolean isNew(String name)
	{
		return !config.contains(name);
	}
	static final WorldObject getUnfinished(String name)
	{
		return new WorldObject
				(
						config.getInt(name + ".width"),
						new int[]
								{	
										config.getInt(name + ".currentRegion.x"),
										config.getInt(name + ".currentRegion.z")
										},
						new int[]
								{	
										config.getInt(name + ".lowerleft.x"),
										config.getInt(name + ".lowerleft.z")
										},
						config.getInt(name + ".n"),
						config.getInt(name + ".c"),
						config.getInt(name + ".D"),
						config.getInt(name + ".d"),
						config.getInt(name + ".B") == 1
						);		
	}
	
	
	//INSTANCE
	private final String name = TaskManager.loadProcess.worldname;
	public final void run()
	{
		config.set(".currentRegion.x", TaskManager.loadProcess.currentRegion[0]);
		config.set(".currentRegion.z", TaskManager.loadProcess.currentRegion[1]);
		config.set(".n", TaskManager.loadProcess.n);
		config.set(".c", TaskManager.loadProcess.c);
		config.set(".D", TaskManager.loadProcess.D);
		config.set(".d", TaskManager.loadProcess.d);
		config.set(".B", TaskManager.loadProcess.B ? 1 : 0);
		plugin.saveConfig();
	}
	final void finish()
	{
		config.set("finished", name);
		config.set(name, null);
		plugin.saveConfig();
	}
}
