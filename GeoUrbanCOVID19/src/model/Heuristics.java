package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import gis.GISCommune;
import gis.GISNeighborhood;
import gis.GISPolygon;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.collections.Pair;
import util.PolygonUtil;

public final class Heuristics {

	/**
	 * Probabilities of a family having up to i members (i = 1,...,6)
	 */
	protected static final double[] FAMILY_PROBABILITIES = { 0.19, 0.23, 0.24,
			0.19, 0.8, 0.7 };

	/**
	 * Private constructor
	 */
	private Heuristics() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Assign family
	 * 
	 * @param citizen  Citizen
	 * @param citizens Citizens
	 */
	public static void assignFamily(Citizen citizen, List<Citizen> citizens) {
		List<Citizen> family = new ArrayList<>();
		family.add(citizen);
		if (citizen.getAge() < 18) {
			// Conditional probability
			double[] familyProbsKids = new double[FAMILY_PROBABILITIES.length
					- 1];
			double sumProb = 1 - FAMILY_PROBABILITIES[0];
			double sumPartial = 0;
			for (int j = 1; j < familyProbsKids.length; j++) {
				familyProbsKids[j] = FAMILY_PROBABILITIES[j + 1] / sumProb;
				sumPartial += familyProbsKids[j];
			}
			familyProbsKids[0] = 1 - sumPartial;
			// How many people are in the family?
			int acum = 0;
			double r = RandomHelper.nextDoubleFromTo(0, 1);
			int familyCount = 0;
			for (int i = 0; i < familyProbsKids.length; i++) {
				acum += familyProbsKids[i];
				if (r < acum) {
					familyCount = i;
					break;
				}
			}
			// Select family
			Citizen adult = null;
			int indexAdult = 0;
			for (Citizen otherCitizen : citizens) {
				if (otherCitizen.getAge() >= 18
						&& otherCitizen.getFamily().isEmpty()) {
					adult = otherCitizen;
					break;
				}
				indexAdult += 1;
			}
			if (adult == null) {
				// Case: Every adult has a family
				for (Citizen otherCitizen : citizens) {
					if (otherCitizen.getAge() >= 18
							&& !otherCitizen.getFamily().isEmpty()) {
						family = otherCitizen.getFamily();
						family.add(citizen);
						break;
					}
				}
			} else {
				// Case: The citizen has an adult in my family
				family.add(adult);
				if (familyCount > 0) {
					for (int i = 0; i < citizens.size(); i++) {
						if (i == indexAdult
								|| citizens.get(i).equals(citizen)) {
							continue;
						}
						if (citizens.get(i).getFamily().isEmpty()) {
							family.add(citizens.get(i));
						}
						if (familyCount == family.size() - 2) {
							break;
						}
					}
				}
			}
		} else {
			int acum = 0;
			double r = RandomHelper.nextDoubleFromTo(0, 1);
			int familyCount = 0;
			for (int i = 0; i < FAMILY_PROBABILITIES.length; i++) {
				acum += FAMILY_PROBABILITIES[i];
				if (r < acum) {
					familyCount = i;
					break;
				}
			}
			// How many people are in the family?
			if (familyCount > 0) {
				for (int i = 0; i < citizens.size(); i++) {
					if (citizens.get(i).equals(citizen)) {
						continue;
					}
					if (citizens.get(i).getFamily().isEmpty()) {
						family.add(citizens.get(i));
					}
					if (familyCount == family.size() - 1) {
						break;
					}
				}
			}
		}
		for (Citizen member : family) {
			member.setFamily(family);
		}
	}

	/**
	 * Assign house to a family
	 * 
	 * @param proxy    Family proxy
	 * @param communes Communes
	 */
	public static void assignHouse(Citizen proxy,
			Collection<GISCommune> communes) {
		// Get total population
		double totalPopulation = 0;
		for (GISCommune commune : communes) {
			double population = commune.getPopulation();
			totalPopulation += population;
		}
		// Select random commune
		GISCommune selectedCommune = null;
		double r = RandomHelper.nextDoubleFromTo(0, 1);
		double cummulativeProbability = 0;
		for (GISCommune commune : communes) {
			double population = commune.getPopulation();
			cummulativeProbability += population / totalPopulation;
			if (r <= cummulativeProbability) {
				selectedCommune = commune;
				break;
			}
		}
		// Select random neighborhood
		List<GISNeighborhood> neighborhoods = selectedCommune
				.getNeighborhoods();
		int index = RandomHelper.nextIntFromTo(0, neighborhoods.size() - 1);
		GISNeighborhood selectedNeighborhood = neighborhoods.get(index);
		// Generate random point inside selected neighborhood
		NdPoint selectedHouse = PolygonUtil
				.getRandomPoint(selectedNeighborhood);
		// Assign same household to all family members
		for (Citizen member : proxy.getFamily()) {
			member.setLivingNeighborhood(selectedNeighborhood);
			member.setHomeplace(selectedHouse);
		}
	}

	/**
	 * Get SOD-based workplace
	 * 
	 * @param sod                SOD matrix
	 * @param livingNeighborhood Living neighborhood
	 * @param neighborhoods      Neighborhoods
	 */
	public static Pair<NdPoint, GISNeighborhood> getSODBasedWorkplace(
			SODMatrix sod, GISPolygon livingNeighborhood,
			Map<String, GISPolygon> neighborhoods) {
		String livingNeighborhoodId = livingNeighborhood.getId();
		String workingNeighborhoodId = null;
		if (sod.containsOrigin(livingNeighborhoodId)) {
			List<Pair<String, Double>> travels = sod
					.getTravelsFromOrigin(livingNeighborhoodId);
			int player1 = RandomHelper.nextIntFromTo(0, travels.size() - 1);
			for (int i = 0; i < 10; i++) {
				int player2 = RandomHelper.nextIntFromTo(0, travels.size() - 1);
				double decision = RandomHelper.nextDoubleFromTo(0, 1);
				double travelsP1 = travels.get(player1).getSecond();
				double travelsP2 = travels.get(player2).getSecond();
				double sum = travelsP1 + travelsP2;
				if (travelsP1 < travelsP2) {
					int temp = player1;
					player1 = player2;
					player2 = temp;
				}
				if (decision >= travelsP1 / sum) {
					player1 = player2;
				}
			}
			Pair<String, Double> destination = travels.get(player1);
			workingNeighborhoodId = destination.getFirst();
		}
		GISPolygon selectedNeighborhood = neighborhoods
				.get(workingNeighborhoodId);
		if (selectedNeighborhood == null) {
			selectedNeighborhood = neighborhoods.get(livingNeighborhoodId);
		}
		NdPoint workplace = PolygonUtil.getRandomPoint(selectedNeighborhood);
		return new Pair<>(workplace, (GISNeighborhood) selectedNeighborhood);
	}

}