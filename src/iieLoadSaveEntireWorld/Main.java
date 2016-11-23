package iieLoadSaveEntireWorld;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	
	static Main plugin;
	static FileConfiguration config;
	static StartCommand start;
	static StopCommand stop;
	
	static BukkitTask task;
	
	public void onEnable() 
	{
		plugin = this;
		config = plugin.getConfig();
		start = new StartCommand(plugin);
		stop = new StopCommand(plugin);
		
		saveDefaultConfig();
		getCommand("beginloadsave").setExecutor(start);
		getCommand("stoploadsave").setExecutor(stop);
		Cache.updateListUnfinished();
	}
}
