package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadProcess implements Runnable 
{
	//=================================INIT=================================
	
	private final World world;
	final int totalRegions;
	int[] currentRegion;
	
	LoadProcess(String name, WorldObj newWorld)
	{
		ConfigProcess.addNew(name, newWorld);
		
		world 			= Bukkit.getWorld(name);
		totalRegions	= newWorld.total;
		currentRegion 	= newWorld.current;
	}
	
	LoadProcess(String name)
	{
		final WorldObj unfinished = ConfigProcess.getUnfinished(name);
		
		world 			= Bukkit.getWorld(name);
		totalRegions	= unfinished.total;
		currentRegion 	= unfinished.current;
		
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
		if (n == totalRegions) return false;
		if (d == D)
		{
			d = 1;		
			if (B) D++;		
			B = !B;
			c = c == 4 ? 1 : c + 1;
		}
		else d++;
		switch (c)
		{
			case 1 : currentRegion[0]++; break;
			case 2 : currentRegion[1]++; break;
			case 3 : currentRegion[0]--; break;
			case 4 : currentRegion[1]--; break;
		}
		n++;
		return true;
	}
	
	
	//==============================GET CHUNKS==============================
	
	private final int[][][] getChunksCurrentRegion()
	{
		final int[][][] chunks = new int[32][32][2];
		int xR = currentRegion[0] * 32;
		int zR = currentRegion[1] * 32;
		int z;
		for (int x = 0; x < 32; x++)
		{
			z = 0;
			for (; z < 32; z++)
			{
				chunks[x][z][0] = xR + x;
				chunks[x][z][1] = zR + z;	
			}
		}
		return chunks;
	}
	
	
	//==================================RUN=================================
	
	private static volatile boolean ready = true;
	private 	   volatile int unqueued = 0;
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
				if (!world.unloadChunkRequest(chunk[0], chunk[1])) unqueued++;
			}
		}
		if (!setNextRegion())
		{
			TaskManager.finish();
		}
		ready = true;
	}	
}
