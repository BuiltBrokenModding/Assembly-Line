package liquidmechanics.common.tileentity;

import java.util.Random;

import liquidmechanics.api.IPipe;
import liquidmechanics.api.IReadOut;
import liquidmechanics.api.helpers.ColorCode;
import liquidmechanics.api.helpers.connectionHelper;
import liquidmechanics.api.liquids.IPressure;
import liquidmechanics.api.liquids.LiquidHandler;
import liquidmechanics.common.handlers.UpdateConverter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityPipe extends TileEntity implements ITankContainer, IReadOut, IPipe
{
    private ColorCode color = ColorCode.NONE;

    private int count = 20;
    private int count2, presure = 0;

    public boolean converted = false;
    public boolean isUniversal = false;

    public TileEntity[] connectedBlocks = new TileEntity[6];

    private LiquidTank stored = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 2);

    @Override
    public void updateEntity()
    {

        this.validataConnections();
        this.color = ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        if (++count >= 20)
        {
            count = 0;
            this.updatePressure();
            if (this.worldObj.isRemote)
            {
                this.randomDisplayTick();
            }
            LiquidStack stack = stored.getLiquid();
            if (!worldObj.isRemote && stack != null && stack.amount >= 0)
            {

                for (int i = 0; i < 6; i++)
                {
                    ForgeDirection dir = ForgeDirection.getOrientation(i);

                    if (connectedBlocks[i] instanceof ITankContainer)
                    {
                        if (connectedBlocks[i] instanceof TileEntityPipe)
                        {
                            if (((TileEntityPipe) connectedBlocks[i]).presure < this.presure)
                            {
                                stored.drain(((TileEntityPipe) connectedBlocks[i]).fill(dir, stack, true), true);
                            }

                        }
                        else if (connectedBlocks[i] instanceof TileEntityTank && ((TileEntityTank) connectedBlocks[i]).getColor() == this.color)
                        {
                            if (dir == ForgeDirection.UP && !color.getLiquidData().getCanFloat())
                            {
                                /* do nothing */
                            }
                            else if (dir == ForgeDirection.DOWN && color.getLiquidData().getCanFloat())
                            {
                                /* do nothing */
                            }
                            else
                            {
                                stored.drain(((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true), true);
                            }
                        }
                        else
                        {
                            stored.drain(((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true), true);
                        }
                    }

                    if (stack == null || stack.amount <= 0)
                    {
                        break;
                    }
                }
            }
        }

    }

    public void randomDisplayTick()
    {
        Random random = new Random();
        LiquidStack stack = stored.getLiquid();
        if (stack != null && random.nextInt(10) == 0)
        {
            // TODO align this with the pipe model so not to drip where there is
            // no pipe
            double xx = (double) ((float) xCoord + random.nextDouble());
            double zz = (double) yCoord + .3D;
            double yy = (double) ((float) zCoord + random.nextDouble());

            if (ColorCode.get(stack) != ColorCode.RED)
            {
                worldObj.spawnParticle("dripWater", xx, zz, yy, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                worldObj.spawnParticle("dripLava", xx, zz, yy, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * gets the current color mark of the pipe
     */
    @Override
    public ColorCode getColor()
    {
        return this.color;
    }

    /**
     * sets the current color mark of the pipe
     */
    @Override
    public void setColor(Object cc)
    {
        this.color = ColorCode.get(cc);
    }

    /**
     * sets the current color mark of the pipe
     */
    public void setColor(int i)
    {
        if (i < ColorCode.values().length)
        {
            this.color = ColorCode.values()[i];
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

        return "Empty" + " @ " + this.presure + "p";
    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        if (resource == null) { return 0; }
        LiquidStack stack = stored.getLiquid();
        if (color != ColorCode.NONE)
        {

            if (stack == null || LiquidHandler.isEqual(resource, this.color.getLiquidData()))
            {
                return this.fill(0, resource, doFill);
            }
            else
            {
                return this.causeMix(stack, resource);
            }

        }
        else
        {
            if (stack == null || LiquidHandler.isEqual(stack, resource))
            {
                return this.fill(0, resource, doFill);
            }
            else
            {
                return this.causeMix(stack, resource);
            }
        }
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (tankIndex != 0 || resource == null) { return 0; }

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
        this.connectedBlocks = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            TileEntity ent = connectedBlocks[i];
            if (ent instanceof ITankContainer)
            {
                if (ent instanceof TileEntityPipe && color != ((TileEntityPipe) ent).getColor())
                {
                    connectedBlocks[i] = null;
                }
                // TODO switch side catch for IPressure
                if (this.color != ColorCode.NONE && ent instanceof TileEntityTank && ((TileEntityTank) ent).getColor() != ColorCode.NONE && color != ((TileEntityTank) ent).getColor())
                {
                    connectedBlocks[i] = null;
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
