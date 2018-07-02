package main.java.com.goxr3plus.xr3player.speechrecognition;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Modify Enviromental variables
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class SetEnv {
	
	public static void setEnv(Map<String,String> newenv) throws ClassNotFoundException , IllegalAccessException , NoSuchFieldException {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			
			//Field 1
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			
			//env
			@SuppressWarnings("unchecked")
			Map<String,String> env = (Map<String,String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			
			//Field2
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			
			//cienv
			@SuppressWarnings("unchecked")
			Map<String,String> cienv = (Map<String,String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
			
		} catch (NoSuchFieldException e) {
			
			//Env
			Map<String,String> env = System.getenv();
			
			//Foe each
			Arrays.asList(Collections.class.getDeclaredClasses()).forEach(cl -> {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					try {
						Field field = cl.getDeclaredField("m");
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						Map<String,String> map = (Map<String,String>) field.get(env);
						map.clear();
						map.putAll(newenv);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}
			});
		}
	}
}
