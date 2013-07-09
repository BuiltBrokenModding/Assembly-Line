package dark.fluid.common.pump;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import dark.fluid.common.FluidMech;
import dark.fluid.common.TabFluidMech;
import dark.library.machine.BlockMachine;

public class BlockDrain extends BlockMachine
{
	private Icon blockIcon;
	private Icon drainIcon;
	private Icon fillIcon;

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

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "ironMachineSide");
		this.drainIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "drain");
		this.fillIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "drain2");
	}

	@Override
	public Icon getIcon(int par1, int par2)
	{
		return par1 != 1 && par1 != 0 ? this.blockIcon : this.drainIcon;
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (entity instanceof TileEntityDrain)
		{

			if (dir == ((TileEntityDrain) entity).getFacing())
			{
				if (((TileEntityDrain) entity).canDrainSources())
				{
					return this.drainIcon;
				}
				else
				{
					return this.fillIcon;
				}

			}
		}
		return this.blockIcon;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack itemStack)
	{
		int angle = MathHelper.floor_double((p.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, angle, 3);
		TileEntity entity = world.getBlockTileEntity(x, y, z);
	}

	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			int meta = world.getBlockMetadata(x, y, z);
			if (world.getBlockMetadata(x, y, z) < 6)
			{
				meta += 6;
			}
			else
			{
				meta -= 6;
			}
			world.setBlockMetadataWithNotify(x, y, z, meta, 3);
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity instanceof TileEntityDrain)
			{
				entityPlayer.sendChatToPlayer("Draining Sources? " + ((TileEntityDrain) entity).canDrainSources());

			}
			return true;
		}
		return true;
	}

	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			int meta = side;
			if (world.getBlockMetadata(x, y, z) > 5)
			{
				meta += 6;
			}
			world.setBlockMetadataWithNotify(x, y, z, meta, 3);
			return true;
		}
		return true;
	}

}
