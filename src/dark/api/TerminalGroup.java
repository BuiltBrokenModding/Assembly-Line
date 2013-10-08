package dark.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.builtbroken.common.Group;

/** Used by a terminal to track what users are part of each group. As well used to setup access
 * points to the terminal.
 *
 * @author DarkGuardsman */
public class TerminalGroup extends Group<TerminalUser>
{
    public TerminalGroup(String name, TerminalUser... js)
    {
        super(name, js);
    }

    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("groupName", this.name());
        NBTTagList usersTag = new NBTTagList();
        for (TerminalUser user : this.memebers)
        {
            if (!user.isTempary)
            {
                NBTTagCompound accessData = new NBTTagCompound();
                user.save(accessData);
                usersTag.appendTag(accessData);
            }
        }
        nbt.setTag("Users", usersTag);

        return nbt;
    }

    public void load(NBTTagCompound nbt)
    {
        this.setName(nbt.getString("groupName"));
        NBTTagList userList = nbt.getTagList("users");
        for (short i = 0; i < userList.tagCount(); ++i)
        {
            NBTTagCompound var4 = (NBTTagCompound) userList.tagAt(i);
            memebers.add(TerminalUser.loadFromNBT(var4));
        }
    }
}
