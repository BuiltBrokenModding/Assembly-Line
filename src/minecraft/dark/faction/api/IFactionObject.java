package dark.faction.api;

import dark.faction.core.FactionInstance;

public interface IFactionObject
{
	/**
	 * Gets the faction this is linked too. Will return Neutral rather than null
	 */
	public FactionInstance getFactinon();

	public boolean setFaction(FactionInstance faction, boolean override);
}
