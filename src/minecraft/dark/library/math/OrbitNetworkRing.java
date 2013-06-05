package dark.library.math;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import universalelectricity.core.vector.Vector3;

public class OrbitNetworkRing
{
	float orbitRadius;
	Vector3 rotationChange = new Vector3(0, 0, 0);

	HashMap<Entity, Integer> orbitMemeber = new HashMap<Entity, Integer>();

	public OrbitNetworkRing(HashMap<Entity, Integer> entities)
	{
		if (entities != null)
		{
			this.orbitMemeber.putAll(entities);
		}
	}

	public HashMap<Entity, Integer> getOrbitMemebers()
	{
		if (this.orbitMemeber == null)
		{
			this.orbitMemeber = new HashMap<Entity, Integer>();
		}
		return this.orbitMemeber;
	}

	/**
	 * Increase the rotation angles of the orbitRing
	 * 
	 * @param vec - rotation change stored as a vector3
	 * @param increase - add the vec rotation to current rotation
	 */
	public void changeRotation(Vector3 vec, boolean increase)
	{
		Vector3 preRotation = rotationChange.clone();
		this.rotationChange = vec;
		if (increase)
		{
			this.rotationChange.add(preRotation);
		}
	}

	/**
	 * Get the rotation change of the orbit
	 */
	public Vector3 getRotation()
	{
		if (this.rotationChange == null)
		{
			this.rotationChange = new Vector3(0, 0, 0);
		}
		return this.rotationChange;
	}

	/**
	 * Ideal minimal radius needed for the number of objects
	 */
	public float getMinRadius()
	{
		float width = 0;
		Iterator<Entry<Entity, Integer>> it = this.getOrbitMemebers().entrySet().iterator();
		while (it.hasNext())
		{
			Entity entity = it.next().getKey();
			width += entity.width;
		}
		width = width / this.getOrbitMemebers().size();
		return ((width + (width / 2)) * this.getOrbitMemebers().size());
	}

	/**
	 * 
	 * @param objectSize - side of the object in the direction it will orbit
	 * @param radIncrase - increase in radius size
	 * @param objects - number of the objects in the orbit
	 * @param pos - position in the orbit
	 * @return offset distance from orbit center
	 * 
	 * Note this only gives the offset from the orbit point. This needs to be used to in combo with
	 * something else to get the orbit point. Then add the result of this to get the pos of the
	 * object
	 */
	public Vector3 getOrbitOffset(int pos)
	{
		float minRadius = this.getMinRadius();
		if (this.orbitRadius < minRadius)
		{
			this.orbitRadius = minRadius;
		}
		float spacing = this.orbitRadius / this.getOrbitMemebers().size();

		double x = this.orbitRadius * Math.cos((spacing * pos) + this.getRotation().y);
		double z = this.orbitRadius * Math.sin((spacing * pos) + this.getRotation().y);
		return null;
	}
}
