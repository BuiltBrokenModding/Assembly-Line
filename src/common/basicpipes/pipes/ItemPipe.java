package basicpipes.pipes;

import java.util.List;

import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.Liquid;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ItemPipe extends Item
{
	int index = 32;//32 + 4 rows alloted to pipes
    private int spawnID;

    public ItemPipe(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("pipe");
        this.setTabToDisplayOn(CreativeTabs.tabRedstone);
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	
    	return par1+index;
    }
    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return itemstack.getItemDamage() < Liquid.values().length ? Liquid.getLiquid(itemstack.getItemDamage()).lName+" Pipe" :  "Empty Pipe";
    }
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < Liquid.values().length; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
    public String getTextureFile() {
		return BasicPipesMain.textureFile+"/Items.png";
	}
    @Override
	 public String getItemName()
    {
        return "Pipes";
    }
    @Override
    public boolean tryPlaceIntoWorld(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	int blockID = par3World.getBlockId(par4, par5, par6);    	
    	spawnID = BasicPipesMain.pipeID;
    	if (blockID == Block.snow.blockID)
        {
            par7 = 1;
        }
        else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID)
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }
    	
        if (BasicPipesMain.pipe.canPlaceBlockAt(par3World,par4,par5,par6))
        {
            Block var9 = Block.blocksList[this.spawnID];
            par3World.editingBlocks = true;
            if (par3World.setBlockWithNotify(par4, par5, par6, var9.blockID))
            {
                if (par3World.getBlockId(par4, par5, par6) == var9.blockID)
                {
                	
                    Block.blocksList[this.spawnID].onBlockAdded(par3World, par4, par5, par6);
                    Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer);
                    TileEntity blockEntity = par3World.getBlockTileEntity(par4, par5, par6);
                    if(blockEntity instanceof TileEntityPipe)
                    {
                    	TileEntityPipe pipeEntity = (TileEntityPipe) blockEntity;                    	
                    	Liquid dm = Liquid.getLiquid(par1ItemStack.getItemDamage()); 	
                    	pipeEntity.setType(dm);
                    }
                }

                --par1ItemStack.stackSize;
                par3World.editingBlocks = false;
                return true;
            }
        }
        par3World.editingBlocks = false;
        return false;
    }	
	

}