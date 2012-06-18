package net.minecraft.src.eui;
import java.util.ArrayList;

import net.minecraft.src.*;

public class ItemMachine extends ItemBlock {       

        public ItemMachine(int id) {
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
            return "Machine";
        }
        
        public int getPlacedBlockMetadata(int damage) {
                return damage;
        }

        public String getItemNameIS(ItemStack par1ItemStack)
        {
   	   int var3 = par1ItemStack.getItemDamage();
   	   switch(var3)
   	   {
   	   case 0: return "CoalProcessor";
   	   case 1: return "Boiler";
   	   case 2: return "FireBox";
   	   case 3: return "SteamGen";
   	   case 15: return "EnergyNuller";
   	   }
            return this.getItemName();
        }
        public void addCreativeItems(ArrayList itemList)     {       
            
            itemList.add(new ItemStack(this, 1,0));
            itemList.add(new ItemStack(this, 1,1));
            itemList.add(new ItemStack(this, 1,2));
            itemList.add(new ItemStack(this, 1,3));
            itemList.add(new ItemStack(this, 1,15));
        
    }

}