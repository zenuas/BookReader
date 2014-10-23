package org.zenu.bookreader;

public enum Direction
{
	LeftToRight(0),
	RightToLeft(1),
	;
	
	private final int id_;
	
	private Direction(final int id)
	{
		id_ = id;
	}
	
	public int getId()
	{
		return(id_);
	}
	
	public static Direction valueOf(int x)
	{
		return(LeftToRight.getId() == x ? LeftToRight : RightToLeft);
		/*
		for(Direction v : Direction.values())
		{
			if(v.getId() == x) {return(v);}
		}
		return(RightToLeft);
		*/
	}
}
