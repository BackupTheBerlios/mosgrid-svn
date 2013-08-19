package de.ukoeln.msml.genericparser.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;

public class ClassDiscoverer {

	public List<Class<?>> getClassesOfJar(String jarFile) {
		File tmpDir = extractJar(jarFile); // TODO: do not extract jar. use jar-URL.
		if (tmpDir == null) // something went wrong, while unzipping jar file.
			return null;
		
		try {
			return getClassesToPath(tmpDir.getAbsolutePath());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			tmpDir.delete();
		}
		
		return null;
	}
	
	public List<Class<?>> getClassesOfPath(String path) {
		try {
			return getClassesToPath(path);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static File extractJar(String jarFile) {
		String curPath = new File(".").getAbsolutePath();
		File tmpDir = new File(curPath + File.separator + "tmp" + FilenameUtils.removeExtension(new File(jarFile).getName()));

		try {
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry file = (JarEntry) jarEntries.nextElement();
				if (!file.getName().endsWith(".class"))
					continue;
				
				File f = new File(tmpDir + File.separator + file.getName());
				new File(f.getParent()).mkdirs();
				f.createNewFile();
				
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = jar.getInputStream(file); // get the input stream
					fos = new FileOutputStream(f);
					while (is.available() > 0) {  // write contents of 'is' to 'fos'
						fos.write(is.read());
					}					
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fos != null)
						fos.close();
					if (is != null)
						is.close();					
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return tmpDir;
	}
	
	private static List<Class<?>> getClassesToPath(String rootpath)
			throws ClassNotFoundException, IOException {
		
		ClassFileWalker walker = new ClassFileWalker();
		List<File> files = walker.getClassFiles(new File(rootpath));
		
		if (files == null || files.size() == 0)
			return null;

		List<Class<?>> result = new ArrayList<Class<?>>();
		
		URL[] classLoaderURLs = new URL[] {new URL("file:///" + rootpath  + File.separator)};
		ClassLoader classLoader = URLClassLoader.newInstance(classLoaderURLs, Thread.currentThread().getContextClassLoader());
		String location = ClassDiscoverer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		for (File file : files) {
			String className = FilenameUtils.removeExtension(file.getName());
			String packagepath = file.getParent().replace(rootpath + File.separator, "");
			
			String packageName = packagepath.replace(location, "").replace("/", "."); // TODO maybe remove if jar is used
			
			result.add(Class.forName(packageName + "." + className, true, classLoader));
		}
		
		return result;
	}
}
