package com.builtbroken.assemblyline.machine.red;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.api.vector.Vector3;

import com.builtbroken.minecraft.interfaces.IMultiBlock;
import com.builtbroken.minecraft.prefab.TileEntityMachine;

public class TileEntityPistonPlus extends TileEntityMachine implements IMultiBlock
{
    int extensionLimit = 1;
    boolean isExtended = false;

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate(Vector3 placedPosition)
    {
        //Don't do anything here as we will handle this in the update area

    }

    @Override
    public void onDestroy(TileEntity callingBlock)
    {
        // TODO Auto-generated method stub

    }

}
