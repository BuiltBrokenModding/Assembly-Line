package liquidmechanics.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import liquidmechanics.api.IPressure;
import liquidmechanics.api.IReadOut;
import liquidmechanics.api.helpers.TankHelper;
import liquidmechanics.common.block.BlockReleaseValve;
import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;
import liquidmechanics.common.handlers.PipeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.prefab.implement.IRedstoneReceptor;

public class TileEntityReleaseValve extends TileEntity implements IPressure, IReadOut, IRedstoneReceptor, IInventory
{
    public LiquidData type = LiquidHandler.air;

    public TileEntity[] connected = new TileEntity[6];

    private List<PipeInstance> output = new ArrayList<PipeInstance>();
    private List<ILiquidTank> input = new ArrayList<ILiquidTank>();

    private int ticks = 0;

    public boolean isPowered = false;
    public boolean converted = false;
    public boolean isRestricted = false;

    private ItemStack[] inventory = new ItemStack[0];

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote && ticks++ == 10)
        {
            BlockReleaseValve.checkForPower(worldObj, xCoord, yCoord, zCoord);

            validateNBuildList();
        }
    }

    /**
     * Collects info about the surrounding 6 tiles and orders them into
     * drain-able(ITankContainer) and fill-able(TileEntityPipes) instances
     */
    public void validateNBuildList()
    {
        // cleanup
        this.connected = TankHelper.getSurroundings(worldObj, xCoord, yCoord, zCoord);
        this.input.clear();
        this.output.clear();
        // read surroundings
        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            TileEntity ent = connected[i];
            if (ent instanceof TileEntityPipe)
            {
                TileEntityPipe pipe = (TileEntityPipe) ent;
                if (this.isRestricted && pipe.type != this.type)
                {
                    connected[i] = null;
                }
                else if (pipe.stored.getLiquid() != null && pipe.stored.getLiquid().amount >= pipe.stored.getCapacity())
                {
                    connected[i] = null;
                }
                else
                {
                    this.output.add(new PipeInstance(LiquidHandler.getMeta(pipe.type), pipe, pipe.isUniversal));
                }
            }
            else if (ent instanceof ITankContainer)
            {
                ILiquidTank[] tanks = ((ITankContainer) connected[i]).getTanks(dir);
                for (int t = 0; t < tanks.length; t++)
                {
                    LiquidStack ll = tanks[t].getLiquid();
                    if (ll != null && ll.amount > 0 && ll.amount < tanks[t].getCapacity())
                    {
                        // if restricted check for type match
                        if (this.isRestricted)
                        {
                            if (LiquidHandler.isEqual(ll, this.type))
                            {
                                this.input.add(tanks[t]);
                            }
                        }
                        else
                        {
                            this.input.add(tanks[t]);
                        }
                    }
                }
            }
            else
            {
                connected[i] = null;
            }
        }
    }

    /**
     * removes liquid from a tank and fills it to a pipe
     * 
     * @param pipe
     *            - pipe being filled
     * @param drainee
     *            - LiquidTank being drained
     */
    public void drainTo(TileEntityPipe pipe, LiquidTank drainee)
    {

    }

    @Override
    public int presureOutput(LiquidData type, ForgeDirection dir)
    {
        if (type == this.type) { return LiquidData.getPressure(type); }
        return 0;
    }

    @Override
    public boolean canPressureToo(LiquidData type, ForgeDirection dir)
    {
        if (type == this.type)
            return true;
        return false;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        if (type == null) return "Error: No Type";
        String output = "";
        if (this.isRestricted)
        {
            output +="Outputting: "+ LiquidData.getName(type)+" ||";
        }else
        {
            output += " Outputting: All ||";
        }
        if (!this.isPowered)
        {
            output += " Running ";
        }
        else
        {
            output += " Offline ";
        }
        return output;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.type = LiquidHandler.get(nbt.getString("name"));
        this.isRestricted = nbt.getBoolean("restricted");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("restricted", this.isRestricted);
        nbt.setString("name", LiquidData.getName(type));
    }

    public void setType(LiquidData dm)
    {
        this.type = dm;

    }

    @Override
    public void onPowerOn()
    {
        this.isPowered = true;

    }

    @Override
    public void onPowerOff()
    {
        this.isPowered = false;

    }

    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.inventory[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.inventory[par1] != null)
        {
            ItemStack var3;

            if (this.inventory[par1].stackSize <= par2)
            {
                var3 = this.inventory[par1];
                this.inventory[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.inventory[par1].splitStack(par2);

                if (this.inventory[par1].stackSize == 0)
                {
                    this.inventory[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.inventory[par1] != null)
        {
            ItemStack var2 = this.inventory[par1];
            this.inventory[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.inventory[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName()
    {
        return "Release Valve";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;

    }

    @Override
    public void openChest()
    {

    }

    @Override
    public void closeChest()
    {

    }

    @Override
    public LiquidData getLiquidType()
    {
        return this.type;
    }

}
