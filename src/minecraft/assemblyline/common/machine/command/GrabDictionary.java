package assemblyline.common.machine.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;

public class GrabDictionary
{

	private static List<GrabDictionary> grabList = new ArrayList<GrabDictionary>();
	private Class<? extends Entity> entityClass;
	private String name;

	public GrabDictionary(String name, Class<? extends Entity> Class)
	{
		this.entityClass = Class;
		this.name = name;
	}

	public static List<GrabDictionary> getList()
	{
		return grabList;
	}

	public static GrabDictionary get(String name)
	{
		for (GrabDictionary ref : grabList)
		{
			if (ref.getName().equalsIgnoreCase(name)) { return ref; }
		}
		return new GrabDictionary("Entity", Entity.class);
	}

	/**
	 * gets the name of the Entity
	 * 
	 * @return "" if null
	 */
	public String getName()
	{
		if (name != null)
			return this.name;
		return "";
	}

	/**
	 * gets the Entity Class
	 * 
	 * @return Entity.class if null
	 */
	public Class<? extends Entity> getEntityClass()
	{
		if (this.entityClass != null)
			return this.entityClass;
		return Entity.class;
	}
	/**
	 * registers an entity by a name to its class allowing it to be 
	 * called by the grab command to be picked up
	 */
	public static void registerGrabableEntity(String name, Class<? extends Entity> eClass)
	{
		GrabDictionary newGrab = new GrabDictionary(name, eClass);
		if (!GrabDictionary.grabList.contains(newGrab))
		{
			for (GrabDictionary ref : grabList)
			{
				if (ref.getName().equalsIgnoreCase(name)) { return; }
			}
			GrabDictionary.getList().add(newGrab);
		}
	}

	public static void registerList()
	{
		registerGrabableEntity("chicken", EntityChicken.class);
	}
}
