package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;

import de.ukoeln.msml.genericparser.DictionaryDocument;
import de.ukoeln.msml.genericparser.worker.StringH;

public class ResourcesHelper {


	
	public static List<String> getResourceListing(Class<?> clazz, String path, String extension) throws URISyntaxException, IOException {
		List<String> filteredResult = new ArrayList<String>();
		String[] reses = doGetResourceListing(clazz, path, extension);
		for (String res : reses) {
			String ext = FilenameUtils.getExtension(res);
			if (!StringH.isNullOrEmpty(ext) && ext.equals(extension))
				filteredResult.add(res);
		}
		return filteredResult;
	}
	


	/**
	 * List directory contents for a resource folder. Not recursive. This is
	 * basically a brute-force implementation. Works for regular files and also
	 * JARs.
	 * 
	 * @author Greg Briggs
	 * @param clazz
	 *            Any java class that lives in the same place as the resources
	 *            you want.
	 * @param path
	 *            Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private static String[] doGetResourceListing(Class<?> clazz, String path, String extension) throws URISyntaxException, IOException {
		Set<String> result = new HashSet<String>();

		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			String[] childs = new File(dirURL.toURI()).list();
			for (String child : childs) {
				File childFile = new File(new URL(dirURL + child).toURI());
				if (!childFile.isDirectory()) {
					result.add(path + child);
					continue;
				}
				List<String> childChilds = getResourceListing(DictionaryDocument.class, path + child + "/", extension);
				for (String childChild : childChilds)
					result.add(childChild);
			}
			return result.toArray(new String[result.size()]);
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			// strip out only the jar file
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			// all entries in the jar
			Enumeration<JarEntry> entries = jar.entries();
			
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path) && !name.equals(path)) { // filter according to the path but leave out path itself
					result.add(name);
				}
			}
			return result.toArray(new String[result.size()]);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}

	public static InputStream getStream(String name) {
		return ResourcesHelper.class.getClassLoader().getResourceAsStream(name);
	}
}
