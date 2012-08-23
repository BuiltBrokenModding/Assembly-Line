package EUI.SteamPower;

import java.util.ArrayList;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class ItemParts extends Item{

	public ItemParts(int par1)
    {
        super(par1);
        this.setItemName("Parts");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	switch(par1)
    	{
    	case 0: return 3;
    	case 1: return 4;
    	case 2: return 5;
    	case 3: return 6;
    	case 4: return 7;
    	case 5: return 8;
    	case 6: return 9;
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
         return "parts";
     }
     
     

   public String getItemNameIS(ItemStack par1ItemStack)
     {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {	   
	   case 1: return "Tank";
	   case 3: return "Valve";
	   case 4: return "Tube";
	   case 5: return "Seal";
	   case 6: return "Rivits";
	   }
         return this.getItemName();
     }
     public void addCreativeItems(ArrayList itemList)
     { 
             itemList.add(new ItemStack(this, 1,1));
             itemList.add(new ItemStack(this, 1,3));
             itemList.add(new ItemStack(this, 1,4));
             itemList.add(new ItemStack(this, 1,5));
             itemList.add(new ItemStack(this, 1,6));
         
     }
}



