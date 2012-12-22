package dark.BasicUtilities.api;

import net.minecraftforge.common.ForgeDirection;

public interface IHeatCreator
{
    /**
     * @param dir - direction
     * @return Can create heat in this direction
     */
    public boolean canCreatHeat(ForgeDirection dir);
    /**
     * @param dir - direction
     * @return ammount of heat created in joules
     */
    public int createHeat(ForgeDirection dir);
}
