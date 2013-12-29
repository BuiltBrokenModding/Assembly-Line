package com.builtbroken.assemblyline.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;

public class ItemWire extends ItemMultipartBase
{

    public ItemWire()
    {
        super("wire");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public TMultiPart newPart(ItemStack itemStack, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 hit)
    {
        return MultiPartRegistry.createPart("assembly_line_basic_wire", false);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}