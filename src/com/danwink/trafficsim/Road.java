package com.danwink.trafficsim;

import java.util.ArrayList;

import javax.vecmath.Point2f;

import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public abstract class Road 
{
	ArrayList<RoadConnection> connections = new ArrayList<RoadConnection>();
	Point2f start = new Point2f();
	Point2f end = new Point2f();
	
	float length = -1;
	
	RoadType type = RoadType.STRAIGHT;

	public abstract void render( Graphics2DRenderer g );
	public Point2f getPosition( float f )
	{
		switch( type )
		{
		case STRAIGHT:
			return new Point2f( DMath.lerp( f, start.x, end.x ), DMath.lerp( f, start.y, end.y ) );
		default:
			return null;
		}
	}
	
	public class RoadConnection
	{
		int side;
		float pos;
		Road road;
		
		public RoadConnection( Road road, int side, float pos )
		{
			this.road = road;
			this.side = side;
			this.pos = pos;
		}
	}

	public enum RoadType
	{
		STRAIGHT,
		CIRCLEARC
	}

	public float getLength() 
	{
		if( length < 0 )
		{
			switch( type )
			{
			case STRAIGHT:
				length = start.distance( end );
			}
		}
		return length;
	}
}
