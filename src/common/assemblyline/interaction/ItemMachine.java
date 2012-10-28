package assemblyline.interaction;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import assemblyline.AssemblyLine;

public class ItemMachine extends ItemBlock {

	public ItemMachine(int par1) {
		super(par1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	private String[] names = new String[] {"Ejector", "ItemScooper", "FB","FB"};
	int blockID = AssemblyLine.machineID;
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		par3List.add(new ItemStack(AssemblyLine.blockMachine,1,0));
		par3List.add(new ItemStack(AssemblyLine.blockMachine,1,4));
		//par3List.add(new ItemStack(AssembleLine.blockMachine,1,8));
		//par3List.add(new ItemStack(AssembleLine.blockMachine,1,12));
    }
	 public String getItemNameIS(ItemStack itemstack)
	    {
		 int meta = itemstack.getItemDamage();
		 switch(meta)
		 {
		 	case 0: return names[0]; 
		 	case 4: return names[1];
		 	case 8: return names[2];
		 	case 12: return names[3];
		 }
	        return "FB";
	    }

	    @Override
	    public int getIconFromDamage(int i)
	    {
	    	switch(i)
			 {
			 	case 0:return 1; 
			 	case 4:return 2;
			 	case 8:return 3;
			 	case 12:return 4;
			 }
	        return this.iconIndex + i;
	    }
	    public int getMetadata(int par1)
	    {
	        return 0;
	    }
	@Override
	public int getBlockID()
    {
        return AssemblyLine.machineID;
    }
	@Override
	 public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	    {
		int angle = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		player.sendChatToPlayer("M:"+stack.getItemDamage()+"A:"+angle);
	       if (!world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, stack.getItemDamage()+angle))
	       {
	               return false;
	       }

	       if (world.getBlockId(x, y, z) == this.blockID)
	       {
	           Block.blocksList[this.blockID].updateBlockMetadata(world, x, y, z, side, hitX, hitY, hitZ);
	           Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, player);
	       }

	       return true;
	    }

}
