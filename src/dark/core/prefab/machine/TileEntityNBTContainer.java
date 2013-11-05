package dark.core.prefab.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/** NBT Container blocks to use that don't really need a tileEntity for anything other than to record
 * a few values
 *
 * @author DarkGuardsman */
public class TileEntityNBTContainer extends TileEntity
{
    private NBTTagCompound saveData;

    public NBTTagCompound getSaveData()
    {
        if (this.saveData == null)
        {
            this.saveData = new NBTTagCompound();
        }
        return saveData;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        nbt.setCompoundTag("saveData", this.getSaveData());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.saveData = nbt.getCompoundTag("saveData");
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }
}
