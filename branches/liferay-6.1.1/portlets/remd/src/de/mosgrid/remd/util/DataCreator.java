package de.mosgrid.remd.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import de.mosgrid.dygraph.util.DataWithCustomErrorBars;
import de.mosgrid.dygraph.util.SimpleData;
import de.mosgrid.dygraph.widget.IData;
import de.mosgrid.remd.util.TemperaturePredictor.IReplica;

public class DataCreator {
	private static final int TOLERANCE = 3;
	private static final double STEP_SIZE = 50;

	private List<IReplica> lastComputedReplicas;
	private SimpleData lastComutedEDist;
	private DataWithCustomErrorBars lastComputedTDist;

	public IData createPotentialEnergyDistribution(List<IReplica> replicas) {
		if (lastComputedReplicas == replicas && lastComutedEDist != null) {
			return lastComutedEDist;
		} else {
			this.lastComputedReplicas = replicas;
			SimpleData data = new SimpleData();
			data.setTitle("Potential Energy Distribution of Replicas");
			data.setxLabel("Epot (kJ/mol)");
			data.setyLabel("P(E)");
			List<NormalDistribution> normalDistributions = new ArrayList<NormalDistribution>();
			for (IReplica replica : replicas) {
				data.addSeriesName("Replica " + replica.getID());
				NormalDistribution nd = new NormalDistribution(replica.getPotentialEnergyMean(),
						replica.getPotentialEnergySD());
				normalDistributions.add(nd);
			}

			IReplica first = replicas.get(0);
			double start = first.getPotentialEnergyMean() - TOLERANCE * first.getPotentialEnergySD();
			IReplica last = replicas.get(replicas.size() - 1);
			double stop = last.getPotentialEnergyMean() + TOLERANCE * last.getPotentialEnergySD();
			for (double i = start; i <= stop; i += STEP_SIZE) {
				StringBuilder valueLine = new StringBuilder((new Double(i)).toString());
				for (NormalDistribution nd : normalDistributions) {
					double value = nd.density(i);
					if (value < 10E-7) {
						value = 0;
					}
					valueLine.append("," + value);
				}
				data.addValueLine(valueLine.toString());
			}
			lastComutedEDist = data;
			return data;
		}
	}

	public IData createTemperatureDistribution(List<IReplica> replicas) {
		if (lastComputedReplicas == replicas && lastComputedTDist != null) {
			return lastComputedTDist;
		} else {
			this.lastComputedReplicas = replicas;
			double offset = replicas.get(0).getTemperature();
			double diff = replicas.get(1).getTemperature() - replicas.get(0).getTemperature();
			DataWithCustomErrorBars data = new DataWithCustomErrorBars();
			data.setTitle("Temperature Distribution of Replicas");
			data.setxLabel("Replica 1-" + replicas.size());
			data.setyLabel("Temperature (K)");
			data.addSeriesName("Temperature");
			for (int i = 0; i < replicas.size(); i++) {
				IReplica r = replicas.get(i);
				double temp = r.getTemperature();
				double linear = offset + i * diff;
				data.addValueLine(+r.getID() + "," + linear + ";" + temp + ";" + temp);
			}
			lastComputedTDist = data;
			return data;
		}
	}
}
