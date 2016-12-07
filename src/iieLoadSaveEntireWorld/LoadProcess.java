package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadProcess implements Runnable 
{
	//================================VALUES================================
	
	private final World world;
	final int totalRegions;
	int[] currentRegion;
	
	//see setNextRegion()
	int n;			
	int c;			
	int D;			
	int d;			
	boolean B;		
	
	
	//=============================CONSTRUCTORS=============================
	
	LoadProcess(String name) 						//resume from stored
	{
		Bukkit.getLogger().info("resuming stored world-load process");
		
		WorldObj unfinishedworld = ConfigProcess.getUnfinished(name);
		
		world 			= Bukkit.getWorld(name);
		totalRegions	= unfinishedworld.total;
		currentRegion 	= unfinishedworld.current;
		
		n = unfinishedworld.n;
		c = unfinishedworld.c;
		D = unfinishedworld.D;
		d = unfinishedworld.d;
		B = unfinishedworld.B;
	}
	LoadProcess(String name, WorldObj newworld)		//new process
	{
		Bukkit.getLogger().info("new world-load process");
		
		ConfigProcess.addNew(name, newworld);
		
		world 			= Bukkit.getWorld(name);
		totalRegions	= newworld.total;
		currentRegion 	= newworld.current;
		
		n = 1;
		c = 1;
		D = 1;
		d = 0;
		B = false;
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

	
	//===========================SET NEXT REGION============================
	
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
	 * 
	 * 	n		how many regions have been saved already
	 * 	c		direction of travel: E,N,W,S - 1,2,3,4
	 * 	D		distance to travel (side-length)
	 * 	d		distance already traveled
	 * 	B		OK to increase distance?
	 */
	
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
	
	
	//==================================RUN=================================
	
	private volatile boolean ready = true;
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
				world.unloadChunkRequest(chunk[0], chunk[1]);
			}
		}
		if (!setNextRegion())
		{
			TaskManager.finish();
		}
		ready = true;
	}
}
