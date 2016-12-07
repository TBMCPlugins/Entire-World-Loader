package iieLoadSaveEntireWorld;

public class WorldObj {
	
	private static final int dWidth = 44; //default
	private static final int dTotal = dWidth * dWidth;
	
	final int total;
	int[] current;
	int n;
	int c;
	int s;
	int d;
	boolean B;
	
	WorldObj()
	{
		total 		= dTotal;
		current		= new int[] { -1,	-1	};
	}
	WorldObj(int total, int[] center)
	{
		this.total 		= total;
		this.current	= center;
	}
	WorldObj(int total, int[] current, 
			int n, int c, int s, int d, boolean B)
	{
		this.total 		= total;
		this.current	= current;
		this.n = n;
		this.c = c;
		this.s = s;
		this.d = d;
		this.B = B;
	}
		
	static final WorldObj generate(String[] args)
	{
		if (args.length == 0)
		{
			return new WorldObj();
		}
		
		int[] bounds = regionBounds(new ParsedArgs(args));
		
		return new WorldObj
				( 
						(bounds[2] - bounds[0]) * (bounds[2] - bounds[0]),//width ^ 2
						new int[] 
								{ 
										bounds[0] - 1 + (bounds[1]-bounds[0] + 1)/2, 
										bounds[2] - 1 + (bounds[3]-bounds[2] + 1)/2 
										} 
						);
		
		/* 	for even widths, the math above returns the 
		 * 	minimum center not the maximum center. So:
		 * 
		 * 		* * * *
		 * 		* X X *
		 * 		* O X *
		 * 		* * * *
		 * 
		 * 	the spiral pattern used in LoadProcess rotates
		 * 	east north west south, so it must begin at minimum 
		 * 	center.
		 */
	}
	//==============================================================================
	private static final class ParsedArgs
	{
		private static final int dRadius = dWidth/2 * 512 - 512;
		private static final int[] dCenter = new int[]{0,0};
		
		final int radius;
		final int[] center;
		ParsedArgs (String[] args)
		{
			if (isInt(args[0]) && args[0] != "0") 
			{
				radius = Integer.parseInt(args[0]);
			}
			else 
			{
				radius = dRadius;
			}
			if (args.length > 2 && isInt(args[1]) && isInt(args[2]))
			{
				center = new int[]{ Integer.parseInt(args[1]), 
						Integer.parseInt(args[2]) };
			}
			else 
			{
				center = dCenter;
			}
		}
		private static final boolean isInt(String arg)
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
	private static final int[] regionBounds(ParsedArgs a)
	{
		int[] bounds = new int[] 
				{
						Math.floorDiv( a.center[0] - a.radius, 512 ),
						Math.floorDiv( a.center[0] + a.radius, 512 ),
						Math.floorDiv( a.center[1] - a.radius, 512 ),
						Math.floorDiv( a.center[1] + a.radius, 512 )
						};
		
		//--------------------------------------------------------------------------
		//ADD MARGINS:
		//--------------------------------------------------------------------------
		
		final int[] edges = new int[4];
		final int[] radii = new int[4];
		final boolean[] margin = new boolean[4];
		
		//get block edge farthest from center
		edges[0] =  bounds[0]		*512;
		edges[0] = (bounds[1]+1)	*512 - 1;
		edges[0] =	bounds[2]		*512;
		edges[0] = (bounds[3]+1)	*512 - 1;
		
		//get radius from center to far block edge of region
		radii[0] = Math.abs(a.center[0] - edges[0]);
		radii[1] = Math.abs(a.center[0] - edges[1]);
		radii[2] = Math.abs(a.center[1] - edges[2]);
		radii[3] = Math.abs(a.center[1] - edges[3]);
		
		//compare to original block radius: if difference < 64 blocks add a region width
		if (radii[0] - a.radius < 64) { bounds[0] -= 1;	margin[0] = true; }
		if (radii[1] - a.radius < 64) { bounds[1] += 1;	margin[1] = true; }
		if (radii[2] - a.radius < 64) { bounds[2] -= 1;	margin[2] = true; }
		if (radii[3] - a.radius < 64) { bounds[3] += 1;	margin[3] = true; }
		
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
