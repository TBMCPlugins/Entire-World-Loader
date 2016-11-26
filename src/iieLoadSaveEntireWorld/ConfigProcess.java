package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	//STATIC
	private static FileConfiguration config;
	
	static final boolean isNew(String name)
	{
		if(config == null)
			config = Main.getPlugin().getConfig();
		return !config.contains(name);
	}
	static final WorldObject getUnfinished(String name)
	{
		if(config == null)
			config = Main.getPlugin().getConfig();
		return new WorldObject
				(
						config.getInt(name + ".width"),
						new int[]
								{	
										config.getInt(name + ".lowerleft.x"),
										config.getInt(name + ".lowerleft.z")
										},
						new int[]
								{	
										config.getInt(name + ".currentRegion.x"),
										config.getInt(name + ".currentRegion.z")
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
		if(config == null)
			config = Main.getPlugin().getConfig();
		Bukkit.getLogger().info("Loading in progress: " + name
				+ "[" + TaskManager.loadProcess.currentRegion[0] + ","
				+ TaskManager.loadProcess.currentRegion[1] + "]");
		config.set(name + ".width", TaskManager.loadProcess.width);
		config.set(name + ".lowerleft.x", TaskManager.loadProcess.lowerleft[0]);
		config.set(name + ".lowerleft.z", TaskManager.loadProcess.lowerleft[1]);
		config.set(name + ".currentRegion.x", TaskManager.loadProcess.currentRegion[0]);
		config.set(name + ".currentRegion.z", TaskManager.loadProcess.currentRegion[1]);
		config.set(name + ".n", TaskManager.loadProcess.n);
		config.set(name + ".c", TaskManager.loadProcess.c);
		config.set(name + ".D", TaskManager.loadProcess.D);
		config.set(name + ".d", TaskManager.loadProcess.d);
		config.set(name + ".B", TaskManager.loadProcess.B ? 1 : 0);
		Main.getPlugin().saveConfig();
	}
	final void finish()
	{
		if(config == null)
			config = Main.getPlugin().getConfig();
		config.set("finished", name);
		config.set(name, null);
		Main.getPlugin().saveConfig();
	}
}
