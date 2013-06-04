package com.danwink.trafficsim;

import java.util.ArrayList;
import java.util.Comparator;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public abstract class Road 
{
	static Comparator<RoadConnection> roadConnectionComparator = new Comparator<RoadConnection>() {
		public int compare( RoadConnection a, RoadConnection b )
		{
			return a.pos > b.pos ? 1 : -1;
		}
	};
	
	ArrayList<RoadConnection> connections = new ArrayList<RoadConnection>();
	Point2f start = new Point2f();
	Point2f end = new Point2f();
	
	Vector2f vec;
	Vector2f normalizedVec;
	
	float length = -1;
	
	float width;
	
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
	
	public Vector2f getVector()
	{
		if( vec == null )
		{
			vec = new Vector2f( end );
			vec.sub(  start  );
		}
		return vec;
	}
	
	public Vector2f getNormalizedVector()
	{
		if( normalizedVec == null )
		{
			normalizedVec = new Vector2f( getVector() );
			normalizedVec.normalize();
		}
		return normalizedVec;
	}
	
	public Point2f getOffset( float amt )
	{
		Point2f pos = new Point2f( start );
		Vector2f vec = getNormalizedVector();
		
		//rotate 90
		pos.x += -vec.y * amt;
		pos.y += vec.x * amt;
		
		return pos;
	}
	
	public Vector2f getOffsetVector( float amt )
	{
		Vector2f vec = new Vector2f( -getNormalizedVector().y, getNormalizedVector().x );
		vec.scale( amt );
		return vec;
	}
}
