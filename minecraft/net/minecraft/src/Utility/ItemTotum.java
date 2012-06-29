package net.minecraft.src.Utility;
import java.util.ArrayList;

import net.minecraft.src.*;

public class ItemTotum extends ItemBlock {       

        public ItemTotum(int id) {
                super(id);
                setMaxDamage(0);
                setHasSubtypes(true);
        }
        public int getMetadata(int metadata)
        {
            return metadata;
        }
        
        public String getItemName()
        {
            return "Totum";
        }
        
        public int getPlacedBlockMetadata(int damage) {
                return damage;
        }

        public String getItemNameIS(ItemStack par1ItemStack)
        {
   	   int var3 = par1ItemStack.getItemDamage();
   	   switch(var3)
   	   {
   	   case 0: return "Healer";
   	   case 1: return "Booster";
   	   case 2: return "";
   	   case 3: return "";
   	   case 15: return "";
   	   }
            return this.getItemName();
        }
        public void addCreativeItems(ArrayList itemList)     {       
            
            itemList.add(new ItemStack(this, 1,0));
            itemList.add(new ItemStack(this, 1,1));
        
    }

}