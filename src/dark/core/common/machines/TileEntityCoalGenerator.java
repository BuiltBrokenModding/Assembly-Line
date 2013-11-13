package dark.core.common.machines;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dark.core.network.PacketHandler;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityCoalGenerator extends TileEntityEnergyMachine
{
    /** Maximum amount of energy needed to generate electricity */
    public static float MAX_GENERATE_WATTS = 0.5f;

    /** Amount of heat the coal generator needs before generating electricity. */

    public static final float MIN_GENERATE_WATTS = MAX_GENERATE_WATTS * 0.1f;

    private static float BASE_ACCELERATION = 0.000001f;
    private static float BASE_DECCELERATION = 0.008f;

    /** Per second */
    public float generateWatts = 0;

    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace
     * burning for */
    public int itemCookTime = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            //Consume item if cook time is too low
            if (this.getInventory().getStackInSlot(0) != null && this.itemCookTime <= 10)
            {
                if (TileEntityFurnace.isItemFuel(this.getInventory().getStackInSlot(0)))
                {
                    this.itemCookTime += TileEntityFurnace.getItemBurnTime(this.getInventory().getStackInSlot(0));
                    this.decrStackSize(0, 1);
                }
            }

            //Update item cook time & power output
            if (this.itemCookTime-- > 0)
            {
                this.generateWatts = Math.min(this.generateWatts + Math.min((this.generateWatts * 0.007F + BASE_ACCELERATION), 0.007F), TileEntityCoalGenerator.MAX_GENERATE_WATTS);
            }

            //Decrease generator output if nothing is burning
            if (this.itemCookTime <= 0)
            {
                this.generateWatts = Math.max(this.generateWatts - BASE_DECCELERATION, 0);
            }

            if (this.generateWatts >= MIN_GENERATE_WATTS)
            {
                this.produceAllSides();
            }
        }
    }

    /** Does this tile have power to run and do work */
    @Override
    public boolean canFunction()
    {
        return !this.isDisabled() && this.generateWatts > 0;
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dataStream, Player player)
    {
        try
        {
            if (this.worldObj.isRemote && super.simplePacket(id, dataStream, player))
            {
                if (id.equalsIgnoreCase("gen"))
                {
                    this.generateWatts = dataStream.readFloat();
                    this.itemCookTime = dataStream.readInt();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sendGUIPacket(EntityPlayer entity)
    {
        if (entity != null)
        {
            PacketDispatcher.sendPacketToPlayer(PacketHandler.instance().getTilePacket(this.getChannel(), this, "gen", this.generateWatts, this.itemCookTime), (Player) entity);
        }
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
        this.generateWatts = par1NBTTagCompound.getFloat("generateRate");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("itemCookTime", this.itemCookTime);
        par1NBTTagCompound.setFloat("generateRate", this.generateWatts);
    }

    @Override
    public String getInvName()
    {
        return LanguageRegistry.instance().getStringLocalization("gui.coalgen.name");
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return TileEntityFurnace.isItemFuel(stack);
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot >= this.getSizeInventory())
        {
            return false;
        }
        return true;
    }

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack electricityPack, boolean doReceive)
    {
        return 0;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        if (direction == ForgeDirection.getOrientation(this.getBlockMetadata() + 2))
        {
            return this.generateWatts < TileEntityCoalGenerator.MIN_GENERATE_WATTS ? 0 : this.generateWatts;
        }

        return 0.0F;
    }

    @Override
    public EnumSet<ForgeDirection> getInputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() + 2));
    }

    @Override
    public float getMaxEnergyStored()
    {
        return MAX_GENERATE_WATTS;
    }
}