package liquidmechanics.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSink extends BlockContainer
{

    protected BlockSink(int par1)
    {
        super(par1, Material.iron);
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
