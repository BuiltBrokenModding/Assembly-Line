package dark.core.items.parts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import dark.core.DarkMain;
import dark.core.recipes.RecipeManager;

/** Class for storing materials, there icon names, sub items to be made from them or there sub ores
 *
 *
 * @author DarkGuardsman */
public enum EnumMeterials
{
	WOOD("Wood", EnumOreParts.INGOTS, EnumOreParts.PLATES, EnumOreParts.RUBBLE, EnumOreParts.ROD),
	STONE("Stone", EnumOreParts.INGOTS),
	COPPER("Copper", true, true, 70, 22, 10),
	TIN("Tin", true, true, 70, 22, 10, EnumOreParts.GEARS, EnumOreParts.TUBE),
	IRON("Iron", EnumOreParts.INGOTS),
	OBBY("Obby", EnumOreParts.INGOTS, EnumOreParts.RUBBLE),
	LEAD("Lead", true, true, 50, 8, 3, EnumOreParts.GEARS, EnumOreParts.TUBE),
	ALUMINIUM("Aluminum", true, true, 50, 8, 3, EnumOreParts.GEARS, EnumOreParts.TUBE),
	SILVER("Silver", true, true, 40, 15, 6, EnumOreParts.GEARS),
	GOLD("Gold", EnumOreParts.GEARS, EnumOreParts.INGOTS),
	COAL("Coal", EnumOreParts.GEARS, EnumOreParts.TUBE, EnumOreParts.PLATES, EnumOreParts.RUBBLE),
	STEEL("Steel", true, false, 0, 0, 0, EnumOreParts.RUBBLE),
	BRONZE("Bronze", true, false, 0, 0, 0, EnumOreParts.RUBBLE);

	/* UNLOCALIZED NAME AND ICON PREFIX */
	public String name;
	/* CREATE AN ITEM FOR THIS ORE */
	public List<EnumOreParts> unneedItems;
	public boolean block;
	/* ORE GENERATOR OPTIONS */
	public boolean doWorldGen;
	public int ammount, branch, maxY;

	private EnumMeterials(String name, boolean block, boolean canWorldGen, int ammount, int branch, int maxY, EnumOreParts... enumOreParts)
	{
		this.name = name;

		unneedItems = new ArrayList<EnumOreParts>();
		for (int i = 0; enumOreParts != null && i < enumOreParts.length; i++)
		{
			unneedItems.add(enumOreParts[i]);
		}

		this.block = block;

		this.doWorldGen = canWorldGen;
		this.maxY = maxY;
		this.ammount = ammount;
		this.branch = branch;
	}

	private EnumMeterials(String name, EnumOreParts... enumOreParts)
	{
		this.name = name;
		this.block = false;

		unneedItems = new ArrayList<EnumOreParts>();
		for (int i = 0; enumOreParts != null && i < enumOreParts.length; i++)
		{
			unneedItems.add(enumOreParts[i]);
		}
		this.doWorldGen = false;
		this.maxY = 0;
		this.ammount = 0;
		this.branch = 0;
	}

	public OreGenReplaceStone getGeneratorSettings()
	{
		if (this.doWorldGen)
		{
			ItemStack stack = new ItemStack(RecipeManager.blockOre, 1, this.ordinal());
			return (OreGenReplaceStone) new OreGenReplaceStone(this.name, this.name + "Ore", stack, this.maxY, this.ammount, this.branch).enable(DarkMain.instance.CONFIGURATION);
		}
		return null;
	}

	public boolean shouldCreateItem(EnumOreParts part)
	{
		if (part == EnumOreParts.ROD || part == EnumOreParts.TUBE || part == EnumOreParts.RUBBLE)
		{
			return false;
		}
		return this.unneedItems == null || !this.unneedItems.contains(part);
	}
}
