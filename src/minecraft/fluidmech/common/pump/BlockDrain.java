package fluidmech.common.pump;

import fluidmech.common.TabFluidMech;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.block.BlockAdvanced;

public class BlockDrain extends BlockAdvanced
{

	public BlockDrain(int id)
	{
		super(id, Material.iron);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("lmDrain");
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityDrain();
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		return par1 != 1 && par1 != 0 ? Block.blockGold.getBlockTextureFromSideAndMetadata(par1, par2) : Block.ice.getBlockTextureFromSide(par1);
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (entity instanceof TileEntityDrain)
		{
			
			if(dir == ((TileEntityDrain)entity).getFacing())
			{
				return Block.blockGold.getBlockTextureFromSide(side);
			}
		}
		return Block.ice.getBlockTextureFromSide(side);
	}
	
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			world.setBlockMetadataWithNotify(x, y, z, side, 3);
		}
		return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
	}
}
