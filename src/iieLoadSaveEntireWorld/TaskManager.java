package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
	
	static Main plugin;
	
	static boolean inProgress = false;
	static boolean canRun = true;
	
	static 	LoadProcess 	loadProcess;
	static	ConfigProcess 	configProcess;
	static	BukkitTask 		loadTask;
	static	BukkitTask 		configTask;
	
	//===================================CONTROLS===================================
	private static final boolean start(String name, String[] args)
	{
		final boolean isNew;
		if (ConfigProcess.isNew(name))
		{
			loadProcess = new LoadProcess(name, WorldObject.generate(args));
			isNew = true;
		}
		else
		{
			loadProcess = new LoadProcess(name);
			isNew = false;
		}
		configProcess = new ConfigProcess();
		loadTask = Bukkit.getScheduler().runTaskTimer( plugin, loadProcess, 0, 100 );
		configTask = Bukkit.getScheduler().runTaskTimer( plugin, configProcess, 0, 200 );
		return isNew;
	}
	static final boolean crashResume()
	{
		if (ConfigProcess.crashResume())
		{
			loadProcess = new LoadProcess(ConfigProcess.getCrashResume());
			configProcess = new ConfigProcess(false);
			
			loadTask = Bukkit.getScheduler().runTaskTimer( plugin, loadProcess, 1200, 100 );
			configTask = Bukkit.getScheduler().runTaskTimer( plugin, configProcess, 1200, 200 );
			return true;
		}
		return false;
	}
	static final void finish()
	{
		loadTask.cancel();
		configTask.cancel();
		configProcess.finish();
		
		loadProcess = null;
		configProcess = null;
		loadTask = null;
		configTask = null;
		
		inProgress = false;
	}
	static final boolean stop()
	{
		if (inProgress)
		{
			if (loadProcess.n == loadProcess.totalRegions) finish();
			else
			{
				loadTask.cancel();
				configTask.cancel();
				configProcess.stop();
				
				loadProcess = null;
				configProcess = null;
				loadTask = null;
				configTask = null;
				
				inProgress = false;
			}
			return true;
		}
		return false;
	}
	
	
	//===================================COMMANDS===================================
	
	static final class StartCommand implements CommandExecutor
	{
		@Override
		public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
		{
			if (inProgress)
			{
				sender.sendMessage("a process is already running (" + loadProcess.worldname + "). /StopLoadSave to stop.");
				return false;
			}
			else inProgress = true;
			if (start(((Player)sender).getWorld().getName(),args))
			{
				sender.sendMessage("starting...");
			}
			else
			{
				sender.sendMessage("resuming...");
			}
			return true;
		}
	}
	static final class StopCommand implements CommandExecutor 
	{
		@Override
		public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
		{
			if (stop())
			{
				sender.sendMessage("stopped.");
				return true;
			}
			sender.sendMessage("nothing to stop.");
			return false;
		}
	}
}
