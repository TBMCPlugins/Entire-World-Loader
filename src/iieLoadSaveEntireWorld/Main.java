package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
		
	public void onEnable() 
	{
		saveDefaultConfig();
		
		getCommand("beginfullmapload").setExecutor(new TaskManager.StartCommand());
		getCommand("stopfullmapload").setExecutor(new TaskManager.StopCommand());
		
		TaskManager.plugin = this;
		ConfigProcess.plugin = this;
		ConfigProcess.config = getConfig();
		
		if (TaskManager.crashResume())
		{
			Bukkit.getLogger().info("...resuming from crash");
		}
	}
}
