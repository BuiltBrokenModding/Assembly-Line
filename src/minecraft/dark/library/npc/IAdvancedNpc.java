package dark.library.npc;

import dark.faction.core.FactionInstance;
import net.minecraft.entity.INpc;

public interface IAdvancedNpc extends INpc
{
	public FactionInstance getFaction();
	
	public String getName();
}
