package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;

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

	public static GrabDictionary get(Object ob)
	{
		if (ob instanceof String)
		{
			String name = (String) ob;
			for (GrabDictionary ref : grabList)
			{
				if (ref.getName().equalsIgnoreCase(name))
				{
					return ref;
				}
			}
		}
		if (ob instanceof Class)
		{
			Class<? extends Entity> cc = (Class<? extends Entity>) ob;
			for (GrabDictionary ref : grabList)
			{
				if (ref.getEntityClass() == cc)
				{
					return ref;
				}
			}
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
		{
			return this.name;
		}
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
		{
			return this.entityClass;
		}
		return Entity.class;
	}

	/**
	 * registers an entity by a name to its class allowing it to be called by the grab command to be
	 * picked up
	 */
	public static void registerGrabableEntity(String name, Class<? extends Entity> eClass)
	{
		GrabDictionary newGrab = new GrabDictionary(name, eClass);
		if (!GrabDictionary.grabList.contains(newGrab))
		{
			for (GrabDictionary ref : grabList)
			{
				if (ref.getName().equalsIgnoreCase(name))
				{
					return;
				}
			}
			GrabDictionary.getList().add(newGrab);
		}
	}

	static
	{
		registerGrabableEntity("chicken", EntityChicken.class);
		registerGrabableEntity("cow", EntityCow.class);
		registerGrabableEntity("sheep", EntitySheep.class);
		registerGrabableEntity("pig", EntityPig.class);
		registerGrabableEntity("player", EntityPlayer.class);
		registerGrabableEntity("zombie", EntityZombie.class);
		registerGrabableEntity("zomb", EntityZombie.class);
		registerGrabableEntity("skeleton", EntitySkeleton.class);
		registerGrabableEntity("skel", EntitySkeleton.class);
		registerGrabableEntity("animal", EntityAnimal.class);
		registerGrabableEntity("monster", EntityMob.class);
		registerGrabableEntity("mob", EntityMob.class);
		registerGrabableEntity("creeper", EntityCreeper.class);
		registerGrabableEntity("spider", EntitySpider.class);
		registerGrabableEntity("slime", EntitySlime.class);
		registerGrabableEntity("items", EntityItem.class);
		registerGrabableEntity("all", Entity.class);
		registerGrabableEntity("everything", Entity.class);
		registerGrabableEntity("boat", EntityBoat.class);
		registerGrabableEntity("cart", EntityMinecart.class);
	}
}
