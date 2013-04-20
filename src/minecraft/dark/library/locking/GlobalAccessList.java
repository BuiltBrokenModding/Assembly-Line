package dark.library.locking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.prefab.flag.NBTFileLoader;

public class GlobalAccessList
{

	/** Hash map of loaded lists **/
	private static Map<String, List<UserAccess>> globalUserLists = new HashMap<String, List<UserAccess>>();
	/** Master save NBT that gets saved **/
	private static NBTTagCompound masterSaveNbt = new NBTTagCompound();
	/** Used to check to see if file is in the process of being loaded **/
	public static boolean loading = false;
	/** Used to check to see if file was loaded at least once **/
	public static boolean hasLoaded = false;
	/** Used to check to see if file was changed and needs saved **/
	public static boolean needsSaving = false;

	/**
	 * Gets or creates a userAccess list to be used for any reason
	 * 
	 * @param name - name of the access list being created or loaded
	 * @param owner - the player's name to be used to create a new list
	 * @return - UserAccess list
	 */
	public List<UserAccess> getOrCreateList(String name, String owner)
	{
		if (name.toCharArray().length < 5 || owner.isEmpty() || name.startsWith("Default#"))
		{
			return null;
		}
		List<UserAccess> list = getList(name);
		if (list == null)
		{
			list = this.createList(name, owner);
		}
		return list;
	}

	/**
	 * creates a new user access list
	 * 
	 * @param name
	 * @param owner
	 * @return
	 */
	public List<UserAccess> createList(String name, String owner)
	{
		/*** Creates a new List if one doesn't exist ***/
		List<UserAccess> list = new ArrayList<UserAccess>();
		list.add(new UserAccess(owner, AccessLevel.OWNER, true));

		globalUserLists.put(name, list);
		saveList(name, list);
		this.needsSaving = true;
		return list;
	}

	/**
	 * Loads up a UserAccess List
	 * 
	 * @param name - name of the list
	 * @return - the list
	 */
	public List<UserAccess> getList(String name)
	{
		if (globalUserLists.containsKey(name))
		{
			/*** Get the list if its already loaded up ***/
			return globalUserLists.get(name);
		}
		else
		{
			/*** Loads the saved list if it exists ***/
			List<UserAccess> list = loadList(name);
			if (list != null)
			{
				globalUserLists.put(name, list);
			}
			return list;
		}
	}

	/**
	 * adds a user to the global list
	 * 
	 * @param listName - name of the list
	 * @param user - user being added as a UserAccess instance
	 * @return true if added
	 */
	public boolean addUser(String listName, UserAccess user)
	{
		if (user == null)
		{
			return false;
		}

		List<UserAccess> userList = this.getList(listName);

		if (userList != null)
		{
			if (userList.contains(user))
			{
				userList = UserAccess.removeUserAccess(user.username, userList);
			}
			if (userList.add(user))
			{
				globalUserLists.put(listName, userList);
				this.saveList(listName, userList);
				this.needsSaving = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a user from the global list
	 * 
	 * @param listName - name of the list
	 * @param user - user being removed
	 * @return true if removed
	 */
	public boolean removeUser(String listName, UserAccess user)
	{
		if (user == null)
		{
			return false;
		}

		List<UserAccess> userList = this.getList(listName);

		if (userList != null)
		{
			if (userList.contains(user))
			{
				userList = UserAccess.removeUserAccess(user.username, userList);
				globalUserLists.put(listName, userList);
				this.saveList(listName, userList);
				this.needsSaving = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Loads a given Global user list from the master save
	 * 
	 * @param name - name given to the list for reference
	 * @return - the list of user access levels to be used
	 */
	private List<UserAccess> loadList(String name)
	{
		NBTTagCompound masterSave = getMasterSaveFile();
		if (masterSave != null && masterSave.hasKey(name))
		{
			NBTTagCompound accessSave = masterSave.getCompoundTag(name);
			return UserAccess.readListFromNBT(accessSave, "Users");
		}
		return null;
	}

	/**
	 * Saves a given Global user list into the master save
	 * 
	 * @param name - name to save the list as
	 * @param list - list to be saved
	 */
	private void saveList(String name, List<UserAccess> list)
	{
		NBTTagCompound masterSave = getMasterSaveFile();
		if (masterSave != null)
		{
			NBTTagCompound accessSave = masterSave.getCompoundTag(name);
			UserAccess.writeListToNBT(accessSave, list);
			this.getMasterSaveFile().setCompoundTag(name, accessSave);
		}
	}

	/**
	 * Loads the master save from the world folder
	 */
	public static NBTTagCompound getMasterSaveFile()
	{
		if (masterSaveNbt.hasNoTags())
		{
			if (!loading)
			{
				hasLoaded = true;
				loading = true;
				NBTFileLoader.loadData(GlobalAccessLoader.SAVE_NAME);
				// TODO save the file
				loading = false;
			}
		}
		return masterSaveNbt;
	}
}
