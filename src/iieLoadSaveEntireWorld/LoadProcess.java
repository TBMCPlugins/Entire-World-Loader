package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadProcess implements Runnable 
{
	//=================================INIT=================================
	
	final 	World 			world;
	final 	String 			worldname;
	final 	int 			totalRegions;
			int[] 			currentRegion;

			
	LoadProcess(String name, WorldObject newWorld)
	{
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		
		totalRegions	= newWorld.width * newWorld.width;
		currentRegion 	= newWorld.current;
		
		this.lowerleft 	= newWorld.lowerleft;
		allChunkCoords 	= generateAllChunkCoords(newWorld.width, newWorld.lowerleft);
	}
	LoadProcess(String name)
	{
		final WorldObject unfinished = ConfigProcess.getUnfinished(name);
		
		world 			= Bukkit.getWorld(name);
		worldname 		= name;
		
		totalRegions	= unfinished.width * unfinished.width;
		currentRegion 	= unfinished.current;
		
		this.lowerleft 	= unfinished.lowerleft;
		allChunkCoords 	= generateAllChunkCoords(unfinished.width, unfinished.lowerleft);
		
		this.n 	= unfinished.n;
		this.c 	= unfinished.c;
		this.D 	= unfinished.D;
		this.d 	= unfinished.d;
		this.B 	= unfinished.B;
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
	
	private final void setNextRegion() 
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
	final boolean isFinished(){
		return n == totalRegions;
	}
	
	
	//===============================CHUNK MAP==============================
	
	private final int[] lowerleft;
	private final int[][][][][] allChunkCoords;
	private final int[][][][][] generateAllChunkCoords(int w,int[] lowerleft)
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
	
	boolean ready = true;
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
		setNextRegion();
		
		if (isFinished()) TaskManager.finish();
		else ready = true;
	}	
}
