package basicpipes.pipes;

import java.util.ArrayList;
import net.minecraft.src.*;

public class ItemGuage extends Item
{
    private int spawnID;

    public ItemGuage(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setIconIndex(10);
        this.setItemName("guage");
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	switch(par1)
    	{
    	case 0: return 11;
    	}
        return this.iconIndex;
    }
    @Override
	 public String getItemName()
    {
        return "guage";
    }
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
    	
                    TileEntity blockEntity = par3World.getBlockTileEntity(par4, par5, par6);
                    if(blockEntity instanceof TileEntityPipe)
                    {
                    	TileEntityPipe pipeEntity = (TileEntityPipe) blockEntity;
                    	int steam = pipeEntity.getStoredLiquid(0);
                    	int type = pipeEntity.getType();
                    	String typeName = getType(type);
                    	par2EntityPlayer.addChatMessage(typeName +" " + steam);
                    	return true;
                    }
                
               
         

        return false;
    }
    public String getType(int type)
    {
    	switch(type)
    	{
    	case 0: return "Steam";
    	case 1: return "Water";
    	case 2: return "Lava";
    	case 3: return "Oil";
    	case 4: return "Fuel";
    	case 5: return "Air";
    	default: return "unknow";
    	}
    }
	public String getItemNameIS(ItemStack par1ItemStack)
    {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {
	   case 1: return "PipeGuage";
	   }
        return this.getItemName();
    }
	@Override
	public String getTextureFile() {
		return "/eui/Items.png";
	}

}
