package de.ukoeln.msml.genericparser.worker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMatchWalker extends DirectoryWalker<File> {

		private File _root;
		private Pattern _curPattern;
		private Logger LOGGER = LoggerFactory.getLogger(FileMatchWalker.class);
		private String _rootPath;
		
		public FileMatchWalker(String rootPath) {
			super(FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), FileFilterUtils.fileFileFilter()), -1);
			_root = new File(rootPath);
			try {
				_rootPath = _root.getCanonicalPath();
				if (!_rootPath.endsWith(File.separator))
					_rootPath += File.separator;
				_rootPath = Pattern.quote(_rootPath);
			} catch (IOException e) {
				LOGGER.error("Could not make " + _root.getAbsolutePath() + " to a canonical path.");
				return;
			}
			
		}
		
		public List<File> getMatchingFiles(Pattern regex) throws IOException {
			_curPattern = regex;
			List<File> result = new ArrayList<File>();
			walk(_root, result);
			return result;
		}

		@Override
		protected void handleFile(File file, int depth, Collection<File> result) {
			String path = "";
			try {
				path = file.getCanonicalPath();
				path = path.replaceFirst(_rootPath, "");
			} catch (IOException e) {
				LOGGER.error("Could not make " + _root.getAbsolutePath() + " to a canonical path.");
				return;
			}
			Matcher m = _curPattern.matcher(path);
			if (m.matches())
				result.add(file);
		}
}
