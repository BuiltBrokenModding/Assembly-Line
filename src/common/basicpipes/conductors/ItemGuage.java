package basicpipes.conductors;

import java.util.ArrayList;
import java.util.List;

import steampower.turbine.TileEntitySteamPiston;

import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.IMechanical;
import basicpipes.pipes.api.Liquid;

import net.minecraft.src.*;
import net.minecraftforge.common.ForgeDirection;

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
        this.setCreativeTab(CreativeTabs.tabTools);
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
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	if(!par3World.isRemote)
    	{
	    	if(itemStack.getItemDamage() == 0)
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
	                    	
	                    	player.addChatMessage(print);
	                    	return true;
	                    }
	                    if(blockEntity instanceof IMechanical)
	                    {
	                    	IMechanical rod = (IMechanical) blockEntity; 
	                    	int steam = rod.getForce();
	                    	int pressure = rod.getAnimationPos();
	                    	String print = "Error";
	                    	
	                    		print = " " + steam +"N "+pressure*45+"degrees";
	                    	
	                    	player.addChatMessage(print);
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
