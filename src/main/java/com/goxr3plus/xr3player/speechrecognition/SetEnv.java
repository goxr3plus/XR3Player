package main.java.com.goxr3plus.xr3player.speechrecognition;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/** Modify Enviromental variables
 * @author GOXR3PLUSSTUDIO
 *
 */
public class SetEnv {
	
	public static void setEnv(Map<String,String> newenv) throws ClassNotFoundException , IllegalAccessException , NoSuchFieldException {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String,String> env = (Map<String,String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String,String> cienv = (Map<String,String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String,String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String,String> map = (Map<String,String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}
	}
}
