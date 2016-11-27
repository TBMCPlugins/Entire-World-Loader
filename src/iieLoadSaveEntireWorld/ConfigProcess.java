package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	//STATIC
	static Main plugin;
	static FileConfiguration config;
	static final boolean isNew(String name)
	{
		return !config.contains(name);
	}
	static final void addNew(String name, WorldObject newWorld)
	{
		config.set(name + ".width", newWorld.width);
		config.set(name + ".lowerleft.x", newWorld.lowerleft[0]);
		config.set(name + ".lowerleft.z", newWorld.lowerleft[1]);
		config.set(name + ".currentRegion.x", newWorld.current[0]);
		config.set(name + ".currentRegion.z", newWorld.current[1]);
		config.set(name + ".n", 1);
		config.set(name + ".c", 1);
		config.set(name + ".D", 1);
		config.set(name + ".d", 0);
		config.set(name + ".B", 0);
		plugin.saveConfig();
	}
	static final WorldObject getUnfinished(String name)
	{
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
	static final boolean crashResume()
	{
		return config.contains("@ CRASH RESUME");
	}
	static final String getCrashResume()
	{
		return config.getString("@ CRASH RESUME");
	}
	
	
	//INSTANCE
	private final String name = TaskManager.loadProcess.worldname;
	ConfigProcess()
	{
		config.set("@ CRASH RESUME", name);
	}
	ConfigProcess(boolean b)
	{
		//don't create crash resume
	}
	public final void run()
	{
		final int[] currentRegion = TaskManager.loadProcess.currentRegion;
		Bukkit.getLogger().info
				(
						"Saving world-load progress: " + name 
						+ "["
						+ currentRegion[0] + "," 
						+ currentRegion[1] 
						+ "]"
						);
		config.set(name + ".currentRegion.x", currentRegion[0]);
		config.set(name + ".currentRegion.z", currentRegion[1]);
		config.set(name + ".n", TaskManager.loadProcess.n);
		config.set(name + ".c", TaskManager.loadProcess.c);
		config.set(name + ".D", TaskManager.loadProcess.D);
		config.set(name + ".d", TaskManager.loadProcess.d);
		config.set(name + ".B", TaskManager.loadProcess.B ? 1 : 0);
		plugin.saveConfig();
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
		Bukkit.getLogger().info("...stopping world-load");
		
	}
}
