package dark.core.prefab.entities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.electricity.ElectricityPack;
import dark.api.IDrone;

public class EntityDroneBase extends EntityCreature implements IDrone, IElectrical, IElectricalStorage
{
    private float energyStored = 0.0f;

    public static final Attribute maxEnergy = (new RangedAttribute("drone.maxEnergy", 100.0D, 0.0D, Double.MAX_VALUE)).func_111117_a("Max Energy").setShouldWatch(true);

    public EntityDroneBase(World par1World)
    {
        super(par1World);
        this.getNavigator().setAvoidsWater(true);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(100.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
    }

    @Override
    protected String getLivingSound()
    {
        return "none";
    }

    @Override
    protected String getHurtSound()
    {
        return "none";
    }

    @Override
    protected String getDeathSound()
    {
        return "none";
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    public boolean consumePower(float watts, boolean doDrain)
    {
        if (this.getEnergyStored() >= watts)
        {
            if (doDrain)
            {
                this.setEnergyStored(this.getEnergyStored() - watts);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return false;
    }

    @Override
    public void setEnergyStored(float energy)
    {
        this.energyStored = energy;

    }

    @Override
    public float getEnergyStored()
    {
        return this.energyStored;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return 10;
    }

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        if (receive != null && this.canConnect(from))
        {
            return this.receiveElectricity(receive.getWatts(), doReceive);
        }
        return 0;
    }

    /** A non-side specific version of receiveElectricity for you to optionally use it internally. */
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {
        if (receive != null)
        {
            float prevEnergyStored = this.getEnergyStored();
            float newStoredEnergy = Math.min(this.getEnergyStored() + receive.getWatts(), this.getMaxEnergyStored());

            if (doReceive)
            {
                this.setEnergyStored(newStoredEnergy);
            }

            return Math.max(newStoredEnergy - prevEnergyStored, 0);
        }
        return 0;
    }

    public float receiveElectricity(float energy, boolean doReceive)
    {
        return this.receiveElectricity(ElectricityPack.getFromWatts(energy, this.getVoltage()), doReceive);
    }

    @Override
    public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
    {
        return null;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return this.getEnergyStored() - this.getMaxEnergyStored();
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getVoltage()
    {
        return 30;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.energyStored = nbt.getFloat("energyStored");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("energyStored", this.energyStored);
    }

}
