package de.ukoeln.msml.genericparser.worker;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class WithBufferedReaderDelegate {

	public abstract String Do(BufferedReader br) throws IOException;

}
