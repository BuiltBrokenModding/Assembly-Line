package dark.assembly.common.machine;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.TabAssemblyLine;
import dark.core.prefab.BlockMachine;

public abstract class BlockAssembly extends BlockMachine
{
    public Icon machine_icon;

    public BlockAssembly(int id, Material material, String name)
    {
        super(name, AssemblyLine.CONFIGURATION, id, material);
        this.setUnlocalizedName(name);
        this.setCreativeTab(TabAssemblyLine.INSTANCE);
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (entityPlayer != null && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().itemID == Item.stick.itemID)
        {
            TileEntity entity = world.getBlockTileEntity(x, y, z);
            if (entity instanceof TileEntityAssembly)
            {
                System.out.println(((TileEntityAssembly) entity).getTileNetwork().toString());
            }

        }
        return false;
    }

}