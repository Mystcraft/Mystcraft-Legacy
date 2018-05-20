package com.xcompwiz.mystcraft.api;

import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;

/**
 * The purpose of this interface is simply to provide instances of the API interfaces. It is possible to request an instance via the static method, but you
 * should first make sure Mystcraft is loaded. This interface supports providing multiple versions of an API, so you can request ex. 'someAPI-1' and always get
 * the same interface, enabling the API to move forward without breaking compatibility. The APIInstanceProvider instance returned, as well as any API interface
 * instances created by it, will belong to the mod which requested the provider. To see what API interfaces are available, see the hook package.
 * @author xcompwiz
 */
public interface APIInstanceProvider {

	/**
	 * Returns a constructed version of the requested API. If the API requested doesn't exist then an exception will be thrown. Be wary when attempting to cast
	 * the returned instance, as, if you try using an interface not included in the class path, you may get missing definition crashes. It is wiser to, after
	 * verifying success, pass the object to another function which can handle the casting.
	 * @param api The name of the API and version desired, formatted as ex. 'symbol-1'.
	 * @return The requested API instance as an Object. If you get an object, it is guaranteed to be of the requested API and version.
	 */
	public Object getAPIInstance(String api) throws APIUndefined, APIVersionUndefined, APIVersionRemoved;

	public static APIInstanceProvider getProviderInstance() {
		return com.xcompwiz.mystcraft.api.impl.InternalAPI.getAPIProviderInstance();
	}

//@formatter:off
	/*	Example Usage
 	public static void doMystcraftIntegration() {
 		if (!Loader.isModLoaded("Mystcraft")) //Note, if the Mystcraft API package isn't included in your packaged code, you'll need to make this check before calling this function
 			return;

 		APIInstanceProvider provider = APIInstanceProvider.getProviderInstance();
 		if (!provider)
			return;
 		
		try {
			Object apiinst = provider.getAPIInstance("awesomeAPI-3");
			useAPI(apiinst); //At this point, we've got an object of the right interface.
		} catch (APIUndefined e) {
			// The API we requested doesn't exist.  Give up with a nice log message.
		} catch (APIVersionUndefined e) {
			// The API we requested exists, but the version we wanted is missing in the local environment. We can try falling back to an older version.
		} catch (APIVersionRemoved e) {
			// The API we requested exists, but the version we wanted has been removed and is no longer supported. Better update.
		}
	}

	// alternate way, should you want to ensure all of the integration can occur properly
 	public static void doMoreComplexMystcraftIntegration() {
 		if (!Loader.isModLoaded("Mystcraft")) //Note, if the Mystcraft API package isn't included in your packaged code, you'll need to make this check before calling this function
 			return;

 		APIInstanceProvider provider = APIInstanceProvider.getProviderInstance();
 		if (!provider)
			return;
 		
 		Object api1 = helperGetAPI(provider, "awesomeAPI-3");
 		Object api2 = helperGetAPI(provider, "otherAPI-1");
 		
 		if (api1 == null || api2 == null)
 			return; //
 		doActualIntegration(api1, api2);
	}
	*/
//@formatter:on
}
