package iieLoadSaveEntireWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheOld 
{
	private static int maxNameLength;
	private static char[][] listUnfinished;
	private static Map<String,WorldStatus> cacheUnfinished;
	static void generate()
	{
		maxNameLength = Main.config.getInt("max name length");
		Set<String> set = Main.unfinished.getKeys(false);
		populateList(set);
		populateCache(set);
	}
	static void addWorld()
	{
		
	}
	static void saveProgress()
	{
		
	}
	static void finish(String name)
	{
		//TODO
	}
	static void addUnfinished(int width, int[] center, int[] lowerleft, String name)
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
	private static void populateList(Set<String> set)
	{
		char[][] listUnfinished = new char[set.size()][maxNameLength];
		int i = 0;
		int ii = 0;
		for (String name : set)
		{
			for (char c : name.toCharArray())
			{
				listUnfinished[i][ii] = c;
				ii++;
			}
			i++;
			ii = 0;
		}
	}
	private static void populateCache(Set<String> set)
	{
		String path;
		for (String name : set){
			path = "unfinishedWorlds." + name + ".";
			cacheUnfinished.put(
					name, 
					new WorldStatus( 
							Main.config.getInt(path + "width"),
							new int[]{
									Main.config.getInt(path + "lowerleft.x"),
									Main.config.getInt(path + "lowerleft.z")},
							new int[]{
									Main.config.getInt(path + "currentRegion.x"),
									Main.config.getInt(path + "currentRegion.z")},
							Main.config.getInt(path + "n"),
							Main.config.getInt(path + "c"),
							Main.config.getInt(path + "D"),
							Main.config.getInt(path + "d"),
							Main.config.getBoolean(path + "B")));
		}
	}	
	static final class WorldStatus 
	{
		final int width;
		final int[] lowerleft;
		int[] currentRegion;
		int n = 1;
		int c = 1;
		int D = 1;
		int d = 0;
		boolean B = false;
		WorldStatus(int width, int[] lowerleft, int[]center)
		{
			this.width = width;
			this.lowerleft = lowerleft;
			currentRegion = center;
		}
		WorldStatus(
				int width, int[] lowerleft,
				int[] current, int n, int c, int D, int d, boolean B)
		{
			this.width = width;
			this.lowerleft = lowerleft;
			currentRegion = current;
			this.n = n;
			this.c = c;
			this.D = D;
			this.d = d;
			this.B = B;
		}
	}
}
