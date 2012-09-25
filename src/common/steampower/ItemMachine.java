package steampower;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.*;

public class ItemMachine extends ItemBlock {       

        public ItemMachine(int id) {
                super(id);
                setMaxDamage(0);
                setHasSubtypes(true);
                this.setIconIndex(21);
                this.setCreativeTab(CreativeTabs.tabBlock);
        }
        @Override
        public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
        {
        	
        		par3List.add(new ItemStack(this, 1, 1));
        		par3List.add(new ItemStack(this, 1, 2));
        		par3List.add(new ItemStack(this, 1, 15));
        }
        @Override
    	public String getTextureFile() {
    		// TODO Auto-generated method stub
    		return SteamPowerMain.textureFile+"Items.png";
    	}
        @Override
        public int getIconFromDamage(int par1)
        {
        	switch(par1)
        	{
        	case 1: return 23;
        	case 2: return 22;
        	case 15: return 25;
        	}
            return this.iconIndex+par1;
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

