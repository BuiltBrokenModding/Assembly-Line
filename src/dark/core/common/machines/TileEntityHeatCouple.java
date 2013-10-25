package dark.core.common.machines;

import java.util.EnumSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.energy.IHeatObject;
import dark.api.parts.ITileConnector.Connection;
import dark.core.prefab.machine.TileEntityEnergyMachine;

/** Machine that turns heat into usable electrical energy
 * 
 * @author DarkGuardsman */
public class TileEntityHeatCouple extends TileEntityEnergyMachine
{
    protected float tempture = 0.0f;
    protected float outputWatts = 0f;
    protected float heatJoules = 0f;
    protected float maxHeat = 1000f;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 10 == 0)
            {
                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
                {
                    Vector3 loc = new Vector3(this).translate(new Vector3(side));
                    TileEntity entity = loc.getTileEntity(this.worldObj);
                    if (entity instanceof IHeatObject && ((IHeatObject) entity).canTileConnect(Connection.HEAT, side.getOpposite()))
                    {
                        if (this.heatJoules < this.maxHeat)
                        {
                            float heat = ((IHeatObject) entity).getHeat(side.getOpposite());
                            if (heat + heatJoules <= maxHeat)
                            {
                                this.heatJoules += heat;
                                ((IHeatObject) entity).setHeat(-heat, true);
                            }
                            else
                            {
                                float room = (heat - ((this.heatJoules + heat) - this.maxHeat));
                                if (room > 0)
                                {
                                    this.heatJoules += room;
                                    ((IHeatObject) entity).setHeat(-room, true);
                                }
                            }
                        }
                    }
                }
                this.outputWatts = heatJoules * .4f;
            }
        }
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return this.outputWatts;
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }
}
