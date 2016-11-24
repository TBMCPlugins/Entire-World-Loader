package iieLoadSaveEntireWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cache {
	
	private static int maxNameLength;
	private static char[][] listUnfinished;
	private static Map<String,WorldStatus> cacheWorldStatus;

	//PRIVATE METHODS===============================
	private static char[][] populate(Set<String> set)
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
	private static void cache()
	{
		
	}
	
	//PUBLIC METHODS================================
	static void generate()
	{
		maxNameLength = Main.config.getInt("max name length");
		listUnfinished = populate(Main.unfinished.getKeys(false));
		cache();
	}
	static void saveProgress()
	{
		
	}
	static boolean isUnfinished(String name){
		int i = 0;
		boolean match = true;
		for (char[] world : listUnfinished)
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
	static void addUnfinished(int width, int[] center, int[] lowerleft, String name)
	{
		
	}
	
	static final class WorldStatus {
		int width;
		int[] center;
		int[] lowerleft;
		int[] currentRegion;
		
		int n;
		int D;
		int d;
		boolean B;
	}

}
