package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	//================================STATIC================================
	static Main plugin;
	static FileConfiguration config;
	//----------------------------------------------------------------------
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
		config.set(name + ".s", 1);
		config.set(name + ".d", 0);
		config.set(name + ".B", false);
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
						config.getInt(name + ".s"),
						config.getInt(name + ".d"),
						config.getBoolean(name + ".B")
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
	final String name;
	//----------------------------------------------------------------------
	ConfigProcess(String name)
	{
		this.name = name;
		config.set("@ CRASH RESUME", name);
	}
	public final void run()
	{		
		config.set(name + ".currentRegion.x", TaskManager.loadProcess.currentRegion[0]);
		config.set(name + ".currentRegion.z", TaskManager.loadProcess.currentRegion[1]);
		config.set(name + ".n", TaskManager.loadProcess.n);
		config.set(name + ".c", TaskManager.loadProcess.c);
		config.set(name + ".s", TaskManager.loadProcess.s);
		config.set(name + ".d", TaskManager.loadProcess.d);
		config.set(name + ".B", TaskManager.loadProcess.B);
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
	final void stop()
	{
		run();
		config.set("@ CRASH RESUME", null); 
		plugin.saveConfig();
		Bukkit.getLogger()
				.info("...stopping world-load");
	}
	final void finish()
	{
		config.set("@ FINISHED WORLDS", name);
		config.set("@ CRASH RESUME", null);
		config.set(name, null);
		plugin.saveConfig();
		Bukkit.getLogger()
				.info("...finished!");
	}
}
