package dark.BasicUtilities.Liquids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import universalelectricity.core.vector.Vector3;

/**
 * The still version of oil.
 * 
 * @author Cammygames
 * 
 */
public class BlockOilStill extends BlockStationary implements ILiquid
{
	public BlockOilStill(int id)
	{
		super(id, Material.water);
		this.setHardness(80F);
		this.setLightOpacity(0);
		this.setRequiresSelfNotify();
		this.disableStats();
		this.setBlockName("oilStill");
	}

	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		super.onBlockAdded(par1World, x, y, z);

		for (byte i = 0; i < 6; i++)
		{
			Vector3 neighborPosition = new Vector3(x, y, z);
			neighborPosition.modifyPositionFromSide(ForgeDirection.getOrientation(i));

			int neighborBlockID = par1World.getBlockId(neighborPosition.intX(), neighborPosition.intY(), neighborPosition.intZ());

			if (neighborBlockID == Block.fire.blockID || neighborBlockID == Block.lavaMoving.blockID || neighborBlockID == Block.lavaStill.blockID)
			{
				par1World.setBlockWithNotify(x, y, z, Block.fire.blockID);
				par1World.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);
				par1World.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
				return;
			}
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
	 * (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int neighborBlockID)
	{
		super.onNeighborBlockChange(par1World, x, y, z, neighborBlockID);

		if (par1World.getBlockId(x, y, z) == this.blockID)
		{
			this.setNotStationary(par1World, x, y, z);
		}
		else if (neighborBlockID == Block.fire.blockID || neighborBlockID == Block.lavaMoving.blockID || neighborBlockID == Block.lavaStill.blockID)
		{
			par1World.setBlockWithNotify(x, y, z, Block.fire.blockID);
			par1World.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);
			par1World.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Changes the block ID to that of an updating fluid.
	 */
	private void setNotStationary(World par1World, int par2, int par3, int par4)
	{
		int var5 = par1World.getBlockMetadata(par2, par3, par4);
		par1World.editingBlocks = true;
		par1World.setBlockAndMetadata(par2, par3, par4, this.blockID - 1, var5);
		par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
		par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID - 1, this.tickRate());
		par1World.editingBlocks = false;
	}

	/**
	 * Checks to see if the block is flammable.
	 */
	private boolean isFlammable(World par1World, int par2, int par3, int par4)
	{
		return par1World.getBlockMaterial(par2, par3, par4).getCanBurn();
	}

	@Override
	public int stillLiquidId()
	{
		return this.blockID;
	}

	@Override
	public boolean isMetaSensitive()
	{
		return false;
	}

	@Override
	public int stillLiquidMeta()
	{
		return 0;
	}

	@Override
	public int getRenderBlockPass()
	{
		return 0;
	}

	@Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		// TODO fix this so your oil is not so
		// dark
		return 0x11111110;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world,
	 * x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, int x, int y, int z, Entity par5Entity)
	{
		if (par5Entity instanceof EntityLiving)
		{
			if (par5Entity.isInsideOfMaterial(this.blockMaterial))
			{
				((EntityLiving) par5Entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 20, 2));
			}
		}
	}
}
