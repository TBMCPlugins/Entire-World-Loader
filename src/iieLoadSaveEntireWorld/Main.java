package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
			
	public void onEnable() 
	{
		saveDefaultConfig();
		getCommand("beginfullmapload").setExecutor(new TaskManager.StartCommand());
		getCommand("stopfullmapload").setExecutor(new TaskManager.StopCommand());
		
		ConfigProcess.plugin = this;
		ConfigProcess.config = getConfig();
		
		if (ConfigProcess.crashResume())
		{
			Bukkit.getScheduler().runTaskLater(this, new CrashResume(), 600);
		}
	}
	private static final class CrashResume implements Runnable
	{
		public final void run()
		{
			Bukkit.getLogger().info("...resuming from crash");
			TaskManager.start(null, ConfigProcess.getCrashResume());
		}
	}
}
