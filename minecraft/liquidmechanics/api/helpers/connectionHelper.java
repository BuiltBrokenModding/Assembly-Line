package liquidmechanics.api.helpers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;

public class connectionHelper
{
	/**
	 * Used to find all tileEntities sounding the location you will have to filter for selective
	 * tileEntities
	 * 
	 * @param world - the world being searched threw
	 * @param x
	 * @param y
	 * @param z
	 * @return an array of up to 6 tileEntities
	 */
	public static TileEntity[] getSurroundings(World world, int x, int y, int z)
	{
		TileEntity[] list = new TileEntity[] { null, null, null, null, null, null };
		for (int i = 0; i < 6; i++)
		{
			ForgeDirection d = ForgeDirection.getOrientation(i);
			TileEntity aEntity = world.getBlockTileEntity(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
			if (aEntity instanceof TileEntity)
			{
				list[i] = aEntity;
			}
		}
		return list;
	}	

	/**
	 * Used to find which of 4 Corners this block is in a group of blocks
	 * 0 = not a corner
	 * 1-4 = a corner of some direction
	 */
	public static int corner(TileEntity entity)
	{
		TileEntity[] en = getSurroundings(entity.worldObj, entity.xCoord, entity.yCoord, entity.zCoord);
		if (en[4] != null && en[2] != null && en[5] == null && en[3] == null) { return 3; }
		if (en[2] != null && en[5] != null && en[3] == null && en[4] == null) { return 4; }
		if (en[5] != null && en[3] != null && en[4] == null && en[2] == null) { return 1; }
		if (en[3] != null && en[4] != null && en[2] == null && en[5] == null) { return 2; }

		return 0;

	}
}
