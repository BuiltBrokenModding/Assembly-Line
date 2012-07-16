package net.minecraft.src.eui.robotics;


import java.util.ArrayList;

import net.minecraft.src.*;


public class ItemSpawn extends Item 
{
	
        
        public ItemSpawn (int id)
        {
                super(id);
                maxStackSize = 1;
                setMaxDamage(0);
                setHasSubtypes(true);
        }
public void addCreativeItems(ArrayList itemList)     
{
	
     itemList.add(new ItemStack(this, 1,1));
            
 }

        public String getItemNameIS(ItemStack itemstack) {
        	switch(itemstack.getItemDamage())
        	{
        	case 1: return "Bot";
        	}
        	
			return "Blank";
        }
        public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        	
        if(!world.isRemote)
        {     
        	
        	 i += Facing.offsetsXForSide[l];
             j += Facing.offsetsYForSide[l];
             k += Facing.offsetsZForSide[l];
            
             EntityShoeBot Guard = new EntityShoeBot(world);
             Guard.setLocationAndAngles((double)i + 0.5D, (double)j + 1.0D, (double)k + 0.5D, 0.0F, 0.0F);                 
             world.spawnEntityInWorld(Guard);    
             		
             	entityplayer.swingItem();
             	--itemstack.stackSize;
           }
             	
             	
        
        
        return true;
    }
}
