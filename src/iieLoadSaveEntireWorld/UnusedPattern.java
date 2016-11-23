package iieLoadSaveEntireWorld;

import org.bukkit.scheduler.BukkitTask;

public class UnusedPattern {
	
	/*	the contents of this class are static because
	 * 	loading and saving an entire world is an intensive process,
	 * 	and only one save process should be running at a time.
	 * 	
	 * 	so only one save process should be TRACKED at a time.
	 */
	
	//=============================STATIC FIELDS============================
	
	static BukkitTask task;
		
	static int[] startRegion;
	
	static int[] currentRegion;

	static TranslatedCoordinates savedRegions;
	
	static int[][][][][] allChunkCoords;	
		
	static RegionPattern regionPattern;
		
	//INITIALIZE FIELDS
	static void init(int width, int x, int z){
		boolean even = width % 2 == 0;
		int radius = Math.floorDiv(width,2);
		if (even) radius -=1 ;
		int lowX = x-radius;
		int lowZ = z-radius;
		
		startRegion = new int[] {x,z};
		currentRegion = startRegion;
		savedRegions = new TranslatedCoordinates(width,lowX,lowZ);
		generateAllChunkCoords(width,lowX,lowZ);			
		if (even)	regionPattern = new OutwardSpiralPattern();
		else		regionPattern = new CardinalPointsPattern();
	}
	

	
	//===============================PATTERNS===============================
	
	//ABSTRACT PARENT CLASS
	static abstract class RegionPattern {	
		static int n = 1;				//iteration number
		abstract void reset();			//reset fields
		abstract void setNextRegion();	
	}
	
	//EVEN DIAMETER: OUTWARD SPIRAL PATTERN
	private static class OutwardSpiralPattern extends RegionPattern {
		
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
		
		static int c;					//direction of travel: E,N,W,S - 1,2,3,4
		static int D;					//distance to travel
		static int d;					//distance already traveled
		static boolean B;				//OK to change direction?
		
		OutwardSpiralPattern(){
			c = 1;
			D = 1;
			d = 0;
			B = false;
		}
		
		

		//interface methods
		public void reset() {
			
		}
		public void setNextRegion() {
			
		}	
	}
	
	
	//ODD DIAMETER: CARDINAL POINTS PATTERN
	private static class CardinalPointsPattern extends RegionPattern {
	
		/*	The pattern:
		 * 
		 * 		2 | 23  18  10  14  22
		 * 		  |
		 * 		1 | 15  07  02  06  21
		 * 		  |
		 * 		Z | 11  03  01  05  13
		 * 		  |
		 * 	   -1 | 19  08  04  09  17
		 * 		  |
		 * 	   -2 | 24  16  12  20  25
		 * 		  +-------------------
		 * 			-2  -1   X   1   2
		 * 	etc.
		 */
		
		private static int[] cardinalPoints;	//midpoint of each side

		private static int c;					//side: N,W,S,E = 1,2,3,4
		private static int r;					//radius from square center
		
		private static int d;					//distance from cardinal point
		private static boolean B;				//direction from cardinal point
		
		private static void expR(){ 			//expand radius, cardinal points
			r++;
			cardinalPoints[0]++;	
			cardinalPoints[1]--;
			cardinalPoints[2]--;
			cardinalPoints[4]++;
		}
		
		CardinalPointsPattern(){
			reset();
		}
		
		//interface methods
		void reset(){
			cardinalPoints = new int[] {		//each cardinal point contains 
					startRegion[1]+1,			//only the dimension that moves
					startRegion[0]-1,
					startRegion[1]-1,
					startRegion[0]+1
					};
			n = 1;
			c = 1;
			r = 1;
			d = 0;
			B = false;
		}		
		void setNextRegion(){
			n++;
			switch (c){
				case 1 : 
					if (B) 	currentRegion = new int[] {startRegion[0] + d, cardinalPoints[0]};
					else;	currentRegion = new int[] {startRegion[0] - d, cardinalPoints[0]};
				case 2 : 
					if (B) 	currentRegion = new int[] {cardinalPoints[1], startRegion[0] + d};
					else;	currentRegion = new int[] {cardinalPoints[1], startRegion[0] - d};
				case 3 : 
					if (B) 	currentRegion = new int[] {startRegion[0] - d, cardinalPoints[2]};
					else;	currentRegion = new int[] {startRegion[0] + d, cardinalPoints[2]};
				case 4 : 
					if (B) 	currentRegion = new int[] {cardinalPoints[3], startRegion[0] - d};
					else;	currentRegion = new int[] {cardinalPoints[3], startRegion[0] + d};
					
					if (r == d)	{ expR();	d = 0;		c = 1;				}
					else		{			d++;		c++;		B = !B;	}
				
			}
		}
	}
	
	
	
	//=================================UTIL=================================
	
	//CUSTOM MAP CLASS FOR TRACKING SAVED REGIONS
	static class TranslatedCoordinates {
		
		int xAdjust;
		int zAdjust;
		boolean[][] savemap;
		public TranslatedCoordinates(int w, int lowX, int lowZ)
		{
			xAdjust = 0 - lowX;
			zAdjust = 0 - lowZ;
			savemap = new boolean[w][w];
		}
		int x(int x){  return x + xAdjust;  }
		int z(int z){  return z + zAdjust;  }
		
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
	}
	
	//GENERATE ALL CHUNK COORDINATES
	private static void generateAllChunkCoords(int d, int lowX, int lowZ) {
		allChunkCoords = new int[d][d][32][32][2];
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
}
