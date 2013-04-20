package dark.library.locking;

import java.util.List;

public interface ISpecialAccess
{
	/**
	 * Gets the player's access level on the machine he is using
	 * 
	 * @return access level of the player, make sure to return no access if the player doesn't have
	 * any
	 */
	public AccessLevel getUserAccess(String username);

	/**
	 * gets the access list for the machine
	 */
	public List<UserAccess> getUsers();

	/**
	 * Set the user's access in the list
	 * 
	 * @param user - userAccess instance
	 * @param isServer - true if added server side
	 * @return true if added to the list
	 */
	public boolean addUserAccess(UserAccess user, boolean isServer);

	/**
	 * Removes the user from the access list
	 */
	public boolean removeUserAccess(String username, boolean isServer);

	/**
	 * Gets a list of users with the specified access level.
	 */
	public List<UserAccess> getUsersWithAcess(AccessLevel level);

}
