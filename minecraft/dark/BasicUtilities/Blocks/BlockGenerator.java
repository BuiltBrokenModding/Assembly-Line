package dark.BasicUtilities.Blocks;

import java.util.ArrayList;

import universalelectricity.prefab.implement.IRedstoneReceptor;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.BlockRenderHelper;
import dark.BasicUtilities.PipeTab;
import dark.BasicUtilities.Tile.TileEntityEValve;
import dark.BasicUtilities.Tile.TileEntityGen;

public class BlockGenerator extends universalelectricity.prefab.BlockMachine {

	public BlockGenerator(int id) {
		super("Generator", id, Material.iron);
		this.setCreativeTab(PipeTab.INSTANCE);
		this.setHardness(1f);
        this.setResistance(5f);
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}
	 @Override
	    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	    {

	        return new ItemStack(BasicUtilitiesMain.generator,1);        
	    }
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving par5EntityLiving) {
		int angle = MathHelper
				.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockAndMetadataWithUpdate(x, y, z, blockID, angle, true);
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
     {
		int angle = MathHelper
				.floor_double((par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int metadata = par1World.getBlockMetadata(x, y, z);
		if (metadata < 3) {
			par1World.setBlockAndMetadata(x, y, z, blockID, metadata + angle);
		} else {
			par1World.setBlockAndMetadata(x, y, z, blockID, 0);
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderHelper.renderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGen();
	}
	
	 @Override
	    public void onNeighborBlockChange(World par1World, int x, int y, int z, int side)
	    {
	        super.onNeighborBlockChange(par1World, x, y, z, side);
	        this.checkForPower(par1World, x, y, z);
	        
	    }
	    public static void checkForPower(World world, int x, int y, int z)
	    {
	        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

	        if (tileEntity instanceof TileEntityGen)
	        {
	            boolean powered = ((TileEntityGen) tileEntity).isPowered;
	            boolean beingPowered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockGettingPowered(x, y, z);
	            if (powered && !beingPowered)
	            {
	                ((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOff();
	            }
	            else if (!powered && beingPowered)
	            {
	                ((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOn();
	            }
	        }
	    }
}
