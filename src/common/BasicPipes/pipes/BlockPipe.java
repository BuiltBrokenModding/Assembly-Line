package BasicPipes.pipes;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import BasicPipes.pipes.api.ILiquidConsumer;
import BasicPipes.pipes.api.ILiquidProducer;

public class BlockPipe extends BlockContainer 
{	
	
	public BlockPipe(int id)
	{
		super(id, Material.iron);
		this.setBlockName("Pipe");
		this.setBlockBounds(0.30F, 0.30F, 0.30F, 0.70F, 0.70F, 0.70F);
	}
    
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    /**
     * The type of render function that is called for this block
    */
    public int getRenderType()
    {
        return -1;
    }
	
	/**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }
	//Per tick
	public int conductorCapacity()
	{
		return 5;
	}
	
	
	/**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
	@Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        
        this.updateConductorTileEntity(world, x, y, z);
    }
	public static TileEntity getUEUnit(World world, int x, int y, int z, byte side,int type)
	{
		switch(side)
		{
			case 0: y -= 1; break;
			case 1: y += 1; break;
			case 2: z += 1; break;
			case 3: z -= 1; break;
			case 4: x += 1; break;
			case 5: x -= 1; break;
		}
		
		//Check if the designated block is a UE Unit - producer, consumer or a conductor
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		TileEntity returnValue = null;		
		
		if(tileEntity instanceof ILiquidConsumer)
		{	
			if(((ILiquidConsumer)tileEntity).canRecieveLiquid(type,ForgeDirection.getOrientation(side)))
			{
				returnValue = tileEntity;
			}
		}
		
		if (tileEntity instanceof ILiquidProducer)
		{			
			if(((ILiquidProducer)tileEntity).canProduceLiquid(type,ForgeDirection.getOrientation(side)))
			{
				returnValue = tileEntity;
			}
		}
		
		return returnValue;
	}
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
	@Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
    {
    	super.onNeighborBlockChange(world, x, y, z, blockID);
    	this.updateConductorTileEntity(world, x, y, z);
    }
	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
		int var5 = par1World.getBlockId(par2, par3, par4);
        return var5 == 0 || blocksList[var5].blockMaterial.isGroundCover();
    }
	@Override
	public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
		return true;
	}
	public static void updateConductorTileEntity(World world, int x, int y, int z)
	{
		
		for(byte i = 0; i < 6; i++)
        {
            //Update the tile entity on neighboring blocks
        	TileEntityPipe conductorTileEntity = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
        	int type = conductorTileEntity.getType();
        	conductorTileEntity.addConnection(getUEUnit(world, x, y, z, i, type), ForgeDirection.getOrientation(i));;
        }
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return new TileEntityPipe();
	}
 }


