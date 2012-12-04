package de.ukoeln.msml.genericparser.worker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;

public class ClassFileWalker extends DirectoryWalker<File> {

	public ClassFileWalker() {
		super(FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), FileFilterUtils.suffixFileFilter(".class")), 1);
	}
	
	public List<File> getClassFiles(File rootPath) throws IOException {
		List<File> result = new ArrayList<File>();
		walk(rootPath, result);
		return result;
	}

	@Override
	protected void handleFile(File file, int depth, Collection<File> result) {
		result.add(file);
	}
}
