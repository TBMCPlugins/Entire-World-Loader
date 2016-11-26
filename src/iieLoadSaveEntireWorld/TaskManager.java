package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
	
	private static final Main plugin = new Main();
	
	static	boolean 		inProgress = false;
	static 	LoadProcess 	loadProcess;
	static	ConfigProcess 	configProcess;
	static	BukkitTask 		loadTask;
	static	BukkitTask 		configTask;
	
	//===================================CONTROLS===================================
	private static final void start(LoadProcess loadProcess)
	{
		inProgress = true;
		TaskManager.loadProcess = loadProcess;
		TaskManager.loadTask = Bukkit.getScheduler().runTaskTimer( plugin, loadProcess, 0, 10 );
		TaskManager.configTask = Bukkit.getScheduler().runTaskTimer( plugin, new ConfigProcess(), 0, 200 );
	}
	static final void finish()
	{
		configProcess.finish();
		loadTask.cancel();
		configTask.cancel();
		
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
			if (loadProcess.isFinished()) finish();
			else
			{
				loadTask.cancel();
				configTask.cancel();
				configProcess.run();
				
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
			String name = ((Player)sender).getWorld().getName();
			if (inProgress)
			{
				sender.sendMessage("a process is already running (" + name + "). /StopLoadSave to stop.");
				return false;
			}
			else inProgress = true;
			
			if (ConfigProcess.isNew(name))
			{
				sender.sendMessage("starting...");
				start(new LoadProcess(name, WorldObject.generate(args)));
			}
			else
			{
				sender.sendMessage("resuming...");
				start(new LoadProcess(name));
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
