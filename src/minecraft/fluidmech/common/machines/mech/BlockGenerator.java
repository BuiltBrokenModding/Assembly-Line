package fluidmech.common.machines.mech;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;
import fluidmech.client.render.BlockRenderHelper;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;

public class BlockGenerator extends BlockAdvanced
{

	public BlockGenerator(int id)
	{
		super(id, Material.iron);
		this.setUnlocalizedName("lmGen");
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setHardness(1f);
		this.setResistance(5f);
	}

	@Override
	public void addCreativeItems(ArrayList itemList)
	{
		itemList.add(new ItemStack(this, 1, 0));
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{

		return new ItemStack(FluidMech.blockGenerator, 1);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack stack)
	{
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockAndMetadataWithNotify(x, y, z, blockID, angle, 3);
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		int angle = MathHelper.floor_double((par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int metadata = par1World.getBlockMetadata(x, y, z);
		if (metadata < 3)
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata + angle, 3);
		}
		else
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, blockID, 0, 3);
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return BlockRenderHelper.renderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityGenerator();
	}

	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int side)
	{
		super.onNeighborBlockChange(par1World, x, y, z, side);

	}
}
