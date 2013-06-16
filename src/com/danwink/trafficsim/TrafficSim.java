package com.danwink.trafficsim;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

import com.danwink.trafficsim.Road.RoadConnection;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class TrafficSim extends Graphics2DRenderer implements MouseListener
{
	ArrayList<Road> roads = new ArrayList<Road>();
	ArrayList<Car> cars = new ArrayList<Car>();
	
	public void initialize() 
	{
		size( 800, 600 );
		
		Road[] vr = new Road[5];
		for( int i = 0; i < vr.length; i++ )
		{
			vr[i] = new TwoLaneRoad( i*100 + 50, 50, i*100 + 50, 500 );
			roads.add( vr[i] );
		}
		
		for( int x = 0; x < vr.length-1; x++ )
		{
			for( float y = .1f; y < 1; y += .5f )
			{
				roads.add( connectRoads( vr[x], y + DMath.randomf( -.01f, .01f ), vr[x+1], y + DMath.randomf( -.01f, .01f ) ) );
			}
		}
		
		for( int i = 0; i < 200; i++ )
		{
			cars.add( new Car( roads.get( DMath.randomi( 0, roads.size()-1 ) ) ) );
		}
		
		
		/*
		Road a = new TwoLaneRoad( 100, 100, 100, 500 );
		Road b = new TwoLaneRoad( 100, 200, 200, 500 );
	
		a.connections.add( a.new RoadConnection( b, 1, .25f ) );
		b.connections.add( b.new RoadConnection( a, 0, 0 ) );
		
		roads.add( a );
		roads.add( b );
		*/
		
		canvas.addMouseListener( this );
	}
	
	RoadPosition beginRoad;
	
	public void update() 
	{	
		for( Car c : cars )
		{
			c.update( cars, roads );
		}
		
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		color( Color.white );
		fillRect( 0, 0, getWidth(), getHeight() );
		
		synchronized( roads )
		{
			for( Road r : roads )
			{
				r.render( this );
			}
		}
		
		for( Car c : cars )
		{
			c.render( this );
		}
		
		if( beginRoad != null )
		{
			color( Color.GREEN );
			line( beginRoad.r.start.x + beginRoad.r.getVector().x*beginRoad.pos, beginRoad.r.start.y + beginRoad.r.getVector().y*beginRoad.pos, m.x, m.y );
		}
		
		if( m.clicked )
		{
			RoadPosition rp = getRoad( m.x, m.y );
			if( rp != null )
			{
				color( Color.GREEN );
				fillOval( rp.r.start.x + rp.r.getVector().x*rp.pos - 10, rp.r.start.y + rp.r.getVector().y*rp.pos - 10, 20, 20 );
			}
		}
	}
	
	public RoadPosition getRoad( float x, float y )
	{
		Point2f p = new Point2f( x, y );
		RoadPosition rp = null;
		float dis = 1000;
		for( int i = 0; i < roads.size(); i++ )
		{
			Road r = roads.get( i );
			Vector2f toLine = DMath.pointToLineSegment( r.start, r.getVector(), p );
			float d2 = toLine.lengthSquared();
			float rw2 = (r.width/2);
			rw2 *= rw2;
			if( d2 < dis && d2 < rw2 )
			{
				rp = new RoadPosition( r, DMath.posOnLineByPerpPoint( r.start, r.getVector(), p ) );
			}
		}
		return rp;
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
		
		Vector2f av = a.getVector();
		a.connections.add( a.new RoadConnection( r, av.dot( rv ) > 0 ? -1 : 1, ad ) );
		
		Vector2f bv = b.getVector();
		b.connections.add( b.new RoadConnection( r, bv.dot( rv ) > 0 ? 1 : -1, bd ) );
		
		return r;
	}
	
	public static void main( String[] args )
	{
		TrafficSim ts = new TrafficSim();
		ts.begin();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed( MouseEvent e ) 
	{
		
	}

	public void mouseReleased( MouseEvent e ) 
	{
		RoadPosition rp = getRoad( e.getX(), e.getY() );
		if( rp != null )
		{
			if( rp.pos < 0 ) rp.pos = 0;
			if( rp.pos > 1 ) rp.pos = 1;
		}
		
		if( beginRoad == null )
		{
			if( rp != null )
			{
				beginRoad = rp;
			}
		}
		else
		{
			if( rp != null )
			{
				synchronized( roads )
				{
					roads.add( connectRoads( beginRoad.r, beginRoad.pos, rp.r, rp.pos ) );
				}
				beginRoad = null;
			}
		}
	}
}
