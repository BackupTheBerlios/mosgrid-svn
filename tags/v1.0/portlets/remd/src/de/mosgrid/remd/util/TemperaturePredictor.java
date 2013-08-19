package de.mosgrid.remd.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.special.Erf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemperaturePredictor {
	private final Logger LOGGER = LoggerFactory.getLogger(TemperaturePredictor.class);
	private static final int STEP_SIZE = 2;
	/* prediction constancts in kJ/mol */
	private static final double A0 = -59.2194;
	private static final double B0 = -22.8396;
	private static final double D0 = 1.1677;
	/* prediction constancts in kJ/mol/K */
	private static final double A1 = 0.07594;
	private static final double B1 = 0.01347;
	private static final double D1 = 0.002976;
	// Boltzmann constant in kJ/mol/K
	private static final double kB = 0.0083144621;
	private static final double SQR_2 = Math.sqrt(2.0);

	/* calc input */
	private int maxNumberOfReplicas = Integer.MAX_VALUE;
	private double desiredExchangeProbability = 0.2;
	private double probabilityTolerance = 1E-4;
	private double lowerTemperatureLimit = 275;
	private double upperTemperatureLimit = 375;
	// number of water molecules
	private long numberOfWaterMolecues = 0;
	// number of protein atoms
	private long numberOfProteinAtoms = 0;
	private HydrogensInProtein proteinHydrogens = HydrogensInProtein.ALL_H;
	private WaterConstraints waterConstraints = WaterConstraints.NONE;
	private ProteinConstraints proteinConstraints = ProteinConstraints.NONE;
	private VirtualSites virtualSites = VirtualSites.NONE;

	private long degreesOfFreedom;
	private double Nprot;
	private double flexEnergy;

	public List<IReplica> start() throws IllegalStateException {
		LOGGER.trace("Starting to compute replica distribution for REMD.");
		List<IReplica> replicas = new ArrayList<IReplica>();

		preComputations();
		Replica currentReplica = new Replica(1, lowerTemperatureLimit);
		currentReplica.setPotEnergyMean(exp(currentReplica.getTemperature()));
		currentReplica.setPotEnergySD(sd(currentReplica.getTemperature()));
		replicas.add(currentReplica);
		while (currentReplica.getTemperature() <= upperTemperatureLimit && currentReplica.getID() < maxNumberOfReplicas) {
			Replica nextReplica = binarySearch(currentReplica);
			if (nextReplica != null) {
				nextReplica.setPotEnergyMean(exp(nextReplica.getTemperature()));
				nextReplica.setPotEnergySD(sd(nextReplica.getTemperature()));
				replicas.add(nextReplica);
				currentReplica = nextReplica;
			} else {
				// break loop if no further replica could be found
				break;
			}
		}
		if (LOGGER.isTraceEnabled()) {
			StringBuilder logBuilder = new StringBuilder("Results:");
			for (IReplica r : replicas) {
				logBuilder.append("\n\t" + r.toString());
			}
			LOGGER.trace(logBuilder.toString());
		}

		return replicas;
	}

	/**
	 * Performs a search for the next temperature level from given replica. At first increases temperature by STEP_SIZE.
	 * After pExchange is smaller than desired pExchange a binary search is started.
	 */
	private Replica binarySearch(Replica replicaA) {
		boolean binarySearch = false;
		// init binary search bounds
		double upperBound = upperTemperatureLimit;
		double lowerBound = replicaA.getTemperature();
		// init first temperature for replica B
		// double T2 = lowerBound + (upperBound - lowerBound) / 2;
		double T2 = lowerBound + 1;
		// init new Replica
		Replica replicaB = new Replica(replicaA.getID() + 1, T2);
		// init exchange probability
		double pExchange = Double.MAX_VALUE;

		// iterate until exchange probability converged
		while ((Math.abs(pExchange - desiredExchangeProbability)) > probabilityTolerance && T2 >= 0) {
			pExchange = Math.min(1, calcExchangeProb(replicaA, replicaB));
			if (pExchange > desiredExchangeProbability) {
				// move up
				lowerBound = T2;
				if (binarySearch) {
					T2 = T2 + ((upperBound - T2) / 2);
				} else {
					T2 += STEP_SIZE;
				}
			} else {// pExchange is smaller than desired pExchange
				// move down
				upperBound = T2;
				if (binarySearch) {
					T2 = T2 - ((T2 - lowerBound) / 2);
				} else {
					T2 -= STEP_SIZE;
					binarySearch = true;
				}
			}
			replicaB.setTemperature(T2);
		}
		return replicaB;
	}

	/**
	 * Helper method for precomputations
	 */
	private void preComputations() {
		Nprot = 0;
		// number of hydrogens
		long Nh = 0;
		// number of virtual sites
		long Nv = 0;
		// number of constraints
		long Nc = 0;
		if (proteinHydrogens == HydrogensInProtein.ALL_H) {
			// estimate number of hydrogens
			Nh = Math.round(numberOfProteinAtoms * 0.5134);
			if (virtualSites == VirtualSites.VIRTUAL_HYDROGENS) {
				// estimate number of virtual sites
				Nv = Math.round(1.91 * Nh);
			}
			Nprot = numberOfProteinAtoms;
		} else if (proteinHydrogens == HydrogensInProtein.POLAR_H) {
			// estimate real number of atoms in protein as only polar h. are given
			Nprot = Math.round(numberOfProteinAtoms * 1.516139);
			// estimate number of hydrogens
			Nh = Math.round(numberOfProteinAtoms * 0.22);
			if (virtualSites == VirtualSites.VIRTUAL_HYDROGENS) {
				// estimate number of virtual sites
				Nv = Math.round(numberOfProteinAtoms + 1.91 * Nh);
			}
		}
		if (proteinConstraints == ProteinConstraints.HYDROGEN_BONDS_ONLY) {
			Nc = Nh;
		} else if (proteinConstraints == ProteinConstraints.ALL_BONDS) {
			Nc = numberOfProteinAtoms;
		}

		int Wc = 0;
		if (waterConstraints == WaterConstraints.FLEXIBLE_ANGLE) {
			Wc = 2;
		} else if (waterConstraints == WaterConstraints.RIGID) {
			Wc = 3;
		}
		// degrees of freedom
		degreesOfFreedom = (9 - Wc) * numberOfWaterMolecues + 3 * numberOfProteinAtoms - Nc - Nv;

		// flex energy
		flexEnergy = 0.5 * kB * (Nc + Nv + Wc * numberOfWaterMolecues);

		if (LOGGER.isTraceEnabled()) {
			StringBuilder logBuilder = new StringBuilder();
			logBuilder.append("\tpExchange: " + desiredExchangeProbability);
			logBuilder.append("\n\tTemp-Range: " + lowerTemperatureLimit + "-" + upperTemperatureLimit);
			logBuilder.append("\n\tNumber of water molecules: " + numberOfWaterMolecues);
			logBuilder.append("\n\tNumber of protein atoms: " + numberOfProteinAtoms);
			logBuilder.append("\n\tNumber of hydrogens in protein: " + Nh);
			logBuilder.append("\n\tNumber of constraints: " + Nc);
			logBuilder.append("\n\tNumber of virtual sites: " + Nv);
			logBuilder.append("\n\tDegrees of freedom: " + degreesOfFreedom);
			logBuilder.append("\n\tEnergy loss: " + flexEnergy);
			LOGGER.trace(logBuilder.toString());
		}
	}

	/**
	 * Calculates the exchange probability for two replicas with given temperature
	 */
	private double calcExchangeProb(Replica replicaA, Replica replicaB) {
		double T1 = replicaA.getTemperature();
		double T2 = replicaB.getTemperature();
		double c = (1 / kB) * ((1 / T2) - (1 / T1));
		// System.out.println("c " + c);

		// double mu_12 = 450.5;
		double mean = (T2 - T1) * ((A1 * numberOfWaterMolecues) + (B1 * Nprot) - flexEnergy);
		replicaB.setEnergyDifMean(mean);
		// System.out.println("mu_12 " + mu_12);

		double variance = degreesOfFreedom
				* (D1 * D1 * (T2 * T2 + T1 * T1) + 2.0 * D1 * D0 * (T2 + T1) + 2.0 * D0 * D0);
		// System.out.println("si_12_sq " + si_12_sq);

		double sd = Math.sqrt(variance);
		replicaB.setEnergyDifSD(sd);

		// System.out.println("si_12 " + si_12);

		double denom1 = sd * SQR_2;

		double term1 = 1.0 + Erf.erf(-mean / denom1);
		// System.out.println("term1 " + term1);

		double expon = c * mean + (c * c * variance) / 2.0;
		// System.out.println("expon " + expon);

		double term2_1 = 1.0 + Erf.erf((mean + c * variance) / denom1);
		// System.out.println("term2_1 " + term2_1);

		double term2 = Math.exp(expon) * term2_1;
		// System.out.println("term2 " + term2);

		double pEx = 0.5 * (term1 + term2);
		// System.out.println("p: " + pEx);
		replicaB.setExchangeProbability(pEx);
		return pEx;
	}

	private double exp(double t) {
		return ((A0 + A1 * t) * numberOfWaterMolecues + (B0 + B1 * t) * Nprot - t * flexEnergy);
	}

	private double sd(double T) {
		return (D0 + D1 * T) * Math.sqrt(degreesOfFreedom);
	}

	public int getMaxNumberOfReplicas() {
		return maxNumberOfReplicas;
	}

	public void setMaxNumberOfReplicas(int maxNumberOfReplicas) {
		if (maxNumberOfReplicas < 1) {
			throw new IllegalArgumentException("Max. number of replicas must be greater than zero!");
		}
		this.maxNumberOfReplicas = maxNumberOfReplicas;
	}

	public ProteinConstraints getProteinConstraints() {
		return proteinConstraints;
	}

	public void setProteinConstraints(ProteinConstraints proteinConstraints) {
		this.proteinConstraints = proteinConstraints;
	}

	public double getDesiredExchangeProbability() {
		return desiredExchangeProbability;
	}

	public void setDesiredExchangeProbability(double desiredExchangeProbability) {
		if (desiredExchangeProbability <= 0 || desiredExchangeProbability >= 1) {
			throw new IllegalArgumentException("Exchange probability must be greater than zero and smaller than one!");
		}
		this.desiredExchangeProbability = desiredExchangeProbability;
	}

	public double getProbabilityTolerance() {
		return probabilityTolerance;
	}

	public void setProbabilityTolerance(double probabilityTolerance) {
		this.probabilityTolerance = probabilityTolerance;
	}

	public double getLowerTemperatureLimit() {
		return lowerTemperatureLimit;
	}

	public void setLowerTemperatureLimit(double lowerTemperatureLimit) {
		this.lowerTemperatureLimit = lowerTemperatureLimit;
	}

	public double getUpperTemperatureLimit() {
		return upperTemperatureLimit;
	}

	public void setUpperTemperatureLimit(double upperTemperatureLimit) {
		this.upperTemperatureLimit = upperTemperatureLimit;
	}

	public long getNumberOfWaterMolecues() {
		return numberOfWaterMolecues;
	}

	public void setNumberOfWaterMolecues(long numberOfWaterMolecues) {
		this.numberOfWaterMolecues = numberOfWaterMolecues;
	}

	public long getNumberOfProteinAtoms() {
		return numberOfProteinAtoms;
	}

	public void setNumberOfProteinAtoms(long numberOfProteinAtoms) {
		this.numberOfProteinAtoms = numberOfProteinAtoms;
	}

	public HydrogensInProtein getProteinHydrogens() {
		return proteinHydrogens;
	}

	public void setProteinHydrogens(HydrogensInProtein proteinHydrogens) {
		this.proteinHydrogens = proteinHydrogens;
	}

	public WaterConstraints getWaterConstraints() {
		return waterConstraints;
	}

	public void setWaterConstraints(WaterConstraints waterConstraints) {
		this.waterConstraints = waterConstraints;
	}

	public VirtualSites getVirtualSites() {
		return virtualSites;
	}

	public void setVirtualSites(VirtualSites virtualSites) {
		this.virtualSites = virtualSites;
	}

	public static double getA0() {
		return A0;
	}

	public static double getB0() {
		return B0;
	}

	public static double getD0() {
		return D0;
	}

	public static double getA1() {
		return A1;
	}

	public static double getB1() {
		return B1;
	}

	public static double getD1() {
		return D1;
	}

	public static double getKb() {
		return kB;
	}

	public static double getSqr2() {
		return SQR_2;
	}

	public long getDegreesOfFreedom() {
		return degreesOfFreedom;
	}

	public double getNprot() {
		return Nprot;
	}

	public double getFlexEnergy() {
		return flexEnergy;
	}

	/**
	 * Simple hidden implementation of the Replica interface
	 * 
	 */
	private class Replica implements IReplica {
		private DecimalFormat formatter = new DecimalFormat("0.00");
		private int id;
		private double temperature;
		private double potEnergyMean;
		private double potEnergySD;
		private double energyDifMean;
		private double energyDifSD;
		private double pExchange;

		public Replica(int id, double temperature) {
			super();
			this.id = id;
			this.temperature = temperature;
		}

		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}

		public void setPotEnergyMean(double mu) {
			this.potEnergyMean = mu;
		}

		public void setPotEnergySD(double sigma) {
			this.potEnergySD = sigma;
		}

		public void setEnergyDifMean(double mu_12) {
			this.energyDifMean = mu_12;
		}

		public void setEnergyDifSD(double sigma_12) {
			this.energyDifSD = sigma_12;
		}

		public void setExchangeProbability(double pEx_12) {
			this.pExchange = pEx_12;
		}

		@Override
		public int getID() {
			return id;
		}

		@Override
		public double getTemperature() {
			return temperature;
		}

		@Override
		public double getPotentialEnergyMean() {
			return potEnergyMean;
		}

		@Override
		public double getPotentialEnergySD() {
			return potEnergySD;
		}

		@Override
		public double getEnergyDifferenceMean() {
			return energyDifMean;
		}

		@Override
		public double getEnergyDifferenceSD() {
			return energyDifSD;
		}

		@Override
		public double getExchangeProbability() {
			return pExchange;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof IReplica) {
				IReplica other = (IReplica) obj;
				if (hashCode() == other.hashCode()) {
					if (temperature == other.getTemperature()) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "ID:" + id + ", T:" + getTemperatureAsString() + ", Epot_mean:" + getPotentialEnergyMeanAsString()
					+ ", Epot_sd" + getPotentialEnergySDAsString() + ", Epot_diff_mean:"
					+ getEnergyDifferenceMeanAsString() + ", Epot_diff_sd:" + getEnergyDifferenceSDAsString()
					+ ", pExchange:" + getExchangeProbabilityAsString();
		}

		@Override
		public String getTemperatureAsString() {
			return formatter.format(temperature);
		}

		@Override
		public String getPotentialEnergyMeanAsString() {
			return formatter.format(potEnergyMean);
		}

		@Override
		public String getPotentialEnergySDAsString() {
			return formatter.format(potEnergySD);
		}

		@Override
		public String getEnergyDifferenceMeanAsString() {
			return formatter.format(energyDifMean);
		}

		@Override
		public String getEnergyDifferenceSDAsString() {
			return formatter.format(energyDifSD);
		}

		@Override
		public String getExchangeProbabilityAsString() {
			return formatter.format(pExchange);
		}

	}

	/**
	 * A public Interface for Replicas
	 * 
	 */
	public interface IReplica {
		int getID();

		double getTemperature();

		String getTemperatureAsString();

		double getPotentialEnergyMean();

		String getPotentialEnergyMeanAsString();

		double getPotentialEnergySD();

		String getPotentialEnergySDAsString();

		double getEnergyDifferenceMean();

		String getEnergyDifferenceMeanAsString();

		double getEnergyDifferenceSD();

		String getEnergyDifferenceSDAsString();

		double getExchangeProbability();

		String getExchangeProbabilityAsString();

	}

	public enum HydrogensInProtein {
		ALL_H, POLAR_H;

		@Override
		public String toString() {
			switch (this) {
			case ALL_H:
				return "All Hydrogens";
			case POLAR_H:
				return "Polar Hydrogens";
			default:
				return super.toString();
			}
		}
	}

	public enum ProteinConstraints {
		NONE, HYDROGEN_BONDS_ONLY, ALL_BONDS;

		@Override
		public String toString() {
			switch (this) {
			case NONE:
				return "None";
			case HYDROGEN_BONDS_ONLY:
				return "Hydrogen bonds";
			case ALL_BONDS:
				return "All bonds";
			default:
				return super.toString();
			}
		}
	}

	public enum WaterConstraints {
		NONE, FLEXIBLE_ANGLE, RIGID;

		@Override
		public String toString() {
			switch (this) {
			case NONE:
				return "None";
			case FLEXIBLE_ANGLE:
				return "Flexible angle";
			case RIGID:
				return "Rigid";
			default:
				return super.toString();
			}
		}
	}

	public enum VirtualSites {
		NONE, VIRTUAL_HYDROGENS;

		@Override
		public String toString() {
			switch (this) {
			case NONE:
				return "None";
			case VIRTUAL_HYDROGENS:
				return "Virual Hydrogens";
			default:
				return super.toString();
			}
		}
	}

}
