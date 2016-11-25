package iieLoadSaveEntireWorld;

public class Dimensions 
{
	private static final int[] 	defaultDimensions 	= new int[]{44,-1,-1,-22,-22};	
	
	static WorldObject generate(String[] args)
	{
		if (args.length == 0)
		{ 
			return new WorldObject();
		}
		ParsedArgs parsedArgs = new ParsedArgs(args);
		
		int[] bounds = regionBounds(parsedArgs.radius, parsedArgs.center);
		
		int   width 	= bounds[2] - bounds[0];
		
		int[] lowerleft = new int[]{	bounds[0], bounds[2] 	};
		
		int[] center 	= new int[]{	bounds[0] - 1 + (bounds[1]-bounds[0] + 1)/2, 
										bounds[2] - 1 + (bounds[3]-bounds[2] + 1)/2 
										};
		
		return new WorldObject( width, lowerleft, center );
	}
	//==============================================================================
	private static final class ParsedArgs
	{
		private static final int 	defaultRadius 	= 11264;
		private static final int[] 	defaultCenter	= new int[]{0,0};
		int radius;
		int[] center;
		ParsedArgs (String[] args)
		{
			if (isInt(args[0]) && args[0] != "0") 
				radius = Integer.parseInt(args[0]);
			else radius = defaultRadius;
			
			if (args.length > 2 && isInt(args[1]) && isInt(args[2]))
				center = new int[]{ Integer.parseInt(args[1]), Integer.parseInt(args[2]) };
			else center = defaultCenter;
		}
		private static boolean isInt(String arg)
		{    
		    int length = arg.length();
		    int i = 0;
		    if (arg.charAt(0) == '-')
		    {
		        if (length == 1) return false;
		        else i = 1;
		    }
		    for (; i < length; i++) 
		    {
		        char c = arg.charAt(i);
		        if (c < '0' || c > '9')
		        	return false;
		    }
		    return true;
		}
	}
	//==============================================================================
	private static int[] regionBounds(int radius, int[] center)
	{
		int[] bounds = new int[] 
				{
				//      [ get region ] [   get block    ]
						Math.floorDiv( center[0] - radius, 512 ),
						Math.floorDiv( center[0] + radius, 512 ),
						Math.floorDiv( center[1] - radius, 512 ),
						Math.floorDiv( center[1] + radius, 512 )
				};
		
		//add margins------------
		
		int[] edges = new int[4];
		int[] radii = new int[4];		
		boolean[] margin = new boolean[4];
		
		//get block edge farthest from center
		edges[0] =  bounds[0]		*512;
		edges[0] = (bounds[1]+1)	*512 - 1;
		edges[0] =	bounds[2]		*512;
		edges[0] = (bounds[3]+1)	*512 - 1;
		
		//get radius from center to far block edge of region
		radii[0] = Math.abs(center[0] - edges[0]);
		radii[1] = Math.abs(center[0] - edges[1]);
		radii[2] = Math.abs(center[1] - edges[2]);
		radii[3] = Math.abs(center[1] - edges[3]);
		
		//compare to original block radius, if difference is < 4 chunks add a region width
		if (radii[0] - radius < 64) { bounds[0] -= 1;	margin[0] = true; }
		if (radii[1] - radius < 64) { bounds[1] += 1;	margin[1] = true; }
		if (radii[2] - radius < 64) { bounds[2] -= 1;	margin[2] = true; }
		if (radii[3] - radius < 64) { bounds[3] += 1;	margin[3] = true; }
		
		//resquare the selection
		if (!margin[0])
			if (!margin[1]) 
				if (!margin[2])
					if (!margin[3])//-----------0000
						return bounds;
					else//----------------------0001
						if (radii[0] < radii[1])	
							bounds[0]++;
						else 
							bounds[1]++;
				else
					if (!margin[3])//-----------0010
						if (radii[0] < radii[1])	
							bounds[0]++;
						else 
							bounds[1]++;
					else//----------------------0011
					{
						bounds[0]++;
						bounds[1]++;
					}
			else
				if (!margin[2])
					if (!margin[3])//-----------0100
						if (radii[2] < radii[3])	
							bounds[2]++;
						else 
							bounds[3]++;
					else//----------------------0101
						return bounds;
				else
					if (!margin[3])//-----------0110
						return bounds;
					else//----------------------0111
						bounds[0]++;
		else
			if (margin[1])
				if (margin[2])
					if (margin[3])//------------1111
						return bounds;
					else//----------------------1110
						bounds[3]++;
				else
					if (margin[3])//------------1101
						bounds[2]++;
					else//----------------------1100
					{
						bounds[2]++;
						bounds[3]++;
					}	
			else
				if (margin[2])
					if (margin[3])//------------1011
						bounds[1]++;
					else//----------------------1010
						return bounds;
				else
					if (margin[3])//------------1001
						return bounds;
					else//----------------------1000
						if (radii[2] == radii[3])	
							bounds[0]++;
						else 
							bounds[1]++;
		
		return bounds;
	}
}
