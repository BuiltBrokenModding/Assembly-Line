package fluidmech.common.pump;

import hydraulic.helpers.MetaGroup;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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

public class BlockConstructionPump extends BlockAdvanced
{

	public BlockConstructionPump(int id)
	{
		super(id, Material.iron);
		this.setUnlocalizedName("lmConPump");
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setHardness(1f);
		this.setResistance(5f);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}

	@Override
	public int damageDropped(int meta)
	{
		return 0;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return new ItemStack(FluidMech.blockConPump, 1, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving p, ItemStack itemStack)
	{
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityConstructionPump();
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
	}

	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}
}
