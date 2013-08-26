package dark.core.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IElectricalStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.IToolReadOut;
import dark.api.IToolReadOut.EnumTools;
import dark.core.DarkMain;
import dark.core.helpers.FluidHelper;

public class ItemTools extends ItemBasic
{
    Icon pipeGuage, multiMeter;

    public ItemTools(int id, Configuration config)
    {
        super(id, "DMTools", config);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.pipeGuage = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + "PipeGauge");
        this.multiMeter = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + "multi-Meter");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int meta)
    {
        if (meta == 0)
        {
            return pipeGuage;
        }
        if (meta == 1)
        {
            return multiMeter;
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        int meta = itemStack.getItemDamage();
        if (meta == 0)
        {
            return "item." + "PipeGauge";
        }
        else if (meta == 1)
        {
            return "item." + "MultiMeter";
        }
        return "item." + this.getUnlocalizedName() + "." + meta;
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this, 1, 0));
        par3List.add(new ItemStack(this, 1, 1));
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            EnumTools tool = EnumTools.get(itemStack.getItemDamage());

            if (tool != null)
            {
                if (tileEntity instanceof IToolReadOut)
                {
                    String output = ((IToolReadOut) tileEntity).getMeterReading(player, ForgeDirection.getOrientation(side), EnumTools.PIPE_GUAGE);
                    if (output != null && !output.isEmpty())
                    {
                        if (output.length() > 100)
                        {
                            output = output.substring(0, 100);
                        }
                        output.trim();
                        player.sendChatToPlayer(ChatMessageComponent.func_111066_d("ReadOut> " + output));
                        return true;
                    }
                }
                if (tool == EnumTools.PIPE_GUAGE)
                {
                    if (tileEntity instanceof IFluidHandler)
                    {
                        FluidTankInfo[] tanks = ((IFluidHandler) tileEntity).getTankInfo(ForgeDirection.getOrientation(side));
                        if (tanks != null)
                        {
                            player.sendChatToPlayer(ChatMessageComponent.func_111066_d("FluidHandler> Side:" + ForgeDirection.getOrientation(side).toString() + " Tanks:" + tanks.length));
                            for (FluidStack stack : FluidHelper.getFluidList(tanks))
                            {
                                player.sendChatToPlayer(ChatMessageComponent.func_111066_d("Fluid>" + stack.amount + "mb of " + stack.getFluid().getName()));
                            }
                            return true;
                        }
                    }
                }
                if (tool == EnumTools.MULTI_METER)
                {
                    player.sendChatToPlayer(ChatMessageComponent.func_111066_d("Side>" + ForgeDirection.getOrientation(side).toString()));
                    boolean out = false;
                    if (tileEntity instanceof IElectrical)
                    {
                        float demand = ((IElectrical) tileEntity).getRequest(ForgeDirection.getOrientation(side).getOpposite());
                        float provide = ((IElectrical) tileEntity).getProvide(ForgeDirection.getOrientation(side).getOpposite());
                        player.sendChatToPlayer(ChatMessageComponent.func_111066_d("   Voltage>" + ((IElectrical) tileEntity).getVoltage()));
                        if (demand > 0)
                        {
                            player.sendChatToPlayer(ChatMessageComponent.func_111066_d(String.format("   RequiredWatts> %1$.2fW", demand)));
                        }
                        if (provide > 0)
                        {
                            player.sendChatToPlayer(ChatMessageComponent.func_111066_d(String.format("   AvailableWatts> %1$.2fW", provide)));
                        }
                        out = true;
                    }
                    if (tileEntity instanceof IElectricalStorage)
                    {
                        player.sendChatToPlayer(ChatMessageComponent.func_111066_d(String.format("   EnergyStored> %1$.2fW of %1$.2fW max", ((IElectricalStorage) tileEntity).getEnergyStored(), ((IElectricalStorage) tileEntity).getMaxEnergyStored())));
                        out = true;
                    }
                    if (tileEntity instanceof IConductor)
                    {
                        player.sendChatToPlayer(ChatMessageComponent.func_111066_d(String.format("   Resistance> %1$.2fW | AmpMax>     %1$.2fW", ((IConductor) tileEntity).getResistance(), ((IConductor) tileEntity).getCurrentCapacity())));

                        if (((IConductor) tileEntity).getNetwork() != null)
                        {
                            player.sendChatToPlayer(ChatMessageComponent.func_111066_d(String.format("   Network>WattRequired> %1$.2fW | TotalResistance> %1$.2fW", ((IConductor) tileEntity).getNetwork().getRequest(), ((IConductor) tileEntity).getNetwork().getTotalResistance())));
                        }
                    }
                    if (!out)
                    {
                        player.sendChatToPlayer(ChatMessageComponent.func_111066_d("   Error failed to find wire connections nodes"));

                    }
                }
            }

        }

        return false;
    }

}
