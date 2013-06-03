package dark.fluid.common.machines.liquids;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

/**
 * Block prefab for gas that will spread and decay in the world as time goes on. Useful for filling
 * the world with an air like gas that poisons or changes the color
 */
public class BlockGas extends Block
{
	private boolean canDecay = true;

	public BlockGas(String name, int id)
	{
		super(id, Material.air);
		this.setUnlocalizedName(name);
		this.setTickRandomly(true);
	}

	/**
	 * Sets the blocks decay flag
	 */
	public void setDecay(boolean bool)
	{
		this.canDecay = bool;
	}

	/**
	 * Can this block decay over time
	 */
	public boolean canDecay()
	{
		return this.canDecay;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote && world.getBlockId(x, y, z) == this.blockID)
		{
			Vector3 currentPos = new Vector3(x, y, z);
			int meta = currentPos.getBlockMetadata(world);

			if (meta-- <= 0)
			{
				world.setBlock(x, y, z, 0);
			}
			else
			{
				currentPos.setBlock(world, this.blockID, meta--);
				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					Vector3 pos = currentPos.clone().modifyPositionFromSide(direction);
					Block block = Block.blocksList[pos.getBlockID(world)];
					if (block != null && block.isBlockReplaceable(world, pos.intX(), pos.intY(), pos.intZ()))
					{
						pos.setBlock(world, this.blockID, meta--);
					}					
				}
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		// Suggest using this if you want the gas to harm the play when he enters it
	}

	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}

	@Override
	public boolean isBlockReplaceable(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return 0;
	}

	@Override
	public boolean isAirBlock(World world, int x, int y, int z)
	{
		return false;
	}

}
