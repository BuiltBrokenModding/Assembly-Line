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

public class ItemEngine extends Item
{
    public ItemEngine(int par1)
    {
        super(par1);
        this.maxStackSize = 5;
        this.setCreativeTab(CreativeTabs.tabBlock);
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
		return SteamPowerMain.textureFile+"Items.png";
	}
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer ePlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if (world.isRemote)
        {
            return false;
        }
    	
    	Block var11 = SteamPowerMain.engine;
    	int angle = MathHelper.floor_double((ePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3; 
    	
        	if (ePlayer.canPlayerEdit(x, y, z))
        	{
            ++y;
                if (var11.canPlaceBlockAt(world, x, y, z))
                {
                	world.editingBlocks = true;
                     
             	        switch (angle)
             	        {
             	        	case 0: world.setBlockAndMetadata(x, y, z, var11.blockID, 0); break;
             	        	case 1: world.setBlockAndMetadata(x, y, z, var11.blockID, 1); break;
             	        	case 2: world.setBlockAndMetadata(x, y, z, var11.blockID, 2); break;
             	        	case 3: world.setBlockAndMetadata(x, y, z, var11.blockID, 3); break;
             	        }
             	        int meta = world.getBlockMetadata(x, y, z);
             	        //ePlayer.sendChatToPlayer("A:"+angle+" M:"+meta);
                	world.notifyBlocksOfNeighborChange(x, y, z, var11.blockID);
                	world.setBlockAndMetadataWithNotify(x, y+1, z, var11.blockID, 14);
                	world.notifyBlocksOfNeighborChange(x, y, z, var11.blockID);
                	world.editingBlocks = false;
                    --itemStack.stackSize;
                    return true;
                }
            }
        
    	return false;
    }
}
