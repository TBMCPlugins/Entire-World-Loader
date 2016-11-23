package iieLoadSaveEntireWorld;

import java.util.Set;

public class Cache {
	
	static int maxNameLength;
	static char[][] worldsFinished;
	static char[][] worldsUnfinished;
	static void set()
	{
		Set<String> fin = Main.config.getConfigurationSection("finished worlds").getKeys(false);
		Set<String> unfin = Main.config.getConfigurationSection("unfinished worlds").getKeys(false);
		
		maxNameLength = Main.config.getInt("max name length");
		worldsFinished = populate(fin);
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
	static boolean isFinished(String name){
		int i = 0;
		boolean match = true;
		for (char[] world : worldsFinished)
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
