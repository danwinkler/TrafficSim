package com.danwink.trafficsim;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

import com.danwink.trafficsim.Road.RoadConnection;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class TrafficSim extends Graphics2DRenderer 
{
	ArrayList<Road> roads = new ArrayList<Road>();
	ArrayList<Car> cars = new ArrayList<Car>();
	
	public void initialize() 
	{
		size( 800, 600 );
		
		Road[] vr = new Road[10];
		for( int i = 0; i < 10; i++ )
		{
			vr[i] = new TwoLaneRoad( i*50, 50, i*50, 500 );
			roads.add( vr[i] );
		}
		
		for( float y = 0; y <= 1; y += .1f )
		{
			for( int x = 0; x < 9; x++ )
			{
				roads.add( connectRoads( vr[x], y, vr[x+1], y ) );
			}
		}
		
		for( int i = 0; i < 10; i++ )
		{
			cars.add( new Car( vr[0] ) );
		}
	}

	public void update() 
	{
		for( Car c : cars )
		{
			c.update();
		}
		
		color( Color.white );
		fillRect( 0, 0, getWidth(), getHeight() );
		
		for( Road r : roads )
		{
			r.render( this );
		}
		
		for( Car c : cars )
		{
			c.render( this );
		}
	}
	
	public Road connectRoads( Road a, float ad, Road b, float bd )
	{
		Point2f pa = new Point2f( DMath.lerp( ad, a.start.x, a.end.x ), DMath.lerp( ad, a.start.y, a.end.y ) );
		Point2f pb = new Point2f( DMath.lerp( bd, b.start.x, b.end.x ), DMath.lerp( bd, b.start.y, b.end.y ) );
		
		Road r = new TwoLaneRoad( pa.x, pa.y, pb.x, pb.y );
		
		r.connections.add( r.new RoadConnection( a, 0, 0 ) );
		r.connections.add( r.new RoadConnection( b, 0, 1 ) );
		
		//To understand how to find which side a road is on, see this: 
		//http://stackoverflow.com/questions/13221873/determining-if-one-2d-vector-is-to-the-right-or-left-of-another
		
		Vector2f rv = new Vector2f( r.end );
		rv.sub( r.start );
		
		rv.set( -rv.y, rv.x ); //rot90CCW
		
		Vector2f av = new Vector2f( a.end );
		av.sub( a.start );
		a.connections.add( a.new RoadConnection( r, av.dot( rv ) > 0 ? 1 : 0, ad ) );
		
		Vector2f bv = new Vector2f( b.end );
		bv.sub( b.start );
		b.connections.add( b.new RoadConnection( r, bv.dot( rv ) > 0 ? 1 : 0, bd ) );
		
		return r;
	}
	
	public static void main( String[] args )
	{
		TrafficSim ts = new TrafficSim();
		ts.begin();
	}
}
