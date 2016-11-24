package iieLoadSaveEntireWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cache {
	
	private static int maxNameLength;
	private static char[][] listUnfinished;
	private static Map<String,WorldStatus> cacheUnfinished;
	
	static final class WorldStatus {
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
				int[] current, int n, int c, int D, int d, boolean B
				)
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
		int width;
		int[] lowerleft;
		int[] current;
		int n;	int c;	int D;	int d; boolean B;
		
		for (String name : set){
			path 		= "unfinishedWorlds." + name + ".";
			
			width 		= Main.config.getInt(path + "width");
			lowerleft 	= new int[]{
						  Main.config.getInt(path + "lowerleft.x"),
						  Main.config.getInt(path + "lowerleft.z")
						  };
			current 	= new int[]{
						  Main.config.getInt(path + "currentRegion.x"),
						  Main.config.getInt(path + "currentRegion.z")
						  };
			n 			= Main.config.getInt(path + "n");
			c			= Main.config.getInt(path + "c");
			D 			= Main.config.getInt(path + "D");
			d 			= Main.config.getInt(path + "d");
			B 			= Main.config.getBoolean(path + "B");
			cacheUnfinished.put(name, new WorldStatus( width,lowerleft,current,n,c,D,d,B ));
		}
	}
	
	//================================PUBLIC METHODS================================
	static void generate()
	{
		maxNameLength = Main.config.getInt("max name length");
		Set<String> set = Main.unfinished.getKeys(false);
		populateList(set);
		populateCache(set);
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
}
