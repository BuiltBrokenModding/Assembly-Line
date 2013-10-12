package basiccomponents.api;

import java.util.HashSet;
import java.util.Set;

/**
 * This should be the only class you include in your mod. If your mod is a coremod, feel free to
 * download Basic Components Core directly during run-time.
 * 
 * @author Calclavia
 */

public class BasicRegistry
{
	public static final Set<String> requests = new HashSet<String>();

	/**
	 * @param request - Name of the item/block to register. Use the EXACT FIELD NAME of the
	 * BasicComponents.java field.
	 */
	public static void register(String request)
	{
		requests.add(request);
	}

	/**
	 * Requests all items in Basic Components.
	 */
	public static void requestAll()
	{
		register("ingotCopper");
		register("ingotTin");

		register("oreCopper");
		register("oreTin");

		register("ingotSteel");
		register("dustSteel");
		register("plateSteel");

		register("ingotBronze");
		register("dustBronze");
		register("plateBronze");

		register("plateCopper");
		register("plateTin");
		register("plateIron");
		register("plateGold");

		register("circuitBasic");
		register("circuitAdvanced");
		register("circuitElite");

		register("motor");
		register("wrench");
	}
}
