package assemblyline.api;

import java.util.List;

import net.minecraft.entity.Entity;

/**
 * An interface applied to Armbots.
 * 
 * @author Calclavia
 */
public interface IArmbot
{
	public List<Entity> getGrabbedEntities();
}
