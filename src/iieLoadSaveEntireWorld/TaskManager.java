package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
		
	static boolean inProgress = false;
	
	static LoadProcess loadProcess;
	static ConfigProcess configProcess;
	private static BukkitTask loadTask;
	private static BukkitTask configTask;
	
	
	//===================================CONTROLS==================================
	
	private static final void schedule(long delay)
	{
		loadTask = Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, loadProcess, delay, 100);
		configTask = Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, loadProcess, delay, 100);
	}
	//-----------------------------------------------------------------------------
	private static final boolean start(String[] args, String name)
	{
		if (ConfigProcess.isNew(name))
		{
			loadProcess = new LoadProcess(name, WorldObj.generate(args));
			configProcess = new ConfigProcess(name);
			schedule(0);
			return true;
		}
		loadProcess = new LoadProcess(name);
		configProcess = new ConfigProcess(name);
		schedule(0);
		return false;
	}
	static final boolean crashResume()
	{
		if (ConfigProcess.crashResume())
		{
			loadProcess = new LoadProcess(ConfigProcess.getCrashResume());
			configProcess = new ConfigProcess(false);
			schedule(1200);
			return true;
		}
		return false;
	}
	static final void stop_or_finish()
	{
		loadTask.cancel();
		configTask.cancel();
		
		loadProcess = null;
		configProcess = null;
		loadTask = null;
		configTask = null;
		
		inProgress = false;
	}
	static final void finish()
	{
		configProcess.finish();
		
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

	static final class StopCommand implements CommandExecutor 
	{
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
	static final class StartCommand implements CommandExecutor
	{
		public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
		{
			if (inProgress)
			{
				sender.sendMessage("a process is already running (" + configProcess.name + "). /StopLoadSave to stop.");
				return false;
			}
			inProgress = true;
			sender.sendMessage
			(
					start(args,((Player)sender).getWorld().getName()) ?
							"starting..." :
								"resuming..."
					);
			return true;
		}
	}
}
