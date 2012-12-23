package dark.SteamPower.steamengine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import dark.SteamPower.SteamPowerMain;

public class ItemSteamPiston extends Item {
	public ItemSteamPiston(int par1) {
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

	 public boolean onItemUse(ItemStack stak, EntityPlayer player, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
	    {
	        if (par7 != 1)
	        {
	            return false;
	        }
	        else
	        {
	            ++y;
	            Block piston = SteamPowerMain.piston;

	            if (player.canPlayerEdit(x, y, z, par7, stak) && player.canPlayerEdit(x, y + 1, z, par7, stak))
	            {
	                if (!piston.canPlaceBlockAt(par3World, x, y, z))
	                {
	                    return false;
	                }
	                else
	                {
	                    int angle = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
	                    placeTwoHigh(par3World, x, y, z, angle, piston);
	                    --stak.stackSize;
	                    return true;
	                }
	            }
	            else
	            {
	                return false;
	            }
	        }
	    }

	    public static void placeTwoHigh(World par0World, int par1, int par2, int par3, int angle, Block par5Block)
	    {
	        par0World.editingBlocks = true;
	        par0World.setBlockAndMetadataWithNotify(par1, par2+1, par3, par5Block.blockID, 14);
	        par0World.setBlockAndMetadataWithNotify(par1, par2, par3, par5Block.blockID, angle);
	        par0World.editingBlocks = false;
	        par0World.notifyBlocksOfNeighborChange(par1, par2, par3, par5Block.blockID);
	        par0World.notifyBlocksOfNeighborChange(par1, par2 + 1, par3, par5Block.blockID);
	    }
			
}
