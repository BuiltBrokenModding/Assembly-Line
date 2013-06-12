package dark.core.faction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import dark.helpers.Pair;
import dark.helpers.Time;
import dark.library.access.AccessLevel;
import dark.library.access.GlobalAccessManager;
import dark.library.access.UserAccess;

/**
 * Class used to track and manage a player/NPC faction
 * 
 * @author DarkGuardsman
 */
public class FactionInstance
{
	public List<UserAccess> userList;
	public String name;
	public String creator;
	public String date = "unkown";
	public String prefix;	

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

	public boolean isEqual(FactionInstance faction)
	{

		return faction != null && faction.name.equalsIgnoreCase(this.name) && faction.creator.equalsIgnoreCase(this.creator);
	}

	public boolean isInvalid()
	{
		return this.wasPlayerCreated() && this.userList.isEmpty();
	}

	public boolean wasPlayerCreated()
	{
		return !this.creator.equalsIgnoreCase("World");
	}
}
