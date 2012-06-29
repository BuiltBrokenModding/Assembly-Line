package net.minecraft.src.Utility;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.*;
import net.minecraft.src.universalelectricity.components.UniversalComponents;

public class BlockTotum extends UEBlockMachine implements ITextureProvider
{
    
    private Random furnaceRand = new Random();  
    private static boolean keepFurnaceInventory = true;

    public BlockTotum(int par1)
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
        	if(side == ((TileEntityTotum)tileEntity).getDirection())
        	{
        		switch(metadata)
        		{
        			
        			case 1: return 3;
        			case 3: return 3;
        		}
        	}
        	//If it is the back side
        	else if(side == UniversalElectricity.getOrientationFromSide(((TileEntityTotum)tileEntity).getDirection(), (byte)2))
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
    		if(par5EntityPlayer.inventory.getCurrentItem().itemID == UniversalComponents.ItemWrench.shiftedIndex)
        	{
    			if(onUseWrench(par1World, par2, par3, par4, par5EntityPlayer))
    			{
    				par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
    				return true;
    			}
        	}
    		else if(par5EntityPlayer.inventory.getCurrentItem().getItem() instanceof UEElectricItem)
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
    	TileEntityTotum tileEntity = (TileEntityTotum)par1World.getBlockTileEntity(par2, par3, par4);

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
            	if(blockEntity instanceof TileEntityHealer)
            	{
            	TileEntity var6 = (TileEntityHealer)par1World.getBlockTileEntity(x, y, z);
            	ModLoader.openGUI(par5EntityPlayer, new GUIHealer(par5EntityPlayer.inventory, (TileEntityHealer) var6 )); ;
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
        case 0: return new TileEntityHealer();
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
        TileEntityTotum tileEntity = (TileEntityTotum)par1World.getBlockTileEntity(x, y, z);
        
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
	    	return UniversalComponents.MachineRenderType;
	    }
}
