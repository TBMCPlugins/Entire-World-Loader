package iieLoadSaveEntireWorld;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	
	static Main plugin;
	static BukkitTask task;
	
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		getCommand("loadsaveentireworld").setExecutor(new StartCommand(plugin));
	}
}
