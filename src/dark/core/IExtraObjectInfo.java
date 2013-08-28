package dark.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import dark.core.helpers.Pair;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;

/** Used to handle info about the block that would normally be handled by the mod main class. Use the
 * BlockRegistry in order for these methods to be called on load of the mod.
 *
 * @author DarkGuardsman */
public interface IExtraObjectInfo
{

    /** List of all tileEntities this block needs */
    public void getTileEntities(int blockID, Set<Pair<String, TileEntity>> list);

    /** True will cause a config file to be generated for this block */
    public boolean hasExtraConfigs();

    /** Loads the config file for this block */
    public void loadExtraConfigs(Configuration config);

    /** Optional way to handle recipes based out of the block/item class */
    public void loadRecipes();

    /** Loads the names used to reference this item in a recipe */
    public void loadOreNames();

}
