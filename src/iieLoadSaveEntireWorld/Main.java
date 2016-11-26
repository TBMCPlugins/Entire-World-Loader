package iieLoadSaveEntireWorld;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public void onEnable() 
	{
		saveDefaultConfig();
		getCommand("beginfullmapload").setExecutor(new TaskManager.StartCommand());
		getCommand("stopfullmapload").setExecutor(new TaskManager.StopCommand());
	}
}
