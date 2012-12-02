package dark.BasicUtilities.mechanical;

import java.util.ArrayList;

import dark.BasicUtilities.ItemRenderHelper;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockGenerator extends universalelectricity.prefab.BlockMachine {

	public BlockGenerator(int id) {
		super("Generator", id, Material.iron);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving par5EntityLiving) {
		int angle = MathHelper
				.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockAndMetadataWithUpdate(x, y, z, blockID, angle, true);
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z,
			EntityPlayer par5EntityPlayer) {
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
		return ItemRenderHelper.renderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGen();
	}
}
