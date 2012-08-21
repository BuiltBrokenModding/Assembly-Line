package net.minecraft.src.eui;
import java.util.ArrayList;

import net.minecraft.src.*;

public class ItemMachine extends ItemBlock {       

        public ItemMachine(int id) {
                super(id);
                setMaxDamage(0);
                setHasSubtypes(true);
        }
        @Override
        public int getMetadata(int metadata)
        {
            return metadata;
        }
        @Override
        public String getItemName()
        {
            return "Machine";
        }
        @Override
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
    }

