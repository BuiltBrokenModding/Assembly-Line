package EUI.SteamPower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import universalelectricity.basiccomponents.BasicComponents;

import EUI.SteamPower.boiler.TileEntityBoiler;
import EUI.SteamPower.burner.TileEntityFireBox;
import EUI.SteamPower.turbine.TileEntityGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import EUI.SteamPower.*;
import EUIClient.SteamPower.GUIFireBox;
import EUIClient.SteamPower.GUIGenerator;
import EUIClient.SteamPower.GuiBoiler;
import net.minecraftforge.common.ForgeDirection;

public class BlockMachine extends universalelectricity.extend.BlockMachine
{
    
    private Random furnaceRand = new Random();  
    private static boolean keepFurnaceInventory = true;

    public BlockMachine(int par1)
    {
        super("machine", par1, Material.iron);
        this.setRequiresSelfNotify();
        this.setCreativeTab(CreativeTabs.tabBlock);
        
    }    
    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	
    		par3List.add(new ItemStack(this, 1, 1));
    		par3List.add(new ItemStack(this, 1, 2));
    		par3List.add(new ItemStack(this, 1, 3));
    		par3List.add(new ItemStack(this, 1, 15));
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
	            int var6 = ((TileEntityFireBox) tileEntity).getDirection();
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
    	if(metadata > -1 && tileEntity != null)
    	{
    	if (side == 1)
        {
    		switch(metadata)
    		{
    			case 0: return 6;
    			case 1: return 4;
    			case 2: return 7;
    			case 3: return 4;
    		}
        }
        	//If it is the back side
        	else if(side == ((TileEntityMachine) tileEntity).getDirection());
        	{
        		switch(metadata)
        		{
        			case 0: return 5;
        			case 2: return 8;
        			case 3: return 4;
        		}
        	}

            
            switch(metadata)
            {
            case 1: return 0;
            case 2: return 2;
            }
    	}
            return 1;
        
    	
	}
    @Override
	public boolean onUseWrench(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
    	TileEntityMachine tileEntity = (TileEntityMachine)par1World.getBlockTileEntity(par2, par3, par4);

    	//Reorient the block
		switch(tileEntity.getDirection())
		{
			case 2: tileEntity.setDirection(5); break;
	    	case 5: tileEntity.setDirection(3); break;
	    	case 3: tileEntity.setDirection(4); break;
	    	case 4: tileEntity.setDirection(2); break;
		}
		
		return true;
    }
    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
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
            	
            	if(blockEntity instanceof TileEntityBoiler)
            	{
            	TileEntity var6 = (TileEntityBoiler)par1World.getBlockTileEntity(x, y, z);
            	par5EntityPlayer.openGui(SteamPower.instance, 1, par1World, x, y, z);
            	}
            	if(blockEntity instanceof TileEntityFireBox)
            	{
            	TileEntity var6 = (TileEntityFireBox)par1World.getBlockTileEntity(x, y, z);
            	par5EntityPlayer.openGui(SteamPower.instance, 0, par1World, x, y, z);
            	}
            	if(blockEntity instanceof TileEntityGenerator)
            	{
            	TileEntity var6 = (TileEntityGenerator)par1World.getBlockTileEntity(x, y, z);
            	par5EntityPlayer.openGui(SteamPower.instance, 2, par1World, x, y, z);
            	}
            }

            return true;
        }
    }    
    @Override
    public TileEntity createNewTileEntity(World var1,int meta)
    {
        switch(meta)
        {  
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
        
	        switch (angle)
	        {
	        	case 0: tileEntity.setDirection(5); break;
	        	case 1: tileEntity.setDirection(3); break;
	        	case 2: tileEntity.setDirection(4); break;
	        	case 3: tileEntity.setDirection(2); break;
	        }
    }
	/**
     * Called whenever the block is removed.
     */
    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4,int par5, int par6){
        if (!keepFurnaceInventory)
        {
        	TileEntityMachine var5 = null;
            TileEntity entityBox = par1World.getBlockTileEntity(par2, par3, par4);
			if(entityBox instanceof TileEntityFireBox)
			{
				var5 = (TileEntityFireBox)entityBox;
			}
			else if(entityBox instanceof TileEntityBoiler)
			{
				var5 = (TileEntityBoiler)entityBox;
			}
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

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
	@Override
	public String getTextureFile()
	{
		return "/EUIClient/textures/blocks/blocks.png";
	}
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	@Override
	public boolean renderAsNormalBlock()
	{
	    return false;
	}
	@Override
	public int getRenderType()
	{
	   return -1;
	}
}
