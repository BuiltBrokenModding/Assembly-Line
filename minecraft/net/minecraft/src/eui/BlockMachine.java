package net.minecraft.src.eui;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.basiccomponents.*;
import net.minecraft.src.eui.boiler.*;
import net.minecraft.src.eui.burner.GUIFireBox;
import net.minecraft.src.eui.burner.TileEntityFireBox;
import net.minecraft.src.eui.grinder.*;
import net.minecraft.src.eui.turbine.GUIGenerator;
import net.minecraft.src.eui.turbine.TileEntityGenerator;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.*;

public class BlockMachine extends net.minecraft.src.universalelectricity.extend.BlockMachine implements ITextureProvider
{
    
    private Random furnaceRand = new Random();  
    private static boolean keepFurnaceInventory = true;

    public BlockMachine(int par1)
    {
        super("machine", par1, Material.iron);
        this.setRequiresSelfNotify();
    }    
    @Override
    protected int damageDropped(int metadata)
    {
        return metadata;
    }
    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
    {
    	TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);
    	
    	if(tileEntity instanceof TileEntityFireBox)
    	{
	        if(((TileEntityFireBox)tileEntity).generateRate > 0)
	        {
	            int var6 = (int)((TileEntityFireBox)tileEntity).getDirection();
	            float var7 = (float)x + 0.5F;
	            float var8 = (float)y + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
	            float var9 = (float)z + 0.5F;
	            float var10 = 0.52F;
	            float var11 = par5Random.nextFloat() * 0.6F - 0.3F;
	
	            if (var6 == 5)
	            {
	                par1World.spawnParticle("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
	                par1World.spawnParticle("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
	            }
	            else if (var6 == 4)
	            {
	                par1World.spawnParticle("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
	                par1World.spawnParticle("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
	            }
	            else if (var6 == 3)
	            {
	                par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
	                par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
	            }
	            else if (var6 == 2)
	            {
	                par1World.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
	                par1World.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
	            }
	        }
    	}
    }
    @Override
	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y, int z, int side)
    {
    	TileEntity tileEntity = par1iBlockAccess.getBlockTileEntity(x, y, z);
    	int metadata = par1iBlockAccess.getBlockMetadata(x, y, z);
    	
    	if (side == 1)
        {
    		switch(metadata)
    		{
    			case 0: return 1;
    			case 1: return 3;
    			case 2: return 18;
    			case 3: return 5;
    		}
        }
        
        	//If it is the front side
        	if(side == ((TileEntityMachine)tileEntity).getDirection())
        	{
        		switch(metadata)
        		{
        			
        			case 1: return 3;
        			case 3: return 3;
        		}
        	}
        	//If it is the back side
        	else if(side == UniversalElectricity.getOrientationFromSide(((TileEntityMachine)tileEntity).getDirection(), (byte)2))
        	{
        		switch(metadata)
        		{
        			case 0: return 19;
        			case 1: return 6;
        			case 2: return 17;
        			case 3: return 3;
        		}
        	}

            
            switch(metadata)
            {
            case 1: return 4;
            case 2: return 16;
            case 3: return 2;
            }
            return 0;
        
    	
	}
    @Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
    	if (side == 1)
        {
    		switch(metadata)
    		{
    			case 0: return 1;
    			case 1: return 3;
    			case 2: return 18;
    			case 3: return 5;
    		}
        }
        else
        {
        	//If it is the front side
        	if(side == 3)
        	{
        		switch(metadata)
        		{
        			case 0: return 19;
        			case 1: return 6;
        			case 2: return 17;
        			case 3: return 3;
        		}
        	}
        	//If it is the back side
        	else if(side == 2)
        	{
        		switch(metadata)
        		{
        			case 0: return this.blockIndexInTexture + 2;
        			case 1: return this.blockIndexInTexture + 3;
        			case 2: return this.blockIndexInTexture + 2;
        		}
        	}

        	switch(metadata)
            {
            case 1: return 4;
            case 2: return 16;
            case 3: return 2;
            }
        }
		return 0;
        }
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
    	int metadata = par1World.getBlockMetadata(par2, par3, par4);
    	
    	/**
    	 * Check if the player is holding a wrench or an electric item. If so, do not open the GUI.
    	 */
    	if(par5EntityPlayer.inventory.getCurrentItem() != null)
    	{
    		if(par5EntityPlayer.inventory.getCurrentItem().itemID == BasicComponents.ItemWrench.shiftedIndex)
        	{
    			if(onUseWrench(par1World, par2, par3, par4, par5EntityPlayer))
    			{
    				par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
    				return true;
    			}
        	}
    		else if(par5EntityPlayer.inventory.getCurrentItem().getItem() instanceof net.minecraft.src.universalelectricity.extend.ItemElectric)
    		{
    			if(onUseElectricItem(par1World, par2, par3, par4, par5EntityPlayer))
    			{
    				return true;
    			}
    		}
    	}

    	return machineActivated(par1World, par2, par3, par4, par5EntityPlayer);
    }
    public boolean onUseElectricItem(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean onUseWrench(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
    	TileEntityMachine tileEntity = (TileEntityMachine)par1World.getBlockTileEntity(par2, par3, par4);

    	//Reorient the block
		switch(tileEntity.getDirection())
		{
			case 2: tileEntity.setDirection((byte)5); break;
	    	case 5: tileEntity.setDirection((byte)3); break;
	    	case 3: tileEntity.setDirection((byte)4); break;
	    	case 4: tileEntity.setDirection((byte)2); break;
		}
		
		return true;
    }
    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean machineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity blockEntity = (TileEntity)par1World.getBlockTileEntity(x, y, z);

            if (blockEntity != null)
            {
            	if(blockEntity instanceof TileEntityGrinder)
            	{
            	TileEntity var6 = (TileEntityGrinder)par1World.getBlockTileEntity(x, y, z);
            	ModLoader.openGUI(par5EntityPlayer, new GuiGrinder(par5EntityPlayer.inventory, (TileEntityGrinder) var6 )); ;
            	}
            	if(blockEntity instanceof TileEntityBoiler)
            	{
            	TileEntity var6 = (TileEntityBoiler)par1World.getBlockTileEntity(x, y, z);
            	ModLoader.openGUI(par5EntityPlayer, new GuiBoiler(par5EntityPlayer.inventory, (TileEntityBoiler) var6 )); ;
            	}
            	if(blockEntity instanceof TileEntityFireBox)
            	{
            	TileEntity var6 = (TileEntityFireBox)par1World.getBlockTileEntity(x, y, z);
            	ModLoader.openGUI(par5EntityPlayer, new GUIFireBox(par5EntityPlayer.inventory, (TileEntityFireBox) var6 )); ;
            	}
            	if(blockEntity instanceof TileEntityGenerator)
            	{
            	TileEntity var6 = (TileEntityGenerator)par1World.getBlockTileEntity(x, y, z);
            	ModLoader.openGUI(par5EntityPlayer, new GUIGenerator(par5EntityPlayer.inventory, (TileEntityGenerator) var6 )); ;
            	}
            }

            return true;
        }
    }    
    @Override
    public TileEntity getBlockEntity(int meta)
    {
        switch(meta)
        {    
        case 0: return new TileEntityGrinder();
        case 1: return new TileEntityBoiler();
        case 2: return new TileEntityFireBox();
        case 3: return new TileEntityGenerator();
        case 15: return new TileEntityNuller();
        }
		return null;
    }
    
    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
    {
        int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int metadata = par1World.getBlockMetadata(x, y, z);
        TileEntityMachine tileEntity = (TileEntityMachine)par1World.getBlockTileEntity(x, y, z);
        
        if(metadata == 0)
        {
	        switch (angle)
	        {
	        	case 0: tileEntity.setDirection((byte)5); break;
	        	case 1: tileEntity.setDirection((byte)3); break;
	        	case 2: tileEntity.setDirection((byte)4); break;
	        	case 3: tileEntity.setDirection((byte)2); break;
	        }
        }
        else
        {
        	switch (angle)
	        {
	        	case 0: tileEntity.setDirection((byte)3); break;
	        	case 1: tileEntity.setDirection((byte)4); break;
	        	case 2: tileEntity.setDirection((byte)2); break;
	        	case 3: tileEntity.setDirection((byte)5); break;
	        }
        }
    }
	/**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        if (!keepFurnaceInventory)
        {
            TileEntityGrinder var5 = (TileEntityGrinder)par1World.getBlockTileEntity(par2, par3, par4);

            if (var5 != null)
            {
                for (int var6 = 0; var6 < var5.getSizeInventory(); ++var6)
                {
                    ItemStack var7 = var5.getStackInSlot(var6);

                    if (var7 != null)
                    {
                        float var8 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                        float var9 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                        float var10 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

                        while (var7.stackSize > 0)
                        {
                            int var11 = this.furnaceRand.nextInt(21) + 10;

                            if (var11 > var7.stackSize)
                            {
                                var11 = var7.stackSize;
                            }

                            var7.stackSize -= var11;
                            EntityItem var12 = new EntityItem(par1World, (double)((float)par2 + var8), (double)((float)par3 + var9), (double)((float)par4 + var10), new ItemStack(var7.itemID, var11, var7.getItemDamage()));

                            if (var7.hasTagCompound())
                            {
                                var12.item.setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
                            }

                            float var13 = 0.05F;
                            var12.motionX = (double)((float)this.furnaceRand.nextGaussian() * var13);
                            var12.motionY = (double)((float)this.furnaceRand.nextGaussian() * var13 + 0.2F);
                            var12.motionZ = (double)((float)this.furnaceRand.nextGaussian() * var13);
                            par1World.spawnEntityInWorld(var12);
                        }
                    }
                }
            }
        }

        super.onBlockRemoval(par1World, par2, par3, par4);
    }
	@Override
	public TileEntity getBlockEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return "/eui/blocks.png";
	}
	    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	public int getRenderType()
	{
	   return 0;
	}
public void addCreativeItems(ArrayList itemList)     {       
        
        itemList.add(new ItemStack(this, 1,0));
        itemList.add(new ItemStack(this, 1,1));
        itemList.add(new ItemStack(this, 1,2));
        itemList.add(new ItemStack(this, 1,3));
        itemList.add(new ItemStack(this, 1,14));
        itemList.add(new ItemStack(this, 1,15));
}
}
