package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
	
	private static Main plugin;
	StartCommand(Main Plugin){
		plugin = Plugin;
	}
	@Override
	public synchronized boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
	{
		String worldName = ((Player)sender).getWorld().getName();
		if (LoadSaveProcess.inProgress)
		{
			sender.sendMessage("a process is already running (" + worldName + "). /StopLoadSave to stop.");
			return false;
		}
		else LoadSaveProcess.inProgress = true;
		if ((Cache.isUnfinished(worldName)))
		{
			sender.sendMessage("resuming...");
			LoadSaveProcess.resume(worldName);
		}
		else
		{
			Dimensions d = new Dimensions(args);
			Main.process = new LoadSaveProcess( d.width, d.center, d.lowerleft, worldName );
		}
		Main.task = Bukkit.getScheduler().runTaskTimer( plugin, Main.process, 0, 100 );
		return true;
	}
}
