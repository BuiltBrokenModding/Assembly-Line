package liquidmechanics.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import liquidmechanics.api.IColorCoded;
import liquidmechanics.api.IReadOut;
import liquidmechanics.api.helpers.ColorCode;
import liquidmechanics.api.helpers.connectionHelper;
import liquidmechanics.api.liquids.IPressure;
import liquidmechanics.api.liquids.LiquidData;
import liquidmechanics.api.liquids.LiquidHandler;
import liquidmechanics.common.block.BlockReleaseValve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.implement.IRedstoneReceptor;

public class TileEntityReleaseValve extends TileEntity implements IPressure, IReadOut, IRedstoneReceptor, IInventory
{
    public boolean[] allowed = new boolean[ColorCode.values().length - 1];
    public TileEntity[] connected = new TileEntity[6];

    private List<TileEntityPipe> output = new ArrayList<TileEntityPipe>();
    private List<ILiquidTank> input = new ArrayList<ILiquidTank>();

    private int ticks = 0;

    public boolean isPowered = false;
    public boolean converted = false;

    private ItemStack[] inventory = new ItemStack[0];

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        connected = connectionHelper.getSurroundingTileEntities(this);
        for(int i =0; i < 6;i++)
        {
            if(connected[i] instanceof ITankContainer)
            {
                if(connected[i] instanceof IColorCoded && !this.canConnect(((IColorCoded) connected[i]).getColor()))
                {
                    connected[i] = null;
                }
            }else 
            {
                
                connected[i] = null;
            }
        }
        if (!this.worldObj.isRemote && ticks++ >= 20)
        {
            ticks = 0;
            BlockReleaseValve.checkForPower(worldObj, xCoord, yCoord, zCoord);
            validateNBuildList();
            // start the draining process
            if (this.input.size() > 0 && this.output.size() > 0)
            {
                for (ILiquidTank tank : input)
                {

                    if (tank.getLiquid() != null && tank.getLiquid().amount > 0)
                    {
                        //FMLLog.warning("Tank: " + LiquidHandler.getName(tank.getLiquid()) + " Vol: " + tank.getLiquid().amount);
                        TileEntityPipe pipe = this.findValidPipe(tank.getLiquid());
                        if (pipe != null)
                        {
                            ILiquidTank tankP = pipe.getTanks(ForgeDirection.UNKNOWN)[0];
                           //FMLLog.warning("Pipe: " + pipe.getColor() + " Vol: " + (tankP.getLiquid() != null ? tankP.getLiquid().amount : 0000));
                            int drain = pipe.fill(ForgeDirection.UNKNOWN, tank.getLiquid(), true);
                            tank.drain(drain, true);
                        }
                    }
                }
            }

        }
    }

    /** used to find a valid pipe for filling of the liquid type */
    public TileEntityPipe findValidPipe(LiquidStack stack)
    {
      
            // find normal color selective pipe first
            for (TileEntityPipe pipe : output)
            {
                ILiquidTank tank = pipe.getTanks(ForgeDirection.UNKNOWN)[0];
                if (LiquidHandler.isEqual(pipe.getColor().getLiquidData().getStack(),stack) && (tank.getLiquid() == null || tank.getLiquid().amount < tank.getCapacity()))
                {
                    //
                    return pipe;
                }
            }
            // if no color selective pipe is found look for generic pipes
            for (TileEntityPipe pipe : output)
            {
                if (pipe.getColor() == ColorCode.NONE) { return pipe; }
            }
        
        return null;
    }

    /** sees if it can connect to a pipe of some color */
    public boolean canConnect(ColorCode cc)
    {
        if (this.isRestricted())
        {
            for (int i = 0; i < this.allowed.length; i++)
            {
                if (i == cc.ordinal()) { return allowed[i]; }
            }
        }
        return true;
    }

    /** if any of allowed list is true
     * 
     * @return true */
    public boolean isRestricted()
    {
        for (int i = 0; i < this.allowed.length; i++)
        {
            if (allowed[i]) { return true; }
        }
        return false;
    }

    /** checks a liquidstack against its color code
     * 
     * @param stack
     * @return */
    public boolean canAcceptLiquid(LiquidStack stack)
    {
        if (!this.isRestricted()) { return true; }
        return canConnect(ColorCode.get(LiquidHandler.get(stack)));
    }

    /** Collects info about the surrounding 6 tiles and orders them into
     * drain-able(ITankContainer) and fill-able(TileEntityPipes) instances */
    public void validateNBuildList()
    {
        // cleanup
        this.connected = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
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
                ILiquidTank tank = pipe.getTanks(ForgeDirection.UNKNOWN)[0];
                if (this.isRestricted() && this.canConnect(pipe.getColor()))
                {
                    connected[i] = null;
                }
                else if (tank.getLiquid() != null && tank.getLiquid().amount >= tank.getCapacity())
                {
                    connected[i] = null;
                }
                else
                {
                    this.output.add(pipe);
                }
            }
            else if (ent instanceof ITankContainer)
            {
                ILiquidTank[] tanks = ((ITankContainer) connected[i]).getTanks(dir);
                for (int t = 0; t < tanks.length; t++)
                {
                    LiquidStack ll = tanks[t].getLiquid();
                    if (ll != null && ll.amount > 0 && ll.amount > 0)
                    {
                        if (this.canAcceptLiquid(ll))
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

    @Override
    public int presureOutput(LiquidData type, ForgeDirection dir)
    {
        if (type == null) return 0;
        if (this.canConnect(type.getColor())) { return type.getPressure(); }
        return 0;
    }

    @Override
    public boolean canPressureToo(LiquidData type, ForgeDirection dir)
    {
        if (type == null) return false;
        if (this.canConnect(type.getColor())) return true;
        return false;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        // TODO maybe debug on # of connected units of input/output
        String output = "";
        if (this.isRestricted())
        {
            output += "Output: Restricted and";
        }
        else
        {
            output += " Output: UnRestricted and";
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
        for (int i = 0; i < this.allowed.length; i++)
        {
            allowed[i] = nbt.getBoolean("allowed" + i);
        }
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        for (int i = 0; i < this.allowed.length; i++)
        {
            nbt.setBoolean("allowed" + i, allowed[i]);
        }
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

    /** Returns the stack in slot i */
    public ItemStack getStackInSlot(int par1)
    {
        return this.inventory[par1];
    }

    /** Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack. */
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

    /** When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI. */
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

    /** Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections). */
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

}
