package com.danwink.trafficsim;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Vector2f;

import com.danwink.trafficsim.Road.RoadConnection;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class Car 
{
	float pos;
	int rdir = 1;
	Road r;
	
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
	
	public void update( ArrayList<Car> cars )
	{
		//Randomly change roads
		for( RoadConnection rc : r.connections )
		{
			if( r.getPosition( pos ).distanceSquared( r.getPosition( rc.pos ) ) < 1 )
			{
				if( DMath.randomf() > .5f )
				{
					for( RoadConnection rc2 : rc.road.connections )
					{
						if( rc2.road == r )
						{
							rdir = DMath.randomf() > .5f ? -1 : 1;
							pos = rc2.pos;
						}
					}
					r = rc.road;
					break;
				}
			}
		}
		
		for( Car c : cars )
		{
			if( c.r == r && c.rdir == rdir )
			{
				float dist = c.pos*c.r.getLength()-pos*r.getLength();
				if( dist > 0 && dist < 10 ) return;
			}
		}
		
		pos += rdir * 1f * (1.f/r.getLength());
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
