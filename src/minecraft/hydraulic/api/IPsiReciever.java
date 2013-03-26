package hydraulic.api;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

/**
 * A tileEntity that receives a pressure driven fluid. Suggested to use some of the class from
 * net.minecraftforge.liquids too make your machine work with other fluid mods that don't use
 * pressure
 */
public interface IPsiReciever extends IPipeConnection
{
	/**
	 * the load that this machine is handling, working, or moving
	 */
	public double getPressureLoad();

}
