package dark.SteamPower.boiler;

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
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IHeatCreator;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;
import dark.BasicUtilities.pipes.TileEntityPipe;

public class TileEntityBoiler extends TileEntity implements IReadOut, ITankContainer
{
    public int heat = 0;
    public int hullHeat = 0;
    public final int heatMax = 4500;
    public final int heatGain = 220;
    public final int heatNeed = 2000;
    public TileEntity[] connectedBlocks =
        { null, null, null, null, null, null };
    public int tankCount = 0;
    public int tickCount = 0;

    public LiquidTank SteamTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 10);
    public LiquidTank WaterTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 4);

    // -----------------------------
    // Update stuff
    // -----------------------------
    @Override
    public void updateEntity()
    {
        // update connection list used for rendering
        if (tickCount-- == 10)
        {
            this.connectedBlocks = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
            this.tankCount = 0;
            for (int i = 0; i < connectedBlocks.length; i++)
            {
                if (!(connectedBlocks[i] instanceof ILiquidTank || connectedBlocks[i] instanceof TileEntityPipe))
                {
                    connectedBlocks[i] = null;
                }
                if (connectedBlocks[i] != null)
                {
                    tankCount++;
                }
            }
            tickCount = 10;
        }
        if (!worldObj.isRemote)
        {
            TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
            if (ent instanceof IHeatCreator && ((IHeatCreator) ent).canCreatHeat(ForgeDirection.UP))
            {
                this.heat = Math.min(heat + Math.max(((IHeatCreator) ent).createHeat(ForgeDirection.UP),0),this.heatMax);
            }
            if(this.hullHeat < 10000)
            {
                this.hullHeat += this.heat;
                this.heat = 0;
            }else
            if(this.heat >= this.heatNeed)
            {
                this.WaterTank.drain(1, true);
                LiquidStack Stack = Liquid.STEAM.liquid.copy();
                Stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
                this.SteamTank.fill(Stack, true);
                this.heat -= this.heatNeed;
            }
        }

        super.updateEntity();
    }

    // -----------------------------
    // Liquid stuff
    // -----------------------------

    // -----------------------------
    // Data
    // -----------------------------

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        // liquid saving
        LiquidStack liquid = this.WaterTank.getLiquid();
        LiquidStack liquid2 = this.SteamTank.getLiquid();
        int water = 0;
        int steam = 0;
        if (liquid != null) water = liquid.amount;
        if (liquid2 != null) steam = liquid2.amount;
        nbt.setInteger("water", water);
        nbt.setInteger("steam", steam);
        // tempature saving
        nbt.setInteger("heat", this.heat);
        nbt.setInteger("hullHeat", this.hullHeat);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        // load up liquids
        int water = par1NBTTagCompound.getInteger("water");
        int steam = par1NBTTagCompound.getInteger("steam");
        this.WaterTank.setLiquid(new LiquidStack(Block.waterStill.blockID, water));
        this.SteamTank.setLiquid(new LiquidStack(BasicUtilitiesMain.SteamBlock.blockID, steam));
        // load up heat/temp levels
        this.heat = par1NBTTagCompound.getInteger("heat");
        this.hullHeat = par1NBTTagCompound.getInteger("hullHeat");
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        String output = " ";
        LiquidStack liquid = this.WaterTank.getLiquid();
        LiquidStack liquid2 = this.SteamTank.getLiquid();
        if (liquid != null) output += (liquid.amount / LiquidContainerRegistry.BUCKET_VOLUME) + "B WATER ";
        if (liquid2 != null) output += (liquid2.amount / LiquidContainerRegistry.BUCKET_VOLUME) + "B Steam ";
        if (liquid != null || liquid2 != null) return output;
        return "messurement error";

    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        int used = 0;

        if (resource.itemID == Block.waterStill.blockID)
        {
            used += WaterTank.fill(resource, doFill);
        }
        else if (resource.itemID == BasicUtilitiesMain.Steam.itemID)
        {
            used += SteamTank.fill(resource, doFill);
        }

        return used;
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (resource.itemID == Block.waterStill.blockID)
            return WaterTank.fill(resource, doFill);
        return 0;
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxEmpty, boolean doDrain)
    {
        return drain(2, maxEmpty, doDrain);
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxEmpty, boolean doDrain)
    {
        if (tankIndex == 1) return SteamTank.drain(maxEmpty, doDrain);
        return null;
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        return new ILiquidTank[]
            { SteamTank, WaterTank };
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
