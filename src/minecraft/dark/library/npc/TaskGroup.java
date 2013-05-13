package dark.library.npc;

import universalelectricity.prefab.TranslationHelper;

public enum TaskGroup
{
	/* Collection of items for ground or inventories */
	COLLECTION("collection"),
	/* Defence of an area from NPCs or Players */
	DEFENCE("Guardsman"),
	/* Attack of an area against NPCs or Players */
	OFFENCE("Soldier"),
	/* Creation or repair of items/blocks */
	CRAFTING("Crafter");

	public String unlocalizedName;

	private TaskGroup(String name)
	{
		this.unlocalizedName = name;
	}

	/**
	 * Returns the localized name of a task for GUIs/etc
	 */
	public String getLocalizedName()
	{
		return TranslationHelper.getLocal("npc.task." + this.unlocalizedName);
	}
}
