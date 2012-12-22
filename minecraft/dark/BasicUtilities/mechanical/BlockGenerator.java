package dark.BasicUtilities.mechanical;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import dark.BasicUtilities.ItemRenderHelper;

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
		return ItemRenderHelper.renderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGen();
	}
}
