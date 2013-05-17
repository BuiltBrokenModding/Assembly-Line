package dark.library.npc;

import dark.library.team.FactionInstance;
import net.minecraft.entity.INpc;

public interface IAdvancedNpc extends INpc
{
	public FactionInstance getFaction();
	
	public String getName();
}
