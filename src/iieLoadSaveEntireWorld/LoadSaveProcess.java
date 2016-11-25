package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class LoadSaveProcess implements Runnable 
{
	
	//=================================INIT=================================
	static 	LoadSaveProcess process;
	static 	BukkitTask 		bukkitTask;	
	static 	boolean 		inProgress 	= false;
			boolean 		ready 		= true;
	
	final 	Map 			map;
	final 	World 			world;
	final 	String 			worldname;
	final 	int 			totalRegions;
			int[] 			currentRegion;

	LoadSaveProcess(String name, int width, int[] lowerleft, int[] center)
	{
		map 			= new Map(width, lowerleft);
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		totalRegions	= width*width;
		currentRegion 	= center;
	}
	LoadSaveProcess(
			String name, int width, int[] lowerleft, 
			int[] current, int n, int c, int D, int d, boolean B)
	{
		map 			= new Map(width, lowerleft);
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		totalRegions	= width*width;
		currentRegion 	= current;
		this.n = n;
		this.c = c;
		this.D = D;
		this.d = d;
		this.B = B;
	}
	
	
	//===============================CONTROLS===============================
	static void start(String name,WorldObject d)
	{
		process = new LoadSaveProcess	(name, d.width, d.lowerleft, d.current);
		ConfigManager.addNew			(name, d.width, d.lowerleft, d.current);
	}
	static void resume(String name)
	{
		
	}
	static void stop()
	{		
		//TODO
	}
	static void finish()
	{
		bukkitTask.cancel();
		process = null;
		System.gc();
		inProgress = false;
	}

	
	//===============================PATTERN================================
	
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
	
	int n = 1;				//number
	int c = 1;				//direction of travel: E,N,W,S - 1,2,3,4
	int D = 1;				//distance to travel
	int d = 0;				//distance already traveled
	boolean B = false;		//OK to change direction?
	
	private void setNextRegion() 
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
	private boolean complete(){
		return n == process.totalRegions;
	}
	
	
	//===============================CHUNK MAP==============================
	
	private static final class Map 
	{
		private int[] lowerleft;
		private int[][][][][] allChunkCoords;
		Map(int w,int[] lowerleft)
		{
			this.lowerleft = lowerleft;
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
		int[][][] getChunksCurrentRegion(){
			return 
					allChunkCoords
					[  process.currentRegion[0] - lowerleft[0]  ]
					[  process.currentRegion[1] - lowerleft[1]  ];
		}
	}	
	
	
	//==================================RUN=================================
	public void run() 
	{
		if (!ready) return;
		else ready = false;
		
		int[][][] r = map.getChunksCurrentRegion();
		for (int[][] xRow : r)
		{
			for (int[] chunk : xRow)
			{
				world.loadChunk(chunk[0], chunk[1], true);
				world.unloadChunk(chunk[0], chunk[1]);
			}
		}
		setNextRegion();
		
		if (complete()) finish();
		else ready = true;
	}	
}
