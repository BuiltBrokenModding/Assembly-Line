package dark.assembly.common.bottler;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.assembly.common.machine.BlockAssembly;

public class BlockBottler extends BlockAssembly
{

    public BlockBottler(int id)
    {
        super(id, Material.iron, "AutoBottler");
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }

}
