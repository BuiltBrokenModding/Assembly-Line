package dark.core.prefab.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.builtbroken.common.science.units.UnitHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

public class NBTFileLoader
{
    /** Saves NBT data in the world folder.
     *
     * @return True on success. */
    public static boolean saveData(File saveDirectory, String filename, NBTTagCompound data)
    {
        try
        {
            File tempFile = new File(saveDirectory, filename + "_tmp.dat");
            File file = new File(saveDirectory, filename + ".dat");

            CompressedStreamTools.writeCompressed(data, new FileOutputStream(tempFile));

            if (file.exists())
            {
                file.delete();
            }

            tempFile.renameTo(file);

            FMLLog.fine("Saved " + filename + " NBT data file successfully.");
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Failed to save " + filename + ".dat!");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveData(String filename, NBTTagCompound data)
    {
        return saveData(getSaveDirectory(MinecraftServer.getServer().getFolderName()), filename, data);
    }

    /** Reads NBT data from the world folder.
     *
     * @return The NBT data */
    public static NBTTagCompound loadData(File saveDirectory, String filename)
    {
        try
        {
            File file = new File(saveDirectory, filename + ".dat");

            if (file.exists())
            {
                FMLLog.fine("Loaded " + filename + " data.");
                return CompressedStreamTools.readCompressed(new FileInputStream(file));
            }
            else
            {
                FMLLog.fine("Created new " + filename + " data.");
                return new NBTTagCompound();
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to load " + filename + ".dat!");
            e.printStackTrace();
            return null;
        }
    }

    public static NBTTagCompound loadData(String filename)
    {
        return loadData(getSaveDirectory(MinecraftServer.getServer().getFolderName()), filename);
    }

    public static File getSaveDirectory(String worldName)
    {
        File parent = getBaseDirectory();

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            parent = new File(getBaseDirectory(), "saves" + File.separator);
        }

        return new File(parent, worldName + File.separator);
    }

    public static File getBaseDirectory()
    {
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            FMLClientHandler.instance().getClient();
            return Minecraft.getMinecraft().mcDataDir;
        }
        else
        {
            return new File(".");
        }
    }

    /** Used to save an object without knowing what the object is exactly. Supports most
     * NBTTagCompound save methods including some special cases. Which includes boolean being saves
     * as a string so it can be loaded as a boolean from an object save.
     *
     * @param tag - NBTTagCompound to save the tag too
     * @param key - name to save the object as
     * @param value - the actual object
     * @return the tag when done saving too i */
    public static NBTTagCompound saveObject(NBTTagCompound tag, String key, Object value)
    {
        if (value instanceof Float)
        {
            tag.setFloat(key, (float) value);
        }
        else if (value instanceof Double)
        {
            tag.setDouble(key, (double) value);
        }
        else if (value instanceof Integer)
        {
            tag.setInteger(key, (int) value);
        }
        else if (value instanceof String)
        {
            tag.setString(key, (String) value);
        }
        else if (value instanceof Short)
        {
            tag.setShort(key, (short) value);
        }
        else if (value instanceof Byte)
        {
            tag.setByte(key, (Byte) value);
        }
        else if (value instanceof Long)
        {
            tag.setLong(key, (long) value);
        }
        else if (value instanceof Boolean)
        {
            tag.setString(key, "NBT:SAVE:BOOLEAN:" + value);
        }
        else if (value instanceof NBTBase)
        {
            tag.setTag(key, (NBTBase) value);
        }
        else if (value instanceof String)
        {
            tag.setString(key, (String) value);
        }
        else if (value instanceof byte[])
        {
            tag.setByteArray(key, (byte[]) value);
        }
        else if (value instanceof int[])
        {
            tag.setIntArray(key, (int[]) value);
        }
        else if (value instanceof NBTTagCompound)
        {
            tag.setCompoundTag(key, (NBTTagCompound) value);
        }
        else if (value instanceof Vector2)
        {
            tag.setString(key, "NBT:SAVE:VECTOR:2:" + ((Vector2) value).x + ":" + ((Vector2) value).y);
        }
        else if (value instanceof Vector3)
        {
            tag.setString(key, "NBT:SAVE:VECTOR:3:" + ((Vector3) value).x + ":" + ((Vector3) value).y + ":" + ((Vector3) value).z);
        }
        return tag;

    }

    /** Reads an unknown object with a known name from NBT
     *
     * @param tag - tag to read the value from
     * @param key - name of the value
     * @param suggestionValue - value to return in case nothing is found
     * @return object or suggestionValue if nothing is found */
    public static Object loadObject(NBTTagCompound tag, String key)
    {
        if (tag != null && key != null)
        {
            NBTBase saveTag = tag.getTag(key);
            if (saveTag instanceof NBTTagFloat)
            {
                return tag.getFloat(key);
            }
            else if (saveTag instanceof NBTTagDouble)
            {
                return tag.getDouble(key);
            }
            else if (saveTag instanceof NBTTagInt)
            {
                return tag.getInteger(key);
            }
            else if (saveTag instanceof NBTTagString)
            {
                String str = tag.getString(key);
                if (str.startsWith("NBT:SAVE:"))
                {
                    str.replaceAll("NBT:SAVE:", "");
                    if (str.startsWith("BOOLEAN:"))
                    {
                        str.replaceAll("BOOLEAN:", "");
                        if (str.equalsIgnoreCase("true"))
                        {
                            return true;
                        }
                        if (str.equalsIgnoreCase("false"))
                        {
                            return false;
                        }
                    }
                    if (str.startsWith("VECTOR:"))
                    {
                        str.replaceAll("VECTOR:", "");
                        String[] nums = str.split(":");
                        if (UnitHelper.tryToParseDouble(nums[0]) == 2)
                        {
                            return new Vector2(UnitHelper.tryToParseDouble(nums[1]), UnitHelper.tryToParseDouble(nums[2]));
                        }
                        if (UnitHelper.tryToParseDouble(nums[0]) == 3)
                        {
                            return new Vector3(UnitHelper.tryToParseDouble(nums[1]), UnitHelper.tryToParseDouble(nums[2]), UnitHelper.tryToParseDouble(nums[3]));
                        }
                    }
                    return null;
                }
                return str;
            }
            else if (saveTag instanceof NBTTagShort)
            {
                return tag.getShort(key);
            }
            else if (saveTag instanceof NBTTagByte)
            {
                return tag.getByte(key);
            }
            else if (saveTag instanceof NBTTagLong)
            {
                return tag.getLong(key);
            }
            else if (saveTag instanceof NBTBase)
            {
                return tag.getTag(key);
            }
            else if (saveTag instanceof NBTTagByteArray)
            {
                return tag.getByteArray(key);
            }
            else if (saveTag instanceof NBTTagIntArray)
            {
                return tag.getIntArray(key);
            }
            else if (saveTag instanceof NBTTagCompound)
            {
                return tag.getCompoundTag(key);
            }
        }
        return null;
    }
}
