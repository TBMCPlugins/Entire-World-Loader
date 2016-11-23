package iieLoadSaveEntireWorld;

import java.util.Set;

public class Cache {
	
	private static int maxNameLength;
	private static char[][] worldsUnfinished;
	static class WorldStatus {
		
	}
	static void updateListUnfinished()
	{
		Set<String> unfin = Main.config.getConfigurationSection("unfinished worlds").getKeys(false);
		
		maxNameLength = Main.config.getInt("max name length");
		worldsUnfinished = populate(unfin);
	}
	static char[][] populate(Set<String> set)
	{
		char[][] worlds = new char[set.size()][maxNameLength];
		int i = 0;
		int ii = 0;
		for (String name : set)
		{
			for (char c : name.toCharArray())
			{
				worlds[i][ii] = c;
				ii++;
			}
			i++;
			ii = 0;
		}
		return worlds;
	}
	static boolean isUnfinished(String name){
		int i = 0;
		boolean match = true;
		for (char[] world : worldsUnfinished)
		{
			for (char c : name.toCharArray())
			{
				if (c != world[i]){ match = false; break; } 
			}
			if (match) break;
			i = 0;
			match = true;
		}
		return match;
	}

}
