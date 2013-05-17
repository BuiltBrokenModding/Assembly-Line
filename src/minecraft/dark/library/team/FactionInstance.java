package dark.library.team;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import dark.library.access.AccessLevel;
import dark.library.access.GlobalAccessManager;
import dark.library.access.UserAccess;
import dark.library.helpers.Pair;
import dark.library.helpers.Time;

/**
 * Class used to track and manage a player/NPC faction
 * 
 * @author DarkGuardsman
 */
public class FactionInstance
{
	List<UserAccess> userList;
	String name;
	String creator;
	String date = "unkown";
	String prefix;

	public FactionInstance(String prefix, String name, String maker, Pair<String, Date> date)
	{
		if (this.name == null || this.name.isEmpty())
		{
			this.name = "Faction" + date.getValue().getMonth() + date.getValue().getDay();
		}
		else
		{
			this.name = name;
		}

		if (maker == null || maker.isEmpty())
		{
			this.creator = "World";
		}
		else
		{
			this.creator = maker;
		}

		if (prefix == null)
		{
			this.prefix = name.substring(0, 4);
		}
		else if (prefix.length() > 6)
		{
			this.prefix = prefix.substring(0, 6);
		}
		else
		{
			this.prefix = prefix;
		}

		if (date == null)
		{
			date = Time.getCurrentTimeStamp();
		}
		this.date = date.getKey();

		userList = GlobalAccessManager.getOrCreateList(name, maker);
		if (userList == null)
		{
			userList = new ArrayList<UserAccess>();
			userList.add(new UserAccess(this.creator, AccessLevel.OWNER, true));
		}
	}

	public String getCreationDate()
	{
		return this.date;
	}

	/**
	 * The person who original created the faction
	 */
	public String getCreator()
	{
		return (creator != null && !creator.isEmpty()) ? this.creator : "World";
	}

	/**
	 * The name of the faction
	 */
	public String getName()
	{
		return (name != null && !name.isEmpty()) ? this.name : "FactionName";
	}

	public NBTTagCompound write()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", this.getName());
		tag.setString("creator", this.creator);
		return tag;
	}
	
	public void read(NBTTagCompound tag)
	{
		
	}
}
