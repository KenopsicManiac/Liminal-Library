package org.dimdev.limlib.impl.bridge;

import org.dimdev.limlib.impl.Limlib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IrisBridge {

	public static final boolean IRIS_LOADED = Limlib.getSided().isModLoaded("iris");

	public static boolean areShadersInUse() {

		if (IRIS_LOADED) {

			try {
				Class<?> irisApi = Class.forName("net.irisshaders.iris.apiimpl.IrisApiV0Impl");
				Field irisInstance = irisApi.getField("INSTANCE");
				Method isShaderInUse = irisApi.getMethod("isShaderPackInUse", new Class[0]);
				Object areThey = isShaderInUse.invoke(irisInstance.get(null), new Object[0]);

				if (areThey instanceof Boolean depends) {
					return depends;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		return false;
	}

}
