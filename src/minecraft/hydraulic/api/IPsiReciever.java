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
public interface IPsiReciever
{
	/**
	 * Called when this machine receives a fluid at a given pressure. Will be called by the network
	 * before ITankContainer.fill
	 * 
	 * @param pressure - input pressure, fill free to pass a reduced # to another network if you can
	 * (100->[*]->40)
	 * @param stack - fluid received in this event. Try to pass this too another network to simulate
	 * flow rate
	 * @return - how much of the stack was used or passed on.
	 */
	public int onReceiveFluid(double pressure, LiquidStack stack);

	/**
	 * 
	 * @param ent - tileEntity trying to connect to this machine
	 * @param stack - liquid(s) it is most likely going to take or pass. It will pass null if it
	 * doesn't care
	 * @return true if it can connect
	 */
	public boolean canConnect(ForgeDirection dir, TileEntity ent, LiquidStack... stacks);

}
