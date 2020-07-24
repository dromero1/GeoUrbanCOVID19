package styles;

import model.Citizen;
import model.Compartment;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.WWTexture;
import repast.simphony.visualization.gis3D.PlaceMark;
import repast.simphony.visualization.gis3D.style.MarkStyle;

public class CitizenStyle implements MarkStyle<Citizen> {

	/**
	 * Standard scale
	 */
	private static final float STANDARD_SCALE = 1.5f;

	/**
	 * Dimension width
	 */
	private static final int WIDTH = 3;

	/**
	 * Dimension height
	 */
	private static final int HEIGHT = 3;

	/**
	 * Texture map
	 */
	private Map<String, WWTexture> textureMap;

	/**
	 * Create a new citizen style
	 */
	public CitizenStyle() {
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		this.textureMap = new HashMap<String, WWTexture>();
		// Black circle
		BufferedImage image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE,
				Color.BLACK);
		this.textureMap.put("black-circle", new BasicWWTexture(image));
		// Orange circle
		image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE, Color.ORANGE);
		this.textureMap.put("orange-circle", new BasicWWTexture(image));
		// Green circle
		image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE, Color.GREEN);
		this.textureMap.put("green-circle", new BasicWWTexture(image));
		// Red circle
		image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE, Color.RED);
		this.textureMap.put("red-circle", new BasicWWTexture(image));
		// Blue circle
		image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE, Color.BLUE);
		textureMap.put("blue-circle", new BasicWWTexture(image));
		// Gray circle
		image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, dimension, STANDARD_SCALE, Color.GRAY);
		this.textureMap.put("gray-circle", new BasicWWTexture(image));
	}

	/**
	 * Get texture
	 * 
	 * @param citizen Citizen
	 * @param texture Texture
	 */
	@Override
	public WWTexture getTexture(Citizen citizen, WWTexture texture) {
		Compartment compartment = citizen.getCompartment();
		switch (compartment) {
		case DEAD:
			return this.textureMap.get("black-circle");
		case EXPOSED:
			return this.textureMap.get("orange-circle");
		case IMMUNE:
			return this.textureMap.get("green-circle");
		case INFECTED:
			return this.textureMap.get("red-circle");
		case SUSCEPTIBLE:
			return this.textureMap.get("blue-circle");
		default:
			return this.textureMap.get("gray-circle");
		}
	}

	/**
	 * Get scale
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public double getScale(Citizen citizen) {
		Compartment compartment = citizen.getCompartment();
		switch (compartment) {
		case SUSCEPTIBLE:
		case DEAD:
		case IMMUNE:
			return STANDARD_SCALE;
		case EXPOSED:
		case INFECTED:
			return STANDARD_SCALE * 2;
		default:
			return 0;
		}
	}

	/**
	 * Get icon offset
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public Offset getIconOffset(Citizen citizen) {
		return Offset.CENTER;
	}

	/**
	 * Get label offset
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public Offset getLabelOffset(Citizen citizen) {
		return null;
	}

	/**
	 * Get place mark
	 * 
	 * @param citizen Citizen
	 * @param mark    Place mark
	 */
	@Override
	public PlaceMark getPlaceMark(Citizen citizen, PlaceMark mark) {
		if (mark == null) {
			mark = new PlaceMark();
		}
		mark.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
		mark.setLineEnabled(false);
		return mark;
	}

	/**
	 * Get elevation
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public double getElevation(Citizen citizen) {
		return 0;
	}

	/**
	 * Get heading
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public double getHeading(Citizen citizen) {
		return 0;
	}

	/**
	 * Get label color
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public Color getLabelColor(Citizen citizen) {
		return null;
	}

	/**
	 * Get line width
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public double getLineWidth(Citizen citizen) {
		return 1;
	}

	/**
	 * Get label font
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public Font getLabelFont(Citizen citizen) {
		return null;
	}

	/**
	 * Get label
	 * 
	 * @param citizen Citizen
	 */
	@Override
	public String getLabel(Citizen citizen) {
		return null;
	}

	/**
	 * Get line material
	 * 
	 * @param citizen      Citizen
	 * @param lineMaterial Line material
	 */
	@Override
	public Material getLineMaterial(Citizen citizen, Material lineMaterial) {
		if (lineMaterial == null) {
			lineMaterial = new Material(Color.RED);
		}
		return lineMaterial;
	}

}