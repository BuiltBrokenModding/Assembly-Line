package dark.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.builtbroken.common.User;

/** Used to define a users access to a terminal based object.
 *
 * @author DarkGuardsman */
public class TerminalUser extends User
{
    protected boolean isTempary = false;
    protected NBTTagCompound extraData;

    public TerminalUser(String username)
    {
        super(username);
    }

    public TerminalUser(EntityPlayer player)
    {
        super(player.username);
    }

    private TerminalUser()
    {

    }

    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("username", this.username);
        nbt.setCompoundTag("extraData", this.userData());
        return nbt;
    }

    public TerminalUser load(NBTTagCompound nbt)
    {
        this.username = nbt.getString("username");
        this.extraData = nbt.getCompoundTag("extraData");
        return this;
    }

    public static TerminalUser loadFromNBT(NBTTagCompound nbt)
    {
        return new TerminalUser().load(nbt);
    }

    public TerminalUser setTempary(boolean si)
    {
        this.isTempary = si;
        return this;
    }

    /** Used to add other data to the user */
    public NBTTagCompound userData()
    {
        if (this.extraData == null)
        {
            this.extraData = new NBTTagCompound();
        }
        return this.extraData;
    }

}
