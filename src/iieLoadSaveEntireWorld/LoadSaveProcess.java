package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadSaveProcess implements Runnable {
	
	
	//=============================STATIC FIELDS============================
		
	static boolean inProgress = false;
	static boolean taskRunning = false;
	
	private static World world;
	private static String worldName;
		
	private static int[] currentRegion;
	private static int totalRegions;

	private static int untilSaveProg;
	
	//===============================CONTROLS===============================
	static void start(int width, int[] center, int[] lowerleft, String name)
	{
		Map.init(width, lowerleft);
		world 			= Bukkit.getWorld(worldName);
		worldName 		= name;
		currentRegion 	= center;
		totalRegions	= width*width;
		untilSaveProg	= 9;
	}
	public static void resume(String name)
	{
		String path = "unfinishedWorlds." + name + ".";
		
		int width = Main.config.getInt(path + "width");
		int[] center = new int[]
				{
						Main.config.getInt(path + "center.x"),
						Main.config.getInt(path + "center.z")
				};
		int[] lowerleft = new int[]
				{
						Main.config.getInt(path + "lowerleft.x"),
						Main.config.getInt(path + "lowerleft.z")
				};
		currentRegion = new int[]
				{
						Main.config.getInt(path + "currentRegion.x"),
						Main.config.getInt(path + "currentRegion.z")
				};
		totalRegions = Main.config.getInt(path + "width");
		Map.init(w, lowerleft);
		
		SavePattern.n = Main.config.getInt(path + "n");
		SavePattern.D = Main.config.getInt(path + "D");
		SavePattern.d = Main.config.getInt(path + "d");
		SavePattern.B = Main.config.getBoolean(path + "B");
	}
	public static void saveProgress()
	{
		String path = "unfinishedWorlds." + worldName + ".";
		Main.config.set(path + "currentRegion.x", currentRegion[0]);
		Main.config.set(path + "currentRegion.z", currentRegion[1]);
		Main.config.set(path + "n", SavePattern.n);
		Main.config.set(path + "D", SavePattern.D);
		Main.config.set(path + "d", SavePattern.d);
		Main.config.set(path + "B", SavePattern.B);
		Main.plugin.saveConfig();
	}
	public static void stop()
	{
		saveProgress();
		SavePattern.reset();
		
	}

	
	//===============================PATTERN================================
	
	private static class SavePattern {
		
		/*	The pattern:
		 * 
		 * 		3 | 36  35  34  33  32  31
		 * 		  |
		 * 		2 | 17  16  15  14  13  30
		 * 		  |
		 * 		1 | 18  05  04  03  12  29
		 * 		  |
		 * 		Z | 19  06  01  02  11  28
		 * 		  |
		 * 	   -1 | 20  07  08  09  10  27
		 * 		  |
		 * 	   -2 | 21  22  23  24  25  26
		 * 		  +-----------------------
		 * 			-2  -1   X   1   2   3
		 * 	etc.
		 */
		
		static int n = 1;				//number
		static int c = 1;				//direction of travel: E,N,W,S - 1,2,3,4
		static int D = 1;				//distance to travel
		static int d = 0;				//distance already traveled
		static boolean B = false;		//OK to change direction?
		static void reset() 
		{
			c = 1;
			D = 1;
			d = 0;
			B = false;
		}
		static void setNextRegion() 
		{
			n++;
			if (d != D) d++;
			else
			{
				d = 0;	
				D++;
				switch (c){
					case 1 : currentRegion[0]++;
					case 2 : currentRegion[1]++;
					case 3 : currentRegion[0]--;
					case 4 : 
						currentRegion[1]--;
						c = B ? 1 : c + 1;
				}
				B = !B;
			}
		}
		static boolean complete(){
			return n == totalRegions;
		}
	}
	
	
	//===============================CHUNK MAP==============================
	
	private static class Map 
	{
		private static int[] 		 lowerleft;
		private static int[][][][][] allChunkCoords;
		static void init(int w,int[] lowerleft)
		{
			Map.lowerleft = lowerleft;
			allChunkCoords = new int[w][w][32][32][2];

			int regionX = lowerleft[0];
			int regionZ = lowerleft[1];
			boolean negX = true;
			boolean negZ = true;
			int chunkX = 0;
			int chunkZ = 0;
			for (int[][][][] xRowRegions : allChunkCoords)
			{
				regionZ = lowerleft[1];
				negZ = true;
				for (int[][][] region : xRowRegions)
				{
					chunkX = 0;
					for (int[][] xRowChunks : region)
					{
						chunkZ = 0;
						for (int[] chunk : xRowChunks)
						{
							chunk[0] = (regionX * 32) + (negX ? 0 - chunkX : chunkX);
							chunk[1] = (regionZ * 32) + (negZ ? 0 - chunkZ : chunkZ);	
							chunkZ++;
						}
						chunkX++;
					}
					regionZ++;
					if (negZ)
						negZ = regionZ < 0;
				}
				regionX++;
				if (negX) 
					negX = regionX < 0;
			}
		}
		static int[][][] getChunksCurrentRegion(){
			return 
					allChunkCoords
					[  currentRegion[0] - lowerleft[0]  ]
					[  currentRegion[1] - lowerleft[1]  ];
		}
	}	
	
	
	//==================================RUN=================================
	public void run() 
	{
		if (taskRunning) return;
		else taskRunning = true;
		
		int[][][] r = Map.getChunksCurrentRegion();
		for (int[][] xRow : r)
		{
			for (int[] chunk : xRow)
			{
				world.loadChunk(chunk[0], chunk[1], true);
				world.unloadChunk(chunk[0], chunk[1]);
			}
		}
		SavePattern.setNextRegion();
		if (SavePattern.complete())
		{
			
		}
		if (untilSaveProg == 0)
		{
			untilSaveProg = 9;
			saveProgress();
		}else 
			untilSaveProg--;
		
	}
	
}
