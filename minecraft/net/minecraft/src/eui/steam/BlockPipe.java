package net.minecraft.src.eui.steam;

import java.util.ArrayList;

import net.minecraft.src.eui.api.*;
import net.minecraft.src.eui.*;
import net.minecraft.src.eui.boiler.TileEntityBoiler;
import net.minecraft.src.eui.turbine.TileEntityGenerator;
import net.minecraft.src.eui.steam.TileEntityPipe;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.*;

import java.util.Random;

public class BlockPipe extends BlockContainer 
{	
	static String type = "";
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
        return mod_EUIndustry.pipeID;
    }

	public TileEntity getBlockEntity()
    {
    	return new TileEntityPipe();
    }
	
	//Per tick
	public int conductorCapacity()
	{
		return 5;
	}
	public static TileEntity getUEUnit(World world, int x, int y, int z, byte side)
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
		
		if(tileEntity instanceof ISteamConsumer)
		{			
			if(((ISteamConsumer)tileEntity).canRecieveSteam(UniversalElectricity.getOrientationFromSide(side, (byte)2)))
			{
				returnValue = tileEntity;
			}
		}
		
		if (tileEntity instanceof ISteamProducer)
		{			
			if(((ISteamProducer)tileEntity).canProduceSteam(UniversalElectricity.getOrientationFromSide(side, (byte)2)))
			{
				returnValue = tileEntity;
			}
		}
		
		return returnValue;
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
	
	public void updateConductorTileEntity(World world, int x, int y, int z)
	{
		for(byte i = 0; i < 6; i++)
        {
            //Update the tile entity on neighboring blocks
        	TileEntityPipe conductorTileEntity = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
        	conductorTileEntity.addConnection(getUEUnit(world, x, y, z, i), i);
        }
	}
	 
     
 }


