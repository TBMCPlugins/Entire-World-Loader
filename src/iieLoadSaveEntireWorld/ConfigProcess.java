package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	static Main plugin;				//initialized in Main onEnable()
	static FileConfiguration config;//
	
	final String name;
	//================================STATIC================================
	static final boolean isNew(String name)
	{
		return !config.contains(name);
	}
	static final void addNew(String name, WorldObj newWorld)
	{
		config.set(name + ".total", newWorld.total);
		config.set(name + ".currentRegion.x", newWorld.current[0]);
		config.set(name + ".currentRegion.z", newWorld.current[1]);
		config.set(name + ".n", 1);
		config.set(name + ".c", 1);
		config.set(name + ".D", 1);
		config.set(name + ".d", 0);
		config.set(name + ".B", 0);
		plugin.saveConfig();
	}
	static final WorldObj getUnfinished(String name)
	{
		return new WorldObj
				(
						config.getInt(name + ".total"),
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
	static final boolean crashResume()
	{
		return config.contains("@ CRASH RESUME");
	}
	static final String getCrashResume()
	{
		return config.getString("@ CRASH RESUME");
	}
	
	//===============================INSTANCE===============================
	ConfigProcess(String name)
	{
		this.name = name;
		config.set("@ CRASH RESUME", name);
	}	
	ConfigProcess(boolean b)
	{
		name = config.getString("@ CRASH RESUME");
	}
	public final void run()
	{		
		config.set(name + ".currentRegion.x", TaskManager.loadProcess.currentRegion[0]);
		config.set(name + ".currentRegion.z", TaskManager.loadProcess.currentRegion[1]);
		config.set(name + ".n", TaskManager.loadProcess.n);
		config.set(name + ".c", TaskManager.loadProcess.c);
		config.set(name + ".D", TaskManager.loadProcess.D);
		config.set(name + ".d", TaskManager.loadProcess.d);
		config.set(name + ".B", TaskManager.loadProcess.B ? 1 : 0);
		plugin.saveConfig();
		Bukkit.getLogger().info
		(
				"Saving world-load progress: " + name 
				+ "["
				+ TaskManager.loadProcess.currentRegion[0] + "," 
				+ TaskManager.loadProcess.currentRegion[1] 
				+ "]"
				);
	}
	final void finish()
	{
		config.set("@ FINISHED WORLDS", name);
		config.set("@ CRASH RESUME", null);
		config.set(name, null);
		plugin.saveConfig();
	}
	final void stop()
	{
		run();
		config.set("@ CRASH RESUME", null); 
		plugin.saveConfig();
		Bukkit.getLogger()
				.info("...stopping world-load");
	}
}
