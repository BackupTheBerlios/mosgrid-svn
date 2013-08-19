package de.mosgrid.qc.adapter;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterForMSMLWithDictionary;
import de.mosgrid.adapter.InputInfoForInputFile;
import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.msml.converter.nonstandart.GaussianCMLConverter;
import de.mosgrid.msml.util.wrapper.Job;

public abstract class GaussianAdapter extends AdapterForMSMLWithDictionary {
//	private static final Logger LOGGER = LoggerFactory.getLogger(GaussianAdapter.class);
	
	@Override
	public void doInit() {
		// nothing to do.
	}

	/*
	%nprocshared=8
	%nproclinda=1
	%chk=acetaldehyde.chk
	#t opt b3lyp/6-31G(d)

	 acetaldehyde 

	0  1
	C          -4.84400         0.85862         0.09359
	C          -3.63296         1.73879         0.02999
	H          -2.71537         1.26022        -0.34931
	O          -3.63668         2.91717         0.37011
	H          -4.63455         0.01041         0.74987
	H          -5.07570         0.49730        -0.91127
	H          -5.69527         1.41944         0.48426
	*/

	@Override
	public InputInfo calculateAdaption() throws AdapterException {
		Job job = getInfo().getJob();

		Integer mem = job.getEnvironment().getMemoryValue();
		mem = mem - (mem / 3);
		Integer nodes = job.getEnvironment().getNumberOfNodes();
		if (nodes == null)
			nodes = 1;
		Integer cores = job.getEnvironment().getNumberOfCores();
		String loglevel = getParameterFromParameterList("loglevel", true);
		if (loglevel == null || "".equals(loglevel))
			loglevel = "t";
		String chkpoint = getParameterFromParameterList("checkpointfile", true);
		if (chkpoint == null || "".equals(chkpoint))
			chkpoint = job.getId();
		
		String functional = getParameterFromParameterList("dft.functional", true);
		if (functional == null || "".equals(functional))
			functional = getParameterFromParameterList("hf.theory");
		String res = "%mem=" + mem + "mb\n";
		res += "%nprocshared=" + cores + "\n";
		res += "%nproclinda=" + nodes + "\n";
		res += "%chk=" + chkpoint + "\n";
		res += "#" + loglevel + " " + getParameterFromParameterList("jobtype") + " " + functional +
				"/" + getParameterFromParameterList("basisset") + "\n";
		res += "\n" + job.getId() + "\n\n";
		res += getParameterFromParameterList("formal.charge") + " " + getParameterFromParameterList("spin") + "\n"; // formal charge, spin multiplicity
		
		res += GaussianCMLConverter.CML2Gaussian(job.getInitialization().getMolecule());
		res += "\n\n";
		
		InputInfoForInputFile info = new InputInfoForInputFile(res, getInfo());
		return info;
	}
}
