package dark.core.faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dark.core.api.faction.IFactionObject;

public class FactionManager
{

	/* LIST OF FACTION CURRENTLY OR RECNETLY LOADED */
	public static List<FactionInstance> cachedFactions = new ArrayList<FactionInstance>();
	/* LIST OF CURRENTLY OR RECENTLY LOADED PLAYERS AND THERE FACTIONS */
	public static HashMap<String, String> cachedAllegance = new HashMap<String, String>();

	public static FactionInstance NEUTRIAL = new FactionInstance("NAN", "Neutrial", "World", null);
	public static FactionInstance GUARDSMAN = new FactionInstance("GSM", "GUARDSMAN", "DARKGUARDSMAN", null);
	public static FactionInstance DARK = new FactionInstance("DARK", "DARK", "DARKGUARDSMAN", null);

	public boolean isPartOfFaction(Object entity, FactionInstance faction)
	{
		if (faction == null || entity == null)
		{
			return false;
		}
		if (entity instanceof IFactionObject)
		{
			return faction.isEqual(((IFactionObject) entity).getFaction());
		}
		return false;
	}

	public static FactionInstance loadFaction(String name)
	{
		if (name != null && !name.isEmpty())
		{
			
		}
		return NEUTRIAL;

	}

	public static void saveFaction(FactionInstance faction)
	{

	}
}
