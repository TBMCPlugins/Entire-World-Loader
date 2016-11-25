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
		String worldname = ((Player)sender).getWorld().getName();
		if (LoadSaveProcess.inProgress)
		{
			sender.sendMessage("a process is already running (" + worldname + "). /StopLoadSave to stop.");
			return false;
		}
		else LoadSaveProcess.inProgress = true;
		if (ConfigManager.isNew(worldname))
		{
			sender.sendMessage("starting...");
			LoadSaveProcess.start(worldname, Dimensions.generate(args));
		}
		else
		{
			sender.sendMessage("resuming...");
			LoadSaveProcess.resume(worldname);
		}
		Main.task = Bukkit.getScheduler().runTaskTimer( plugin, Main.process, 0, 100 );
		return true;
	}
}
