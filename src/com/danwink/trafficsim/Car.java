package com.danwink.trafficsim;

import java.awt.Color;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;

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
		g.fillOval( p.x-5, p.y-5, 10, 10 );
	}
	
	public void update()
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
							rdir = rc2.pos > .5f ? -1 : 1;
							pos = rc2.pos;
						}
					}
					r = rc.road;
					break;
				}
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
