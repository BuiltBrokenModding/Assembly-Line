package dark.library.orbit;

import universalelectricity.core.vector.Vector3;

public interface IOrbitingEntity
{
	/**
	 * Gets the current orbit the object is using
	 */
	public OrbitNetworkRing getOrbit();

	/**
	 * Tells this object were it should be in the orbit
	 */
	public void setOrbitWayPoint(Vector3 vec);
}
