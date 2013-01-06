package liquidmechanics.common.tileentity;

import liquidmechanics.api.IReadOut;
import liquidmechanics.api.IPressure;
import liquidmechanics.api.helpers.PipeColor;
import liquidmechanics.api.helpers.connectionHelper;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;
import liquidmechanics.common.handlers.UpdateConverter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityPipe extends TileEntity implements ITankContainer, IReadOut
{
    private PipeColor color = PipeColor.NONE;

    private int count = 40;
    private int count2, presure = 0;

    public boolean converted = false;
    public boolean isUniversal = false;

    public TileEntity[] connectedBlocks = new TileEntity[6];

    public LiquidTank stored = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 2);

    @Override
    public void updateEntity()
    {

        this.validataConnections();
        this.color = PipeColor.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        if (!worldObj.isRemote && ++count >= 40)
        {
            count = 0;
            this.updatePressure();

            LiquidStack stack = stored.getLiquid();
            if (stack != null && stack.amount >= 0)
            {

                for (int i = 0; i < 6; i++)
                {
                    ForgeDirection dir = ForgeDirection.getOrientation(i);
                    int moved = 0;
                    if (connectedBlocks[i] instanceof ITankContainer)
                    {
                        if (connectedBlocks[i] instanceof TileEntityPipe)
                        {
                            if (((TileEntityPipe) connectedBlocks[i]).presure < this.presure)
                            {
                                moved = ((TileEntityPipe) connectedBlocks[i]).stored.fill(stack, true);
                            }

                        }
                        else
                        {
                            moved = ((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true);
                        }
                    }
                    stored.drain(moved, true);
                    // FMLLog.warning("Moved "+moved+ " "+ i);
                    if (stack == null || stack.amount <= 0)
                    {
                        break;
                    }
                }
            }
        }

    }

    /**
     * gets the current color mark of the pipe
     */
    public PipeColor getColor()
    {
        return this.color;
    }

    /**
     * sets the current color mark of the pipe
     */
    public void setColor(PipeColor cc)
    {
        this.color = cc;
    }

    /**
     * sets the current color mark of the pipe
     */
    public void setColor(int i)
    {
        if (i < PipeColor.values().length)
        {
            this.color = PipeColor.values()[i];
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        UpdateConverter.convert(this, nbt);
        LiquidStack liquid = new LiquidStack(0, 0, 0);
        liquid.readFromNBT(nbt.getCompoundTag("stored"));
        stored.setLiquid(liquid);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (stored.getLiquid() != null)
        {
            nbt.setTag("stored", stored.getLiquid().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        LiquidStack stack = this.stored.getLiquid();
        if (stack != null) { return (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + "/" + (this.stored.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME) + " " + LiquidHandler.get(stack).getName() + " @ " + this.presure + "p"; }

        return "Empty";
    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        if (resource == null) { return 0; }
        LiquidStack stack = stored.getLiquid();
        if (color != PipeColor.NONE)
        {
            if (color != PipeColor.get(LiquidHandler.get(resource)) || !LiquidHandler.isEqual(stack, resource))
            {
                this.causeMix(stack, resource);
            }
            else
            {
                this.fill(0, resource, doFill);
            }

        }
        else
        {
            if (stack != null && !LiquidHandler.isEqual(stack, resource))
            {
                this.causeMix(stack, resource);
            }
            else
            {
                this.fill(0, resource, doFill);
            }
        }

        return 0;
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (tankIndex != 0 || resource == null)
            return 0;

        return stored.fill(resource, doFill);
    }

    public int causeMix(LiquidStack stored, LiquidStack fill)
    {
        if (stored == null || fill == null) { return 0; }
        // water flowing into lava creates obby
        if (LiquidHandler.isEqual(stored, LiquidHandler.lava) && LiquidHandler.isEqual(fill, LiquidHandler.water))
        {
            worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, Block.obsidian.blockID);
            return fill.amount;
        }// lava flowing into water creates cobble
        else if (LiquidHandler.isEqual(stored, LiquidHandler.water) && LiquidHandler.isEqual(fill, LiquidHandler.lava))
        {
            worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, Block.cobblestone.blockID);
            return fill.amount;
        }
        else
        // anything else creates waste liquid
        {
            int f = this.stored.fill(new LiquidStack(stored.itemID, fill.amount, stored.itemMeta), true);
            int s = this.stored.getLiquid().amount;
            LiquidStack stack = LiquidHandler.getStack(LiquidHandler.waste, s);
            this.stored.setLiquid(stack);
            return f;
        }
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        return new ILiquidTank[] { this.stored };
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        return null;
    }

    /**
     * collects and sorts the surrounding TE for valid connections
     */
    public void validataConnections()
    {
        this.connectedBlocks = connectionHelper.getSurroundings(worldObj, xCoord, yCoord, zCoord);
        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            TileEntity ent = connectedBlocks[i];
            if (ent instanceof ITankContainer)
            {
                if (this.color != PipeColor.NONE)
                {
                    if (ent instanceof TileEntityPipe && color != ((TileEntityPipe) ent).getColor())
                    {
                        connectedBlocks[i] = null;
                    }
                    else if (ent instanceof TileEntityTank && color != ((TileEntityTank) ent).getColor())
                    {
                        connectedBlocks[i] = null;
                    }
                }
            }
            else if (ent instanceof IPressure)
            {
                if (!((IPressure) ent).canPressureToo(color.getLiquidData(), dir))
                {
                    connectedBlocks[i] = null;
                }
            }
            else
            {
                connectedBlocks[i] = null;
            }
        }
    }

    /**
     * updates this units pressure level using the pipe/machines around it
     */
    public void updatePressure()
    {
        int highestPressure = 0;
        this.presure = 0;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);

            if (connectedBlocks[i] instanceof TileEntityPipe)
            {
                if (((TileEntityPipe) connectedBlocks[i]).getPressure() > highestPressure)
                {
                    highestPressure = ((TileEntityPipe) connectedBlocks[i]).getPressure();
                }
            }
            if (connectedBlocks[i] instanceof IPressure && ((IPressure) connectedBlocks[i]).canPressureToo(color.getLiquidData(), dir))
            {

                int p = ((IPressure) connectedBlocks[i]).presureOutput(color.getLiquidData(), dir);
                if (p > highestPressure)
                    highestPressure = p;
            }
        }
        this.presure = highestPressure - 1;
    }

    public int getPressure()
    {
        return this.presure;
    }
}
