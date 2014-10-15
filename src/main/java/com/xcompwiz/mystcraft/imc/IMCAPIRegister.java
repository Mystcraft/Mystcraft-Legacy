package com.xcompwiz.mystcraft.imc;

import java.lang.reflect.Method;

import com.xcompwiz.mystcraft.api.MystAPI;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCAPIRegister implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isStringMessage()) return;
		LoggerUtils.info(String.format("Receiving registration request from [%s] for method %s", message.getSender(), message.getStringValue()));
		callbackRegistration(message.getStringValue(), message.getSender());
	}

	/**
	 * This is a cool bit of code lifted from WAILA
	 * @author: ProfMobius, Edited by XCompWiz
	 * @param method The method (prefixed by classname) to call
	 * @param modname The name of the mod which made the request
	 */
	public static void callbackRegistration(String method, String modname) {
		String[] splitName = method.split("\\.");
		String methodName = splitName[splitName.length - 1];
		String className = method.substring(0, method.length() - methodName.length() - 1);

		LoggerUtils.info(String.format("Trying to call (reflection) %s %s", className, methodName));

		try {
			Class reflectClass = Class.forName(className);
			Method reflectMethod = reflectClass.getDeclaredMethod(methodName, MystAPI.class);
			reflectMethod.invoke(null, InternalAPI.getAPIInstance(modname));

			LoggerUtils.info(String.format("Success in registering %s", modname));

		} catch (ClassNotFoundException e) {
			LoggerUtils.warn(String.format("Could not find class %s", className));
		} catch (NoSuchMethodException e) {
			LoggerUtils.warn(String.format("Could not find method %s", methodName));
		} catch (Exception e) {
			LoggerUtils.warn(String.format("Exception while trying to access the method : %s", e.toString()));
		}
	}
}
