package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
		
	private static boolean inProgress = false;
	
	static LoadProcess loadProcess;
	static ConfigProcess configProcess;
	private static BukkitTask loadTask;
	private static BukkitTask configTask;
	
	
	//===================================CONTROLS==================================
	
	private static final void schedule()
	{
		loadTask = Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, loadProcess, 0, 200);
		configTask = Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, configProcess, 0, 400);
	}
	//-----------------------------------------------------------------------------
	static final boolean start(String[] args, String name)
	{
		boolean isNew;
		if (isNew = ConfigProcess.isNew(name))
		{
			loadProcess = new LoadProcess(name, WorldObj.generate(args));
		}
		else
		{
			loadProcess = new LoadProcess(name);
		}
		configProcess = new ConfigProcess(name);
		schedule();
		return isNew;
	}
	private static final void stop_or_finish()
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
		stop_or_finish();
	}
	private static final boolean stop()
	{
		if (inProgress)
		{
			if (loadProcess.n == loadProcess.totalRegions) finish();
			else
			{
				configProcess.stop();
				stop_or_finish();
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
