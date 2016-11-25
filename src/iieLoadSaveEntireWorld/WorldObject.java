package iieLoadSaveEntireWorld;

public class WorldObject {
	
	final int 	width;
	final int[] lowerleft;
	
	int[] 	current	= new int[] { -1,	-1	};		
	int 	n 		= 1;
	int 	c 		= 1;
	int 	D 		= 1;
	int 	d 		= 0;
	boolean B 		= false;
	
	WorldObject()
	{
		width 		= 44;
		lowerleft 	= new int[] { -22,	-22	};
	}
	WorldObject(int width)
	{
		this.width 	= width;
		lowerleft 	= new int[] { -22,	-22	};
	}
	WorldObject(int width, int[] lowerleft, int[] center)
	{
		this.width 		= width;
		this.lowerleft 	= lowerleft;
		this.current	= center;
	}
	WorldObject(int width, int[] lowerleft, 
			int[] current, int n, int c, int D, int d, boolean B)
	{
		this.width 		= width;
		this.lowerleft 	= lowerleft;
		this.current	= current;
		this.n = n;
		this.c = c;
		this.D = D;
		this.D = d;
		this.B = B;
	}
}
