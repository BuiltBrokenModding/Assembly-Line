package EUI.SteamPower;

import java.util.ArrayList;

import net.minecraft.src.*;

public class ItemCoalFuel extends Item
{
    
	public ItemCoalFuel(int par1)
    {
        super(par1);
        this.setItemName("CoalDust");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	switch(par1)
    	{
    	case 0: return 0;
    	case 1: return 1;
    	case 2: return 2;
    	}
        return this.iconIndex;
    }
	@Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return "/eui/Items.png";
	}
	 public String getItemName()
     {
         return "CoalDust";
     }
     
     

   public String getItemNameIS(ItemStack par1ItemStack)
     {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {
	   case 0: return "CoalNuggets";
	   case 1: return "CoalPellets";
	   case 2: return "CoalDust";
	   }
         return this.getItemName();
     }
     public void addCreativeItems(ArrayList itemList)     {       
         
             itemList.add(new ItemStack(this, 1,0));
             itemList.add(new ItemStack(this, 1,1));
             itemList.add(new ItemStack(this, 1,2));
         
     }
}
