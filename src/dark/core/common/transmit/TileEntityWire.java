package dark.core.common.transmit;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderBlockSolarPanel;
import dark.core.client.renders.RenderBlockWire;
import dark.core.prefab.IExtraInfo.IExtraTileEntityInfo;
import universalelectricity.compatibility.TileEntityUniversalConductor;

public class TileEntityWire extends TileEntityUniversalConductor implements IExtraTileEntityInfo
{
    int updateTick = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.ticks % 1 + updateTick == 0)
        {
            this.updateTick = this.worldObj.rand.nextInt(200);
            this.refresh();
        }
    }

    @Override
    public float getResistance()
    {
        return BlockWire.wireResistance;
    }

    @Override
    public float getCurrentCapacity()
    {
        return BlockWire.ampMax;
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer getClientTileEntityRenderer()
    {
        return new RenderBlockWire();
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {


    }

}
