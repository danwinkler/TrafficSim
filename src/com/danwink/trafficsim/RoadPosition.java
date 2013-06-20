package com.danwink.trafficsim;

import javax.vecmath.Point2f;

public class RoadPosition 
{
	Road r;
	float pos;
	
	Point2f coords;
	
	public RoadPosition( Road r, float pos )
	{
		this.r = r;
		this.pos = pos;
	}
	
	public RoadPosition( float x, float y )
	{
		coords = new Point2f( x, y );
	}
	
	public Point2f getCoords()
	{
		if( coords == null )
		{
			coords = new Point2f( r.start.x + r.getVector().x * pos, r.start.y + r.getVector().y* pos );
		}
		return coords;
	}
}
