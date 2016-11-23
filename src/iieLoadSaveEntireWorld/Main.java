package iieLoadSaveEntireWorld;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	
	static Main plugin;
	static FileConfiguration config;
	static StartCommand start;
	static LoadSaveProcess process;
	static BukkitTask task;
	
	public void onEnable() 
	{
		plugin = this;
		config = plugin.getConfig();
		start = new StartCommand(plugin);
		
		saveDefaultConfig();
		getCommand("beginloadsave").setExecutor(start);
		getCommand("stoploadsave").setExecutor(new StopCommand(plugin));
		Cache.set();
	}
}
