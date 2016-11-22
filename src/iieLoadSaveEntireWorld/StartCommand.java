package iieLoadSaveEntireWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {
	private Main plugin;
	StartCommand(Main Plugin){
		plugin = Plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command label, String command, String[] args) {
		if (LoadSaveProcess.inProgress)
		{
			sender.sendMessage("a process is already running. /StopLoadSave to stop.");
			return false;
		}
		else 
			LoadSaveProcess.inProgress = true;
		Dimensions dimensions = new Dimensions(args);
		Main.task = Bukkit.getScheduler().runTaskTimer
				(
						plugin,
						new LoadSaveProcess(
								dimensions.width,dimensions.center,dimensions.lowCorner
								),
						0, 100
				);
		return false;
	}
	
	private static final int[][] defaultDimensions = 
			new int[][] {	{22528,0,0}	,	{44,-1,-1,-22,-22}	};
						
	private static class Dimensions{		
		int width;
		int[] center;
		int[] lowCorner;
		
		Dimensions(String[] args){
			int length = args.length;
			if (length == 0)
			{
				width = 				  defaultDimensions[1][0];
				center = 	new int[] 	{ defaultDimensions[1][1], defaultDimensions[1][2] };
				lowCorner = new int[] 	{ defaultDimensions[1][3], defaultDimensions[1][4] };
				return;
			}
			int blockRadius = blockRadius(args[0]);
			int[] blockCenter = length > 2 ? 
					blockCenter(args[1], args[2]) : 
						new int[]
							{
								defaultDimensions[0][1],
								defaultDimensions[0][2]
							};					
			int[] blockBounds = blockBounds(blockCenter,blockRadius);
			int[] regionBounds = regionBoundsAddMargins(
					regionBounds(blockBounds),
					blockCenter,
					blockRadius
					);
			int[] regionCenter = regionCenter(regionBounds);
			//TODO
		}
	}
	
	//==============================================================================================
	//this shit is only used when someone passes args to the command
	//we're going to use the default dimensions so hardly any of this
	//will get called
	
	
	private static boolean isInteger(String arg){    
	    int length = arg.length();
	    int i = 0;
	    if (arg.charAt(0) == '-')
	        if (length == 1) return false;
	        else i = 1;
	    for (; i < length; i++) {
	        char c = arg.charAt(i);
	        if (c < '0' || c > '9')
	        	return false;
	    }
	    return true;
	}
	
	
	private static int blockRadius(String arg0)
	{
		int blockWidth = isInteger(arg0) ? 
				Integer.parseInt(arg0) : defaultDimensions[0][0];
		return blockWidth/2;
	}
	private static int[] blockCenter(String arg1, String arg2)
	{
		int xBlock = isInteger(arg1) ? Integer.parseInt(arg1) : 0;
		int zBlock = isInteger(arg2) ? Integer.parseInt(arg2) : 0;
		return new int[] {xBlock,zBlock};
	}
	private static int[] blockBounds(int[] center, int blockRadius)
	{
		int xMinBlock = center[0] - blockRadius;
		int xMaxBlock = center[0] + blockRadius;
		int zMinBlock = center[1] - blockRadius;
		int zMaxBlock = center[1] + blockRadius;
		return new int[] {xMinBlock,xMaxBlock,zMinBlock,zMaxBlock};
	}
	private static int[] regionBounds(int[] blockBounds)
	{
		int xMinRegion = Math.floorDiv(blockBounds[0],512);
		int xMaxRegion = Math.floorDiv(blockBounds[1],512);
		int zMinRegion = Math.floorDiv(blockBounds[2],512);
		int zMaxRegion = Math.floorDiv(blockBounds[3],512);
		return new int[] {xMinRegion,xMaxRegion,zMinRegion,zMaxRegion};
	}
	private static int[] regionBoundsAddMargins(int[] regionBounds, int[] blockCenter, int blockRadius)
	{
		int[] regionBlockRadii = new int[4];
		boolean[] marginAdded = new boolean[4];
		
		//get block edge farthest from center
		int xMinRegionBlockEdge =  regionBounds[0]		*512;
		int xMaxRegionBlockEdge = (regionBounds[1]+1)	*512 - 1;
		int zMinRegionBlockEdge =  regionBounds[2]		*512;
		int zMaxRegionBlockEdge = (regionBounds[3]+1)	*512 - 1;
		
		//get edge's block distance from center
		regionBlockRadii[0] = Math.abs(blockCenter[0] - xMinRegionBlockEdge);
		regionBlockRadii[1] = Math.abs(blockCenter[0] - xMaxRegionBlockEdge);
		regionBlockRadii[2] = Math.abs(blockCenter[1] - zMinRegionBlockEdge);
		regionBlockRadii[3] = Math.abs(blockCenter[1] - zMaxRegionBlockEdge);
		
		//compare to original block radius, if difference is < 4 chunks add a region width
		if (regionBlockRadii[0] - blockRadius < 64) { regionBounds[0] -= 1;	marginAdded[0] = true; }
		if (regionBlockRadii[1] - blockRadius < 64) { regionBounds[1] += 1;	marginAdded[1] = true; }
		if (regionBlockRadii[2] - blockRadius < 64) { regionBounds[2] -= 1;	marginAdded[2] = true; }
		if (regionBlockRadii[3] - blockRadius < 64) { regionBounds[3] += 1;	marginAdded[3] = true; }
		
		//resquare the selection
		regionBounds = regionBoundsResquare(regionBounds,marginAdded,regionBlockRadii);
		return regionBounds;
	}
	private static int[] regionCenter(int[] regionBounds){
		int regionCenterX = regionBounds[0] - 1 + (regionBounds[1]-regionBounds[0] + 1)/2;
		int regionCenterZ = regionBounds[2] - 1 + (regionBounds[3]-regionBounds[2] + 1)/2;
		return new int[] {regionCenterX, regionCenterZ};
	}
	private static int[] regionBoundsResquare(int[] regionBounds, boolean[]marAdd, int[]radii){
		if (!marAdd[0])
			if (!marAdd[1]) 
				if (!marAdd[2])
					if (!marAdd[3])//0000
						return regionBounds;
					else//0001
					{
						int min = Math.min(radii[0], radii[1]);
						if (min == radii[0])	
							regionBounds[0]++;
						else 
							regionBounds[1]++;
					}
				else
					if (!marAdd[3])//0010
					{
						int min = Math.min(radii[0], radii[1]);
						if (min == radii[0])	
							regionBounds[0]++;
						else 
							regionBounds[1]++;
					}
					else//0011
					{
						regionBounds[0]++;
						regionBounds[1]++;
					}
			else
				if (!marAdd[2])
					if (!marAdd[3])//0100
					{
						int min = Math.min(radii[2], radii[3]);
						if (min == radii[2])	
							regionBounds[2]++;
						else 
							regionBounds[3]++;
					}
					else//0101
						return regionBounds;
				else
					if (!marAdd[3])//0110
						return regionBounds;
					else//0111
						regionBounds[0]++;
		else
			if (marAdd[1])
				if (marAdd[2])
					if (marAdd[3])//1111
						return regionBounds;
					else//1110
						regionBounds[3]++;
				else
					if (marAdd[3])//1101
						regionBounds[2]++;
					else//1100
					{
						regionBounds[2]++;
						regionBounds[3]++;
					}	
			else
				if (marAdd[2])
					if (marAdd[3])//1011
						regionBounds[1]++;
					else //1010
						return regionBounds;
				else //100
					if (marAdd[3])//1001
						return regionBounds;
					else {//1000
						int min = Math.min(radii[2], radii[3]);
						if (min == radii[2])	
							regionBounds[0]++;
						else 
							regionBounds[1]++;
					};
		return regionBounds;
	}
}
