package iieLoadSaveEntireWorld;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	
	static Main plugin;
	static FileConfiguration config;
	static ConfigurationSection unfinished;
	
	static StartCommand start;
	static StopCommand stop;
	
	static LoadSaveProcess process;
	static BukkitTask task;
	
	public void onEnable() 
	{
		plugin = this;
		config = plugin.getConfig();
		unfinished = config.getConfigurationSection("unfinished worlds");
		start = new StartCommand(plugin);
		stop = new StopCommand(plugin);
		
		saveDefaultConfig();
		getCommand("beginloadsave").setExecutor(start);
		getCommand("stoploadsave").setExecutor(stop);
	}
}
