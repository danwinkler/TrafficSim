package com.danwink.trafficsim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class TwoLaneRoad extends Road
{
	public TwoLaneRoad( float x1, float y1, float x2, float y2 )
	{
		start.x = x1;
		start.y = y1;
		end.x = x2;
		end.y = y2;
		width = 20;
	}
	
	Color color = new Color( DMath.randomi( 0, 255 ), DMath.randomi( 0, 255 ), DMath.randomi( 0, 255 ) );
	
	public void render( Graphics2DRenderer g )
	{
		g.color( color );
		switch( type )
		{
		case STRAIGHT:
			Vector2f vector = getVector();
			Point2f right = getOffset( width/2 );
			Point2f left = getOffset( -width/2 );
			
			RoadConnection startRoad = null;
			RoadConnection endRoad = null;
			
			ArrayList<RoadConnection> rightRoads = new ArrayList<RoadConnection>();
			ArrayList<RoadConnection> leftRoads = new ArrayList<RoadConnection>();
			
			for( RoadConnection rc : connections )
			{
				if( rc.pos == 0 && rc.side == 0 ) startRoad = rc;
				else if( rc.pos == 1 && rc.side == 0 ) endRoad = rc;
				else if( rc.side == 1 )
				{
					rightRoads.add( rc );
				}
				else if( rc.side == -1 )
				{
					leftRoads.add( rc );
				}
			}
			
			Collections.sort( rightRoads, roadConnectionComparator );
			Collections.sort( leftRoads, roadConnectionComparator );
			
			//g.color( Color.BLACK );
			
			//g.line( right.x, right.y, right.x + vector.x, right.y + vector.y );
			//g.line( left.x, left.y, left.x + vector.x, left.y + vector.y );
			
			for( int i = 0; i < 2; i++ )
			{
				Point2f offset = null;
				ArrayList<RoadConnection> rcArr = null;
				switch( i )
				{
				case 0: offset = right; rcArr = rightRoads; break;
				case 1: offset = left; rcArr = leftRoads; break;
				}
				
				if( rcArr.size() == 0 )
				{
					g.line( offset.x + vector.x * (startRoad == null ? 0 : startRoad.road.width*.5f/getLength()), 
							offset.y + vector.y * (startRoad == null ? 0 : startRoad.road.width*.5f/getLength()), 
							offset.x + vector.x * (1-(endRoad == null ? 0 : endRoad.road.width*.5f/getLength())), 
							offset.y + vector.y * (1-(endRoad == null ? 0 : endRoad.road.width*.5f/getLength())) );
				} else
				{
					RoadConnection first = rcArr.get(0);
					
					g.line( offset.x + vector.x * (startRoad == null ? 0 : startRoad.road.width*.5f/getLength()), 
							offset.y + vector.y * (startRoad == null ? 0 : startRoad.road.width*.5f/getLength()), 
							offset.x + vector.x*(first.pos-(first.road.width*.5f/getLength())), 
							offset.y + vector.y*(first.pos-(first.road.width*.5f/getLength())) );
					for( int j = 0; j < rcArr.size()-1; j++ )
					{
						RoadConnection a = rcArr.get( j );
						RoadConnection b = rcArr.get( j+1 );
						
						g.line( offset.x + vector.x*(a.pos+(a.road.width*.5f/getLength())), 
								offset.y + vector.y*(a.pos+(a.road.width*.5f/getLength())), 
								offset.x + vector.x*(b.pos-(b.road.width*.5f/getLength())), 
								offset.y + vector.y*(b.pos-(b.road.width*.5f/getLength())) );
					}
					RoadConnection last = rcArr.get(rcArr.size()-1);
					g.line( offset.x + vector.x * (last.pos+(last.road.width*.5f/getLength())), 
							offset.y + vector.y * (last.pos+(last.road.width*.5f/getLength())), 
							offset.x + vector.x * (1-(endRoad == null ? 0 : endRoad.road.width*.5f/getLength())), 
							offset.y + vector.y * (1-(endRoad == null ? 0 : endRoad.road.width*.5f/getLength())) );
				}
			}
		}
	}
}
