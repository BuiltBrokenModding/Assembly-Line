package net.minecraft.src.eui.pipes;

import java.util.ArrayList;

import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class ItemPipe extends Item implements ITextureProvider
{
    private int spawnID;

    public ItemPipe(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("pipe");
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	switch(par1)
    	{
    	case 0: return 11;
    	case 1: return 10;
    	case 2: return 11;
    	case 3: return 10;
    	case 4: return 11;
    	case 5: return 10;
    	}
        return this.iconIndex;
    }
    @Override
	 public String getItemName()
    {
        return "Pipes";
    }
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
    	int blockID = par3World.getBlockId(par4, par5, par6);    	
    	spawnID = mod_BasicPipes.pipeID;
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
    	
        if (par3World.canBlockBePlacedAt(this.spawnID, par4, par5, par6, false, par7))
        {
            Block var9 = Block.blocksList[this.spawnID];

            if (par3World.setBlockWithNotify(par4, par5, par6, this.spawnID))
            {
                if (par3World.getBlockId(par4, par5, par6) == this.spawnID)
                {
                    Block.blocksList[this.spawnID].onBlockPlaced(par3World, par4, par5, par6, par7);
                    Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer);
                    TileEntity blockEntity = par3World.getBlockTileEntity(par4, par5, par6);
                    if(blockEntity instanceof TileEntityPipe)
                    {
                    	TileEntityPipe pipeEntity = (TileEntityPipe) blockEntity;                    	
                    	int dm = par1ItemStack.getItemDamage();
                    	String rType = "air";                    	
                    	pipeEntity.setType(dm);
                    }
                }

                --par1ItemStack.stackSize;
                
                return true;
            }
        }

        return false;
    }
	public String getItemNameIS(ItemStack par1ItemStack)
    {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {
	   case 0: return "steamPipe";
	   case 1: return "waterPipe";	   
	   case 2: return "lavaPipe";
	   case 3: return "oilPipe";
	   case 4: return "fuelPipe";
	   case 5: return "airPipe";
	   default: return "Pipe";
	   }
    }
	@Override
	public String getTextureFile() {
		return "/eui/Items.png";
	}
	@Override
	public void addCreativeItems(ArrayList itemList)     {       
        
        itemList.add(new ItemStack(this, 1,0));
        itemList.add(new ItemStack(this, 1,1));
        itemList.add(new ItemStack(this, 1,2));
        itemList.add(new ItemStack(this, 1,3));
        itemList.add(new ItemStack(this, 1,4));
        itemList.add(new ItemStack(this, 1,5));
}

}
