package iieLoadSaveEntireWorld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopCommand implements CommandExecutor{
	
	private static Main plugin;
	StopCommand(Main Plugin){
		plugin = Plugin;
	}
	@Override
	public synchronized boolean onCommand(CommandSender sender, Command label, String command, String[] args) {
		if (LoadSaveProcess.inProgress)
		{ 
			LoadSaveProcess.stop(); 
			return true; 
		}
		else 
			return false;
	}
}
