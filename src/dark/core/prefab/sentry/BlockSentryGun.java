package dark.core.prefab.sentry;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import com.builtbroken.common.Pair;

import dark.core.ModObjectRegistry;
import dark.core.prefab.ItemBlockHolder;
import dark.core.prefab.machine.BlockMachine;
import dark.machines.DarkMain;

/** Actual block that is the sentry gun. Mainly a place holder as the sentry guns need something to
 * exist threw that is not an entity. Renders need to still be handled by the respective mod.
 * Especial item renders as this just creates the block and reservers the meta slot
 *
 * @author DarkGuardsman */
public class BlockSentryGun extends BlockMachine
{
    static TileEntitySentry[] sentryGuns = new TileEntitySentry[16];
    static String[] sentryGunNames = new String[16];
    static int registeredGuns = 0;
    static int[] sentryBlockIds = new int[1];

    public BlockSentryGun(int v)
    {
        super(DarkMain.CONFIGURATION, "DMSentryGun" + v, Material.iron);
        this.setResistance(100);
        this.setHardness(100);
    }

    /** Make sure to let others know your claimed IDs or add configs so we don't run into issues. As
     * well the slot is the meta data value the gun will be created with. Any number over 15 will
     * create another block then use its 16 meta values. */
    public static void registerSentry(int slot, String name, Class<? extends TileEntitySentry> clazz)
    {
        try
        {
            //Expands the sentry gun list as needed;
            if (slot > sentryGuns.length)
            {
                int b = (slot / 16) + 1;
                sentryBlockIds = new int[b];
                TileEntitySentry[] guns = new TileEntitySentry[b * 16];
                for (int s = 0; s < sentryGuns.length; s++)
                {
                    guns[s] = sentryGuns[s];
                }
                sentryGuns = guns;
            }
            if (clazz != null && sentryGuns[slot] == null)
            {
                int b = (slot / 16);
                sentryBlockIds[b] = -1;
                sentryGuns[slot] = clazz.newInstance();
                sentryGunNames[slot] = name;
                registeredGuns++;
            }
            else if (sentryGuns[slot] != null)
            {
                throw new IllegalArgumentException("Sentry gun slot " + slot + " is already occupied by " + sentryGuns[slot] + " when adding " + clazz);
            }

        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Called by the core when this block is about to be created. If there are no registered sentry
     * guns then this block doesn't exist. */
    public static void createAndRegister()
    {
        if (registeredGuns > 0)
        {
            for (int b = 0; b < sentryBlockIds.length; b++)
            {
                if (sentryBlockIds[b] == -1)
                {
                    Block block = new BlockSentryGun(b);
                    if (block != null)
                    {
                        ModObjectRegistry.registredBlocks.put(block, "DMSentryGun" + b);
                        ModObjectRegistry.proxy.registerBlock(block, ItemBlockHolder.class, "DMSentryGun" + b, DarkMain.MOD_ID);
                        ModObjectRegistry.finishCreation(block, null);
                        sentryBlockIds[b] = block.blockID;
                    }
                }
            }
        }
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        for (int t = 0; t < sentryGuns.length; t++)
        {
            if (sentryGuns[t] != null)
            {
                list.add(new Pair("DMSentry_" + sentryGunNames[t], sentryGuns[t].getClass()));
            }
        }
    }
}
