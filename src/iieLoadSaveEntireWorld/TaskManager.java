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
	
	
	//=====================================UTIL====================================
	
	private static final void schedule()
	{
		loadTask 	= Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, loadProcess, 0, 200);
		configTask 	= Bukkit.getScheduler().runTaskTimer(ConfigProcess.plugin, configProcess, 100, 200);
	}
	private static final void closedown()
	{
		loadTask.cancel();
		configTask.cancel();
		
		loadProcess = null;
		configProcess = null;
		loadTask = null;
		configTask = null;
		
		inProgress = false;
	}
	
	//===================================CONTROLS==================================
	
	private static final boolean start_or_resume(String[] args, String name)
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
	static final void resume(String name)
	{
		loadProcess = new LoadProcess(name);
		configProcess = new ConfigProcess(name);
		schedule();
	}
	private static final boolean stop_or_finish()
	{
		boolean isFin;
		if (isFin = loadProcess.n == loadProcess.totalRegions)
		{
			configProcess.finish();
		}
		else
		{
			configProcess.stop();
		}
		closedown();
		return isFin;
	}
	static final void finish()
	{
		configProcess.finish();
		closedown();
	}
	
	//===================================COMMANDS===================================

	static final class StopCommand implements CommandExecutor 
	{
		public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
		{
			if (inProgress)
			{
				sender.sendMessage
				(
						stop_or_finish() ?
								"it just finished!" :
									"stopping..."
						);
				return true;
			}
			return false;
		}
	}
	static final class StartCommand implements CommandExecutor
	{
		public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
		{
			if (inProgress)
			{
				sender.sendMessage("already loading " + configProcess.name + ". /StopFullMapLoad to stop.");
				return false;
			}
			inProgress = true;
			sender.sendMessage
			(
					start_or_resume(args,((Player)sender).getWorld().getName()) ?
							"starting..." :
								"resuming..."
					);
			return true;
		}
	}
}
