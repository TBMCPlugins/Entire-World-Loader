package iieLoadSaveEntireWorld;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private static Main plugin;
	public static Main getPlugin()
	{
		return plugin;
	}
	
	public void onEnable() 
	{
		//saveDefaultConfig();
		plugin = this;
		getCommand("beginfullmapload").setExecutor(new TaskManager.StartCommand());
		getCommand("stopfullmapload").setExecutor(new TaskManager.StopCommand());
	}
}
