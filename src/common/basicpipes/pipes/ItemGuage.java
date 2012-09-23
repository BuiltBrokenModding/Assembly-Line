package basicpipes.pipes;

import java.util.ArrayList;
import java.util.List;

import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.Liquid;

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
        this.setTabToDisplayOn(CreativeTabs.tabTools);
    }
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	par3List.add(new ItemStack(this, 1, 0));
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	switch(par1)
    	{
    	case 0: return 24;
    	}
        return this.iconIndex;
    }
    public String getTextureFile() {
		return BasicPipesMain.textureFile+"/Items.png";
	}
    @Override
	 public String getItemName()
    {
        return "guage";
    }
    public boolean tryPlaceIntoWorld(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	if(!par3World.isRemote)
    	{
	    	if(par1ItemStack.getItemDamage() == 0)
	    	{
	                    TileEntity blockEntity = par3World.getBlockTileEntity(par4, par5, par6);
	                    if(blockEntity instanceof TileEntityPipe)
	                    {
	                    	TileEntityPipe pipeEntity = (TileEntityPipe) blockEntity;                    	
	                    	Liquid type = pipeEntity.getType();
	                    	int steam = pipeEntity.getStoredLiquid(type);
	                    	int pressure = pipeEntity.presure;
	                    	String typeName = type.lName;
	                    	String print = "Error";
	                    	
	                    		print = typeName +" " + steam +" @ "+pressure+"PSI";
	                    	
	                    	par2EntityPlayer.addChatMessage(print);
	                    	return true;
	                    }
	    	}
               
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
    	default: return "???";
    	}
    }
	public String getItemNameIS(ItemStack par1ItemStack)
    {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {
	   	case 0: return "PipeGuage";
	   }
        return this.getItemName();
    }
}
