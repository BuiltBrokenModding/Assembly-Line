package basicpipes.conductors;

import java.util.ArrayList;
import java.util.List;

import basicpipes.BasicPipesMain;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemParts extends Item{
	String[] names = new String[]{"BronzeTube","IronTube","ObbyTube","NetherTube","Seal","StickSeal","BronzeTank","Valve",};
	int[] iconID =  new int[]	 {0	          ,1       	 ,2         ,3           ,16    ,17         ,18          ,19};//TODO check these
	public ItemParts(int par1)
    {
        super(par1);
        this.setItemName("Parts");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }
    @Override
    public int getIconFromDamage(int par1)
    {
    	if(par1 < iconID.length)
    	{
    		return iconID[par1];
    	}
    	return par1;
    }
    @Override
    public String getItemNameIS(ItemStack itemstack)
    {
        return names[itemstack.getItemDamage()];
    }
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < names.length; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
    public String getTextureFile() {
		return BasicPipesMain.textureFile+"/Items.png";
	}
	@Override
	 public String getItemName()
     {
         return "parts";
     }
}
     
     




