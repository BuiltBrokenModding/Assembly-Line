package liquidmechanics.api.liquids;

import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LiquidFiniteStill extends BlockStationary implements ILiquid
{

    public LiquidFiniteStill(int blockId)
    {
        super(blockId, Material.water);
        this.setHardness(100);
        this.disableStats();
        this.setTextureFile("");
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return 0xFFFFFF;
    }

    @Override
    public int tickRate()
    {
        return 20;
    }

    @Override
    public int stillLiquidId()
    {
        return this.blockID;
    }

    @Override
    public boolean isMetaSensitive()
    {
        return false;
    }

    @Override
    public int stillLiquidMeta()
    {
        return 0;
    }

    @Override
    public boolean isBlockReplaceable(World world, int x, int y, int z)
    {
        return true;
    }
}
