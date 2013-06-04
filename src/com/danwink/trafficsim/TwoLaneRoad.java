package com.danwink.trafficsim;

import java.awt.Color;

import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class TwoLaneRoad extends Road
{
	public static float width = 20;
	
	public TwoLaneRoad( float x1, float y1, float x2, float y2 )
	{
		start.x = x1;
		start.y = y1;
		end.x = x2;
		end.y = y2;
	}
	
	public void render( Graphics2DRenderer g )
	{
		switch( type )
		{
		case STRAIGHT:
			g.color( Color.BLACK );
			g.line( start.x, start.y, end.x, end.y );
		}
	}
}
