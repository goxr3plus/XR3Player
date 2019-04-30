package com.goxr3plus.xr3player.utils.general;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Modify Computer Environment variables
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class EnvVarsTool {

	public static void setEnv(final Map<String, String> newenv)
			throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
		try {
			final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");

			// Field 1
			final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);

			// env
			@SuppressWarnings("unchecked")
			final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newenv);

			// Field2
			final Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);

			// cienv
			@SuppressWarnings("unchecked")
			final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);

		} catch (final NoSuchFieldException e) {

			// Env
			final Map<String, String> env = System.getenv();

			// Foe each
			Arrays.asList(Collections.class.getDeclaredClasses()).forEach(cl -> {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					try {
						final Field field = cl.getDeclaredField("m");
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						final Map<String, String> map = (Map<String, String>) field.get(env);
						map.clear();
						map.putAll(newenv);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}

				}
			});
		}
	}
}
