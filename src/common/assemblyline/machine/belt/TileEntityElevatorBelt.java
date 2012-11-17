package assemblyline.machine.belt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.TileEntity;

/**
 * @author Rseifert
 * 
 */
public class TileEntityElevatorBelt extends TileEntityConveyorBelt
{
	public List<Entity> conveyList = new ArrayList<Entity>();

	public void doBeltAction()
	{
		this.conveyItemsVertical(true, false);
	}

	/**
	 * Used to detect belt bellow for rendering
	 * and to prevent items from falling
	 * 
	 * @return
	 */
	public boolean isBellowABelt()
	{
		TileEntity ent = worldObj.getBlockTileEntity(xCoord, xCoord - 1, zCoord);
		if (ent instanceof TileEntityElevatorBelt) { return true; }
		return false;
	}

	/**
	 * Same as conveyItemHorizontal but will pull,
	 * or lower the items up/down the belt like an
	 * elevator
	 * 
	 * @param extendLife
	 * @param preventPickUp
	 */
	public void conveyItemsVertical(boolean extendLife, boolean preventPickUp)
	{
		// TODO find all Entities in bounds
		// Prevent entities from falling
		// Find if can move up, only a few
		// entities can be moved at a time, 1
		// EntityLiving, or 3 EntityItems
		// ^ has to do with animation why only
		// some not all move
		// move those that can up
		// IF top find belt, and/or throw slightly
		// over the belt and back
	}
}
