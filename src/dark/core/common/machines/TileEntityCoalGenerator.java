package dark.core.common.machines;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
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
    public static final float MAX_GENERATE_WATTS = 0.5f;

    /** Amount of heat the coal generator needs before generating electricity. */

    public static final float MIN_GENERATE_WATTS = MAX_GENERATE_WATTS * 0.1f;

    private static final float BASE_ACCELERATION = 0.000001f;

    /** Per second */
    public float prevGenerateWatts, generateWatts = 0;

    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace
     * burning for */
    public int itemCookTime = 0;

    public final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    @Override
    public void updateEntity()
    {
        this.setEnergyStored(this.generateWatts);

        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            this.prevGenerateWatts = this.generateWatts;

            if (this.itemCookTime > 0)
            {
                this.itemCookTime--;

                if (this.getEnergyStored() < this.getMaxEnergyStored())
                {
                    this.generateWatts = Math.min(this.generateWatts + Math.min((this.generateWatts * 0.007F + BASE_ACCELERATION), 0.007F), TileEntityCoalGenerator.MAX_GENERATE_WATTS);
                }
            }

            if (this.getInventory().getStackInSlot(0) != null && this.getEnergyStored() < this.getMaxEnergyStored())
            {
                if (this.getInventory().getStackInSlot(0).getItem().itemID == Item.coal.itemID)
                {
                    if (this.itemCookTime <= 0)
                    {
                        this.itemCookTime = 320;
                        this.decrStackSize(0, 1);
                    }
                }
            }

            if (this.itemCookTime <= 0)
            {
                this.generateWatts = Math.max(this.generateWatts - 0.008F, 0);
            }

            if (this.ticks % 3 == 0)
            {
                for (EntityPlayer player : this.playersUsing)
                {
                    PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), (Player) player);
                }
            }

            if (this.prevGenerateWatts <= 0 && this.generateWatts > 0 || this.prevGenerateWatts > 0 && this.generateWatts <= 0)
            {
                PacketHandler.instance().sendPacketToClients(getDescriptionPacket(), this.worldObj);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getPacket(this.getChannel(), this, "gen", this.generateWatts, this.itemCookTime);
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
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
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
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        return itemstack.itemID == Item.coal.itemID;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int j)
    {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int j)
    {
        return slotID == 0;
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