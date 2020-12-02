package styles;

import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.SurfaceShape;
import java.awt.Color;
import gis.GISPolygon;
import repast.simphony.visualization.gis3D.style.SurfaceShapeStyle;

public class GISPolygonStyle implements SurfaceShapeStyle<GISPolygon> {

	/**
	 * Get surface shape
	 * 
	 * @param polygon Polygon
	 * @param shape   Shape
	 */
	@Override
	public SurfaceShape getSurfaceShape(GISPolygon polygon,
			SurfaceShape shape) {
		return new SurfacePolyline();
	}

	/**
	 * Get fill color
	 * 
	 * @param polygon Polygon
	 */
	@Override
	public Color getFillColor(GISPolygon polygon) {
		return Color.WHITE;
	}

	/**
	 * Get fill opacity
	 * 
	 * @param polygon Polygon
	 */
	@Override
	public double getFillOpacity(GISPolygon polygon) {
		return 0.99;
	}

	/**
	 * Get line color
	 * 
	 * @param polygon Polygon
	 */
	@Override
	public Color getLineColor(GISPolygon polygon) {
		return Color.BLACK;
	}

	/**
	 * Get line opacity
	 * 
	 * @param polygon Polygon
	 */
	@Override
	public double getLineOpacity(GISPolygon polygon) {
		return 1.0;
	}

	/**
	 * Get line width
	 * 
	 * @param polygon Polygon
	 */
	@Override
	public double getLineWidth(GISPolygon polygon) {
		return 3;
	}

}