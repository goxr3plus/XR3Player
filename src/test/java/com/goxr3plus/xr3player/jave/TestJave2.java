package com.goxr3plus.xr3player.jave;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ws.schild.jave.DefaultFFMPEGLocator;

class TestJave2 {

	@DisplayName("Test Jave2 DefaultFFMPEGLocator2")
	@Test
	void testJaveInits() {

//		System.out.println("-------------------RESOURCES-----------" + getBasePathForClass(TestJave2.class));
//		for (File f : getResourceFolderFiles(getBasePathForClass(TestJave2.class).substring(0, getBasePathForClass(TestJave2.class).length() - 1))) {
//			System.out.println(f);
//		}

		new DefaultFFMPEGLocator();
	}

	public URL getResource(String resource){

		URL url ;

		//Try with the Thread Context Loader.
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader != null){
			url = classLoader.getResource(resource);
			if(url != null){
				return url;
			}
		}

		//Let's now try with the classloader that loaded this class.
		classLoader = getClass().getClassLoader();
		if(classLoader != null){
			url = classLoader.getResource(resource);
			if(url != null){
				return url;
			}
		}

		//Last ditch attempt. Get the resource from the classpath.
		return ClassLoader.getSystemResource(resource);
	}

	private static File[] getResourceFolderFiles(String folder) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(folder);
		String path = url.getPath();
		return new File(path).listFiles();
	}

	/**
	 * Returns the absolute path of the current directory in which the given class
	 * file is.
	 *
	 * @param classs * @return The absolute path of the current directory in which
	 * the class file is. <b>[it ends with File.Separator!!]</b>
	 *
	 * @author GOXR3PLUS[StackOverFlow user] + bachden [StackOverFlow user]
	 */
	public static final String getBasePathForClass(final Class<?> classs) {

		// Local variables
		File file;
		String basePath = "";
		boolean failed = false;

		// Let's give a first try
		try {
			file = new File(classs.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			basePath = (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip"))
				? file.getParent()
				: file.getPath();
		} catch (final URISyntaxException ex) {
			failed = true;
			Logger.getLogger(classs.getName()).log(Level.WARNING,
				"Cannot firgue out base path for class with way (1): ", ex);
		}

		// The above failed?
		if (failed)
			try {
				file = new File(classs.getClassLoader().getResource("").toURI().getPath());
				basePath = file.getAbsolutePath();

				// the below is for testing purposes...
				// starts with File.separator?
				// String l = local.replaceFirst("[" + File.separator +
				// "/\\\\]", "")
			} catch (final URISyntaxException ex) {
				Logger.getLogger(classs.getName()).log(Level.WARNING,
					"Cannot firgue out base path for class with way (2): ", ex);
			}

		// fix to run inside Eclipse
		if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin")
			|| basePath.endsWith("bin" + File.separator) || basePath.endsWith("lib" + File.separator)) {
			basePath = basePath.substring(0, basePath.length() - 4);
		}
		// fix to run inside NetBeans
		if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
			basePath = basePath.substring(0, basePath.length() - 14);
		}
		// end fix
		if (!basePath.endsWith(File.separator))
			basePath += File.separator;

		return basePath;
	}

}
