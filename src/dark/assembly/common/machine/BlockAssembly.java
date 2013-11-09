package dark.assembly.common.machine;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import dark.assembly.common.AssemblyLine;
import dark.core.common.DMCreativeTab;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockAssembly extends BlockMachine
{

    public BlockAssembly(BlockBuildData buildBuildData)
    {
        super(buildBuildData.setCreativeTab(DMCreativeTab.tabAutomation).setConfigProvider(AssemblyLine.CONFIGURATION));
    }

    public BlockAssembly(String blockName, Material material)
    {
        super(AssemblyLine.CONFIGURATION, blockName, material);
        this.setCreativeTab(DMCreativeTab.tabAutomation);
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