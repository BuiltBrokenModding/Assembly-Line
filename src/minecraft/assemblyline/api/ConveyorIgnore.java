package assemblyline.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;

/**
 * A class that allows you to ignore specific entities on the Conveyor Belt.
 * 
 * @author Calclavia
 * 
 */
public class ConveyorIgnore
{
	private static final List<Entity> entityIgnoreList = new ArrayList<Entity>();

	public static void ignore(Entity entity)
	{
		if (!entityIgnoreList.contains(entity))
		{
			entityIgnoreList.add(entity);
		}
	}

	public static boolean isIgnore(Entity entity)
	{
		return entityIgnoreList.contains(entity);
	}
}
