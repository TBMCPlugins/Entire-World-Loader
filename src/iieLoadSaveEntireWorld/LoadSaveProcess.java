package iieLoadSaveEntireWorld;

import org.bukkit.scheduler.BukkitTask;

public class LoadSaveProcess implements Runnable {
	
	
	//=============================STATIC FIELDS============================
	
	static boolean inProgress = false;
	
	private static int[] startRegion;
	
	private static int[] currentRegion;

	private static TranslatedCoordinates map;
	
	private static int[][][][][] allChunkCoords;	
	
	public LoadSaveProcess(int width, int[] center, int[] lowCorner){
		currentRegion = startRegion = new int[] {center[0],center[1]};
		map = new TranslatedCoordinates(width,lowCorner[0],lowCorner[1]);
		generateAllChunkCoords(width,lowCorner[0],lowCorner[1]);
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
		
		private static int n = 1;				//number
		private static int c = 1;				//direction of travel: E,N,W,S - 1,2,3,4
		private static int D = 1;				//distance to travel
		private static int d = 0;				//distance already traveled
		private static boolean B = false;		//OK to change direction?
		static void reset() 
		{
			c = 1;
			D = 1;
			d = 0;
			B = false;
		}
		static class Loc{//used when pausing the process
			int c; int D; int d; boolean B;
			Loc(){
				this.c = SavePattern.c;
				this.D = SavePattern.D;
				this.d = SavePattern.d;
				this.B = SavePattern.B;
			}
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
	}
	
	
	
	//=================================UTIL=================================
	
	//TRACKER, COORDINATE TRANSLATOR
	private static class TranslatedCoordinates {
		
		int xAdjust;
		int zAdjust;
		//boolean[][] savemap;
		public TranslatedCoordinates(int w, int lowX, int lowZ)
		{
			xAdjust = 0 - lowX;
			zAdjust = 0 - lowZ;
			//savemap = new boolean[w][w];
		}
		int x(int x){  return x + xAdjust;  }
		int z(int z){  return z + zAdjust;  }
		/*
		void save(int x, int z)			{  savemap[x(x)][z(z)] = true;  }
		boolean isSaved(int x, int z)	{  return savemap[x(x)][z(z)];  }
		
		boolean allSaved(){
			for (boolean[] xRow : savemap){
				for (boolean region : xRow){
					if (!region) return false;
				}
			}
			return true;
		}
		*/
	}
	
	//GENERATE ALL CHUNK COORDINATES
	private static void generateAllChunkCoords(int width, int lowX, int lowZ) {
		allChunkCoords = new int[width][width][32][32][2];
		int regionX = lowX;
		boolean negX = true;
		for (int[][][][] xRowRegions : allChunkCoords){
			int regionZ = lowZ;
			boolean negZ = true;
			for (int[][][] region : xRowRegions){
				int chunkX = 0;
				for (int[][] xRowChunks : region){
					int chunkZ = 0;
					for (int[] chunk : xRowChunks){
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
	
	
	//==================================RUN=================================
	public void run() {
		
	}
}
