package SteamPower.turbine;
import java.util.List;

import SteamPower.SteamPowerMain;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class ItemEngine extends Item
{
    public ItemEngine(int par1)
    {
        super(par1);
        this.maxStackSize = 5;
        this.setTabToDisplayOn(CreativeTabs.tabBlock);
        this.setIconIndex(21);
    }
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		par3List.add(new ItemStack(this, 1, 0));
    }
    @Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return "/EUIClient/Textures/Items.png";
	}
    @Override
    public boolean tryPlaceIntoWorld(ItemStack par1ItemStack, EntityPlayer ePlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	if (par3World.isRemote)
        {
            return false;
        }
    	
    	Block var11 = SteamPowerMain.engine;
    	int angle = MathHelper.floor_double((ePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3; 
    	
        	if (ePlayer.canPlayerEdit(par4, par5, par6))
        	{
            ++par5;
                if (var11.canPlaceBlockAt(par3World, par4, par5, par6))
                {
                	par3World.editingBlocks = true;
                	par3World.setBlockAndMetadataWithNotify(par4, par5, par6, var11.blockID, 1);
                	par3World.notifyBlocksOfNeighborChange(par4, par5, par6, var11.blockID);
                	par3World.setBlockAndMetadataWithNotify(par4, par5+1, par6, var11.blockID, 14);
                	par3World.notifyBlocksOfNeighborChange(par4, par5, par6, var11.blockID);
                	ePlayer.sendChatToPlayer(""+par3World.getBlockMetadata(par4, par5, par6));
                	par3World.editingBlocks = false;
                    --par1ItemStack.stackSize;
                    return true;
                }
            }
        
    	return false;
    }
}
