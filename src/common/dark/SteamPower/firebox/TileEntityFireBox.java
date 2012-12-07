package dark.SteamPower.firebox;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.api.IProducer;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;
import dark.Library.prefab.TileEntityMachine;
import dark.SteamPower.SteamPowerMain;
import dark.SteamPower.boiler.TileEntityBoiler;

public class TileEntityFireBox extends TileEntityMachine implements IPacketReceiver, IInventory, ISidedInventory, IProducer
{
    // max heat generated per second

    public boolean isConnected = false;
    public TileEntity[] connectedBlocks =
        { null, null, null, null, null, null };
    private int connectedUnits = 0;
    public static int maxGenerateRate = 250;
    public int generateRate = 0;
    int count = 0;
    public int itemCookTime = 0;

    public void updateEntity()
    {
        super.updateEntity();
        if (count++ >= 10)
        {
            count = 0;
            addConnection();
            if (!worldObj.isRemote)
            {
                sharCoal();
            }
            TileEntity blockEntity = worldObj.getBlockTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
            if (blockEntity instanceof TileEntityBoiler)
            {
                isConnected = true;
            }
            else
            {
                isConnected = false;
            }
            if (!this.worldObj.isRemote)
            {

                maxGenerateRate = SteamPowerMain.fireOutput + (connectedUnits * 10);

                // The top slot is for recharging items. Check if the item is a
                // electric item. If so, recharge it.
                if (this.storedItems[0] != null && isConnected)
                {
                    if (this.storedItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
                    {
                        if (this.itemCookTime <= 0)
                        {
                            itemCookTime = Math.max(1600 - (int) (this.generateRate * 20), 400);
                            this.decrStackSize(0, 1);
                        }
                    }
                }

            }
            // Starts generating electricity if the device is heated up
            if (this.itemCookTime > 0)
            {
                this.itemCookTime--;
                if (isConnected)
                {
                    this.generateRate = Math.min(this.generateRate + Math.min((this.generateRate) + 1, 1), this.maxGenerateRate / 10);
                }
            }
            // Loose heat when the generator is not connected or if there is no
            // coal in the inventory.
            if (this.itemCookTime <= 0 || !isConnected)
            {
                this.generateRate = Math.max(this.generateRate - 5, 0);
            }
        }
    }

    // gets all connected fireBoxes and shares its supply of coal
    public void sharCoal()
    {
        for (int i = 0; i < 6; i++)
        {

            if (connectedBlocks[i] instanceof TileEntityFireBox)
            {
                TileEntityFireBox connectedConsumer = (TileEntityFireBox) connectedBlocks[i];
                if (this.storedItems[0] != null)
                {
                    if (this.storedItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex && this.storedItems[0].stackSize > 0)
                    {
                        if (connectedConsumer.storedItems[0] != null)
                        {
                            if (connectedConsumer.storedItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
                            {
                                if (connectedConsumer.storedItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
                                {
                                    int CSum = Math.round(this.storedItems[0].stackSize + connectedConsumer.storedItems[0].stackSize) / 2;
                                    if (this.storedItems[0].stackSize > connectedConsumer.storedItems[0].stackSize)
                                    {
                                        int transferC = 0;
                                        transferC = Math.round(CSum - connectedConsumer.storedItems[0].stackSize);
                                        connectedConsumer.storedItems[0].stackSize = connectedConsumer.storedItems[0].stackSize + transferC;
                                        this.storedItems[0].stackSize = this.storedItems[0].stackSize - transferC;
                                    }
                                }
                            }
                        }
                        else
                        {
                            connectedConsumer.storedItems[0] = new ItemStack(this.storedItems[0].getItem(), 1, this.storedItems[0].getItemDamage());
                            this.storedItems[0].stackSize -= 1;
                        }
                    }
                }
            }
        }

    }

    public void addConnection()
    {
        connectedUnits = 0;
        TileEntity[] aEntity = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
        for (int i = 0; i < 6; i++)
        {

            if (aEntity[i] instanceof TileEntityFireBox && i != 0 && i != 1)
            {
                this.connectedBlocks[i] = aEntity[i];
                connectedUnits += 1;
            }
            else
            {
                this.connectedBlocks[i] = null;
            }
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
        this.generateRate = par1NBTTagCompound.getInteger("generateRate");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("itemCookTime", (int) this.itemCookTime);
        par1NBTTagCompound.setInteger("generateRate", (int) this.generateRate);
    }

    @Override
    public String getInvName()
    {
        return "FireBox";
    }

    public float onProduceHeat(float jouls, ForgeDirection side)
    {
        if (side == ForgeDirection.UP) { return Math.min(generateRate, jouls); }
        return 0;
    }

    @Override
    public Object[] getSendData()
    {
        return new Object[]
            { (int) generateRate, (int) itemCookTime };
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType,
            Packet250CustomPayload packet, EntityPlayer player,
            ByteArrayDataInput dataStream)
    {
        try
        {
            generateRate = dataStream.readInt();
            itemCookTime = dataStream.readInt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getStartInventorySide(ForgeDirection side)
    {
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        return 1;
    }

    @Override
    public String getChannel()
    {
        return SteamPowerMain.channel;
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public boolean needUpdate()
    {
        return true;
    }

    @Override
    public int onProduceLiquid(Liquid type, int maxVol, ForgeDirection side)
    {
        if (type == Liquid.HEAT) { return Math.min(this.generateRate, maxVol); }
        return 0;
    }

    @Override
    public boolean canProduceLiquid(Liquid type, ForgeDirection side)
    {
        if (type == Liquid.HEAT) { return true; }
        return false;
    }

    @Override
    public boolean canProducePresure(Liquid type, ForgeDirection side)
    {
        return false;
    }

    @Override
    public int presureOutput(Liquid type, ForgeDirection side)
    {
        return 0;
    }
}
