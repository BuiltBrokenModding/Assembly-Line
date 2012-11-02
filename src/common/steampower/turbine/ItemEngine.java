package steampower.turbine;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import steampower.SteamPowerMain;
import steampower.TileEntityMachine;

public class ItemEngine extends Item {
	public ItemEngine(int par1) {
		super(par1);
		this.maxStackSize = 5;
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setIconIndex(21);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(new ItemStack(this, 1, 0));
	}

	@Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return SteamPowerMain.textureFile + "Items.png";
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer ePlayer,
			World world, int x, int y, int z, int par7, float par8, float par9,
			float par10) {
		int var11 = world.getBlockId(x, y, z);
		int BlockID = SteamPowerMain.EngineID;

		if (var11 == Block.snow.blockID) {
			par7 = 1;
		} else if (var11 != Block.vine.blockID
				&& var11 != Block.tallGrass.blockID
				&& var11 != Block.deadBush.blockID
				&& (Block.blocksList[var11] == null || !Block.blocksList[var11]
						.isBlockReplaceable(world, x, y, z))) {
			if (par7 == 0) {
				--y;
			}

			if (par7 == 1) {
				++y;
			}

			if (par7 == 2) {
				--z;
			}

			if (par7 == 3) {
				++z;
			}

			if (par7 == 4) {
				--x;
			}

			if (par7 == 5) {
				++x;
			}
		}

		if (itemStack.stackSize == 0) {
			return false;
		} else if (!ePlayer.func_82247_a(x, y, z, par7, itemStack)) {
			return false;
		} else if (y == 255
				&& Block.blocksList[BlockID].blockMaterial.isSolid()) {
			return false;
		} else if (world.canPlaceEntityOnSide(BlockID, x, y, z, false, par7,
				ePlayer)) {
			Block var12 = Block.blocksList[BlockID];
			int angle = MathHelper
					.floor_double((ePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			switch (angle) {
			case 0:
				world.setBlockAndMetadata(x, y, z, var12.blockID, 0);
				break;
			case 1:
				world.setBlockAndMetadata(x, y, z, var12.blockID, 1);
				break;
			case 2:
				world.setBlockAndMetadata(x, y, z, var12.blockID, 2);
				break;
			case 3:
				world.setBlockAndMetadata(x, y, z, var12.blockID, 3);
				break;
			}
			int meta = world.getBlockMetadata(x, y, z);
			// ePlayer.sendChatToPlayer("A:"+angle+" M:"+meta);
			world.notifyBlocksOfNeighborChange(x, y, z, var12.blockID);
			world.setBlockAndMetadataWithNotify(x, y + 1, z, var12.blockID, 14);
			world.notifyBlocksOfNeighborChange(x, y, z, var12.blockID);
			world.editingBlocks = false;
			--itemStack.stackSize;

			return true;
		} else {
			return false;
		}
	}
}
