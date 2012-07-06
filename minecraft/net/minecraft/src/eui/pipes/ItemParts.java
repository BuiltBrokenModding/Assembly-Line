package net.minecraft.src.eui.pipes;

import java.util.ArrayList;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.forge.ITextureProvider;

public class ItemParts extends Item implements ITextureProvider{

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
    	case 0: return 7;
    	case 1: return 7;
    	case 2: return 8;
    	}
        return this.iconIndex;
    }
	@Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return "/eui/Items.png";
	}
	@Override
	 public String getItemName()
     {
         return "parts";
     }
     
     
@Override
   public String getItemNameIS(ItemStack par1ItemStack)
     {
	   int var3 = par1ItemStack.getItemDamage();
	   switch(var3)
	   {	   
	   case 0: return "BronzeTube";
	   case 1: return "ObbyTube";
	   case 2: return "Seal";
	   case 3: return "IronTube";
	   case 4: return "StickSeal";
	   }
         return this.getItemName();
     }
@Override
     public void addCreativeItems(ArrayList itemList)
     { 
    	 	 itemList.add(new ItemStack(this, 1,0));
             itemList.add(new ItemStack(this, 1,1));
             itemList.add(new ItemStack(this, 1,2));
             itemList.add(new ItemStack(this, 1,3));
             itemList.add(new ItemStack(this, 1,4));
     }
}



