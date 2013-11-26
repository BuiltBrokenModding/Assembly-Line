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
import dark.core.prefab.machine.TileEntityMachine;

/** Simple compact generator designed to only power a few machines at a time
 * 
 * @author DarkGuardsman */
public class TileEntitySteamGen extends TileEntityMachine
{
    /** Maximum amount of energy needed to generate electricity */
    public static float MAX_GENERATE_WATTS = 0.5f;

    /** Amount of heat the coal generator needs before generating electricity. */
    public static final float MIN_GENERATE_WATTS = MAX_GENERATE_WATTS * 0.1f;

    private static float BASE_ACCELERATION = 0.000001f;
    private static float BASE_DECCELERATION = 0.008f;

    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace
     * burning for */
    public int itemCookTime = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
    }

        
}