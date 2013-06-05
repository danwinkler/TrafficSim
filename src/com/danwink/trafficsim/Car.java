package com.danwink.trafficsim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Vector2f;

import com.danwink.trafficsim.Road.RoadConnection;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class Car 
{
	float maxSpeed = DMath.randomf( 1.1f, 2.2f );
	float accel = .1f;
	
	float speed = 0;
	float pos;
	int rdir = 1;
	Road r;
	
	ArrayList<Road> path = new ArrayList<Road>();
	ArrayList<Float> pathDir = new ArrayList<Float>(); 
	
	public Car( Road r ) 
	{
		this.r = r;
	}

	public void render( Graphics2DRenderer g )
	{
		g.color( Color.red );
		Point2f p = r.getPosition( pos );
		Vector2f v = r.getOffsetVector( r.width/4 );
		v.scale( rdir );
		p.add( v );
		g.fillOval( p.x-5, p.y-5, 10, 10 );		
	}
	
	public void pathTo( Road dest )
	{
		HashMap visited = new HashMap();
		pathHelper( pos, r, dest, visited );
	}
	
	private boolean pathHelper( float pos, Road on, Road dest, HashMap visited )
	{
		visited.put( on, 1 );
		path.add( on );
		for( RoadConnection rc : on.connections )
		{
			if( visited.get( rc.road ) == null )
			{
				pathDir.add( (float)(rc.pos - pos > 0 ? 1 : -1) );
				if( rc.road == dest ) return true;
				
				boolean v = pathHelper( rc.pos, rc.road, dest, visited );
				if( v ) return true;
				pathDir.remove( pathDir.size()-1 );
			}
		}
		path.remove( path.size()-1 );
		return false;
	}
	
	public void update( ArrayList<Car> cars, ArrayList<Road> roads )
	{
		assert( pathDir.size() == path.size() );
		if( path.size() == 0 )
		{
			pathTo( roads.get( DMath.randomi( 0, roads.size() ) ) );
		}
		else
		{
			if( path.get( 0 ) == r )
			{
				path.remove( 0 );
				rdir = pathDir.remove( 0 ) > 0 ? 1 : -1;
			}
			else
			{
				for( RoadConnection rc : r.connections )
				{
					if( path.get( 0 ) == rc.road )
					{
						if( r.getPosition( pos ).distanceSquared( r.getPosition( rc.pos ) ) < 1 )
						{
							RoadConnection rc2 = rc.road.getByRoad( r );
							rdir = pathDir.get( 0 ) > 0 ? 1 : -1;
							pos = rc2.pos;	
							r = rc.road;
							break;
						}
					}
				}
			}
		}
		
		float cCarD = 1000;
		Car cCar = null;
		for( Car c : cars )
		{
			if( c.r == r && c.rdir == rdir && c != this )
			{
				float dist = (c.pos*c.r.getLength()-pos*r.getLength()) * rdir;
				if( dist > 0 && dist < cCarD ) 
				{
					cCarD = dist;
					cCar = c;
				}
			}
		}
		
		if( cCar != null )
		{
			if( cCarD > 0 )
			{
				if( cCarD < 10 )
				{
					speed = 0;
				}
				if( cCarD < 15 )
				{
					if( speed > 0 )
					{
						speed -= accel*2;
						if( speed < 0 ) speed = 0;
					} 
				} else if( cCarD < 50*speed )
				{
					if( speed > cCar.speed )
					{
						speed -= accel*.5f;
					}
					if( speed < 0 ) speed = 0;
				}
				else if( speed < maxSpeed )
				{
					speed += accel;
				}
			}
		}
		else
		{
			if( speed < maxSpeed )
			{
				speed += accel;
			}
		}
		
		pos += rdir * (speed/r.getLength());
		if( pos > 1 )
		{
			pos = 1;
			rdir = -1;
		}
		else if( pos < 0 )
		{
			pos = 0;
			rdir = 1;
		}
	}
}
