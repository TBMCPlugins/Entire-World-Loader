package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigProcess implements Runnable {
	
	//STATIC
	private static final Main plugin = new Main();
	private static final FileConfiguration config = plugin.getConfig();
	
	static final boolean isNew(String name)
	{
		return !config.contains(name);
	}
	static final void addNew(String name, int width, int[] lowerleft, int[] center)
	{
		config.set(name + ".width", width);
		config.set(name + ".lowerleft.x", lowerleft[0]);
		config.set(name + ".lowerleft.z", lowerleft[1]);
		config.set(name + ".currentRegion.x", center[0]);
		config.set(name + ".currentRegion.z", center[1]);
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
	
	
	//INSTANCE
	private final String name = TaskManager.loadProcess.worldname;
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
		config.set("finished", name);
		config.set(name, null);
		plugin.saveConfig();
	}
}
