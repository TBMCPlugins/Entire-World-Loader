package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadProcess implements Runnable 
{
	//=================================INIT=================================
	
	final 	World 	world;
	final 	String 	worldname;
	final 	int 	totalRegions;
			int[] 	currentRegion;

			
	LoadProcess(String name, WorldObject newWorld)
	{
		ConfigProcess.addNew(name, newWorld);
		
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		
		totalRegions	= newWorld.width * newWorld.width;
		currentRegion 	= newWorld.current;
		
		lowerleft 		= newWorld.lowerleft;
		allChunkCoords 	= generateAllChunkCoords(newWorld.width);
	}
	LoadProcess(String name)
	{
		final WorldObject unfinished = ConfigProcess.getUnfinished(name);
		
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		
		totalRegions	= unfinished.width * unfinished.width;
		currentRegion 	= unfinished.current;
		
		lowerleft	 	= unfinished.lowerleft;
		allChunkCoords 	= generateAllChunkCoords(unfinished.width);
		
		n = unfinished.n;
		c = unfinished.c;
		D = unfinished.D;
		d = unfinished.d;
		B = unfinished.B;
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
	
	int n = 1;				//how many regions have been saved already
	int c = 1;				//direction of travel: E,N,W,S - 1,2,3,4
	int D = 1;				//distance to travel
	int d = 0;				//distance already traveled
	boolean B = false;		//OK to increase distance?
	
	private final boolean setNextRegion() 
	{
		n++;
		if (n == totalRegions) return false;
		if (d != D) d++;
		else
		{
			d = 0;		if (B) D++;		
			B = !B;
			c = c == 4 ? 1 : c + 1;
		}
		switch (c)
		{
			case 1 : currentRegion[0]++; break;
			case 2 : currentRegion[1]++; break;
			case 3 : currentRegion[0]--; break;
			case 4 : currentRegion[1]--; break;
		}
		return true;
	}
	
	
	//===============================CHUNK MAP==============================
	
	private final int[] lowerleft;
	private final int[][][][][] allChunkCoords;
	private final int[][][][][] generateAllChunkCoords(int w)
	{
		int[][][][][] allChunkCoords = new int[w][w][32][32][2];

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
		return allChunkCoords;
	}
	private final int[][][] getChunksCurrentRegion(){
		return 
				allChunkCoords
				[  currentRegion[0] - lowerleft[0]  ]
				[  currentRegion[1] - lowerleft[1]  ];
	}
	
	
	//==================================RUN=================================
	private static volatile boolean ready = true;
	public final void run() 
	{
		if (!ready) return;
		else ready = false;
		
		final int[][][] r = getChunksCurrentRegion();
		for (int[][] xRow : r)
		{
			for (int[] chunk : xRow)
			{
				world.loadChunk(chunk[0], chunk[1], true);
				world.unloadChunk(chunk[0], chunk[1]);
			}
		}
		if (!setNextRegion())
		{
			TaskManager.finish();
		}
		ready = true;
	}	
}
