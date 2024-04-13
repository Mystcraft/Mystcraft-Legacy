package com.xcompwiz.mystcraft.imc;

import java.lang.reflect.Method;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCAPIRegister implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isStringMessage()) return;
		LoggerUtils.info(String.format("Receiving API registration request from [%s] for method %s", message.getSender(), message.getStringValue()));
		callbackRegistration(message.getStringValue(), message.getSender());
	}

	/**
	 * This is a cool bit of code lifted and modified from WAILA
	 * @author: ProfMobius, XCompWiz
	 * @param method The method (prefixed by classname) to call
	 * @param modname The name of the mod which made the request
	 */
	public static void callbackRegistration(String method, String modname) {
		String[] splitName = method.split("\\.");
		String methodName = splitName[splitName.length - 1];
		String className = method.substring(0, method.length() - methodName.length() - 1);

		APIInstanceProvider providerinst = InternalAPI.getAPIProviderInstance(modname);
		if (providerinst == null) {
			LoggerUtils.error(String.format("Could not initialize API provider instance for %s", modname));
			return;
		}

		LoggerUtils.info(String.format("Trying to call (reflection) %s %s", className, methodName));

		try {
			Class reflectClass = Class.forName(className);
			Method reflectMethod = reflectClass.getDeclaredMethod(methodName, APIInstanceProvider.class);
			reflectMethod.invoke(null, providerinst);
			LoggerUtils.info(String.format("API provided to %s", modname));
		} catch (ClassNotFoundException e) {
			LoggerUtils.error(String.format("Could not find class %s", className));
		} catch (NoSuchMethodException e) {
			LoggerUtils.error(String.format("Could not find method %s", methodName));
		} catch (Exception e) {
			LoggerUtils.error(String.format("Exception while calling the method %s.%s", className, methodName));
			e.printStackTrace();
		}
	}
}
