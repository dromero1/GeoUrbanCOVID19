package util;

import java.util.List;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import gis.GISPolygon;
import repast.simphony.gis.util.GeometryUtil;
import repast.simphony.space.continuous.NdPoint;

public class PolygonUtil {

	/**
	 * Private constructor
	 */
	private PolygonUtil() {
		throw new UnsupportedOperationException("Utility class");
	}
	
	/**
	 * Get random point from polygon
	 * 
	 * @param polygon Polygon
	 */
	public static NdPoint getRandomPoint(GISPolygon polygon) {
		Geometry geometry = polygon.getGeometry();
		List<Coordinate> coordinates = GeometryUtil.generateRandomPointsInPolygon(geometry, 1);
		Coordinate coordinate = coordinates.get(0);
		return new NdPoint(coordinate.x, coordinate.y);
	}

}