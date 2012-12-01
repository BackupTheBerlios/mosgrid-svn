package de.mosgrid.chemdoodle.converter.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.mosgrid.chemdoodle.converter.JsonConversionException;
import de.mosgrid.chemdoodle.converter.pdb.PDB2JSONConverter;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File pdbFile = new File("/home/Andi/Dokumente/Hiwi/ChemDoodle/pdb/101M_post.pdb");
		PDB2JSONConverter c = new PDB2JSONConverter();
		try {
			String json = c.convert(new FileReader(pdbFile), null);
			System.out.println(json);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
