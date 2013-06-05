package com.danwink.trafficsim;

import java.awt.Color;
import java.awt.Polygon;
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
	
	public void render( Graphics2DRenderer g )
	{
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
			
			RoadConnection startrc = null;
			RoadConnection endrc = null;
			
			if( startRoad != null )
			{
				startrc = startRoad.road.getByRoad( this );
			}
			
			if( endRoad != null )
			{
				endrc = endRoad.road.getByRoad( this );
			}
			
			Polygon poly = new Polygon();
			Point2f tp = null;
			if( startRoad != null ) tp = getIntersection( this, 1, startRoad.road, startrc.side );
			poly.addPoint( (int)(startRoad == null ? right.x : tp.x), 
							(int)(startRoad == null ? right.y : tp.y) );
			
			if( startRoad != null ) tp = getIntersection( this, -1, startRoad.road, startrc.side );
			poly.addPoint( (int)(startRoad == null ? left.x : tp.x), 
							(int)(startRoad == null ? left.y : tp.y) );
			
			if( endRoad != null ) tp = getIntersection( this, -1, endRoad.road, endrc.side );
			poly.addPoint( (int)(endRoad == null ? left.x + vector.x : tp.x), 
							(int)(endRoad == null ? left.y + vector.y : tp.y) );
			
			if( endRoad != null ) tp = getIntersection( this, 1, endRoad.road, endrc.side );
			poly.addPoint( (int)(endRoad == null ? right.x + vector.x : tp.x), 
							(int)(endRoad == null ? right.y + vector.y : tp.y) );
			
			g.setLineWidth( 1 );
			g.color( Color.lightGray );
			g.g.fill( poly );
			
			g.color( Color.black );
			g.setLineWidth( 2 );
			for( int i = 0; i < 2; i++ )
			{
				Point2f offset = null;
				ArrayList<RoadConnection> rcArr = null;
				int side = 0;
				switch( i )
				{
				case 0: offset = right; rcArr = rightRoads; side = 1; break;
				case 1: offset = left; rcArr = leftRoads; side = -1; break;
				}
				
				Point2f startRoadPos = null;
				Point2f endRoadPos = null;
				
				if( startRoad != null )
				{
					startRoadPos = getIntersection( this, side, startRoad.road, startrc.side );
				}
				
				if( endRoad != null )
				{
					endRoadPos = getIntersection( this, side, endRoad.road, endrc.side );
				}
				
				if( rcArr.size() == 0 )
				{
					g.line( (startRoad == null ? offset.x : startRoadPos.x), 
							(startRoad == null ? offset.y : startRoadPos.y), 
							(endRoad == null ? offset.x + vector.x : endRoadPos.x), 
							(endRoad == null ? offset.y + vector.y : endRoadPos.y) );
				} else
				{
					RoadConnection first = rcArr.get(0);
					
					Point2f fp = getIntersection( this, side, first.road, (first.road.getByRoad( this ).pos < .5f ? -1 : 1) * -side );
					g.line( (startRoad == null ? offset.x : startRoadPos.x), 
							(startRoad == null ? offset.y : startRoadPos.y), 
							fp.x, 
							fp.y );
					
					for( int j = 0; j < rcArr.size()-1; j++ )
					{
						RoadConnection a = rcArr.get( j );
						Point2f ap = getIntersection( this, side, a.road, (a.road.getByRoad( this ).pos < .5f ? 1 : -1) * -side );
						
						RoadConnection b = rcArr.get( j+1 );
						Point2f bp = getIntersection( this, side, b.road, (b.road.getByRoad( this ).pos < .5f ? -1 : 1) * -side );
						g.line( ap.x, 
								ap.y,
								bp.x,
								bp.y );
					}
					
					RoadConnection last = rcArr.get(rcArr.size()-1);
					Point2f lp = getIntersection( this, side, last.road, (last.road.getByRoad( this ).pos < .5f ? 1 : -1) * -side );
					
					g.line( lp.x, 
							lp.y, 
							(endRoad == null ? offset.x + vector.x : endRoadPos.x), 
							(endRoad == null ? offset.y + vector.y : endRoadPos.y) );
				}
			}
			
			g.color( Color.yellow );
			for( float i = 6; i < getLength(); i += 12 )
			{
				g.line( start.x + vector.x * i/getLength(), start.y + vector.y * i/getLength(), 
						start.x + vector.x * (i+3)/getLength(), start.y + vector.y * (i+3)/getLength() );
			}
			break;
		}
	}
	
	public static Point2f getIntersection( Road r1, int r1side, Road r2, int r2side )
	{
		Point2f a = r1.getOffset( r1.width/2 * r1side );
		Point2f b = new Point2f( a );
		b.add( r1.getVector() );
		
		Point2f c = r2.getOffset( r2.width/2 * r2side );
		Point2f d = new Point2f( c );
		d.add( r2.getVector() );
		return DMath.lineLineIntersection( a, b, c, d );
	}
}
