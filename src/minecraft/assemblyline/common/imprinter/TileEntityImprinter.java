package assemblyline.common.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import assemblyline.api.IArmbot;
import assemblyline.api.IArmbotUseable;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import dark.library.helpers.Pair;
import dark.library.inv.ISlotPickResult;

public class TileEntityImprinter extends TileEntityAdvanced implements net.minecraftforge.common.ISidedInventory, ISidedInventory, IArmbotUseable, IPacketReceiver, ISlotPickResult
{
	public static final int IMPRINTER_MATRIX_START = 9;
	public static final int INVENTORY_START = IMPRINTER_MATRIX_START + 3;

	/**
	 * 9 slots for crafting, 1 slot for an imprint, 1 slot for an item
	 */
	public ItemStack[] craftingMatrix = new ItemStack[9];
	public static final int[] craftingSlots = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public ItemStack[] imprinterMatrix = new ItemStack[3];
	public static final int[] imprinterSlots = { IMPRINTER_MATRIX_START, IMPRINTER_MATRIX_START + 1, IMPRINTER_MATRIX_START + 2 };

	/**
	 * The Imprinter inventory containing slots.
	 */
	public ItemStack[] containingItems = new ItemStack[18];
	public static final int[] invSlots = new int[18];

	static
	{
		for (int i = 0; i < invSlots.length; i++)
		{
			invSlots[i] = TileEntityImprinter.INVENTORY_START + i;
		}
	}

	/**
	 * The containing currently used by the imprinter.
	 */
	public ContainerImprinter container;

	/**
	 * Is the current crafting result a result of an imprint?
	 */
	private boolean isImprinting = false;

	/**
	 * The ability for the imprinter to serach nearby inventories.
	 */
	public boolean searchInventories = true;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return this.craftingMatrix.length + this.imprinterMatrix.length + this.containingItems.length;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
	 * sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		if (slot < this.getSizeInventory())
		{
			if (slot < IMPRINTER_MATRIX_START)
			{
				this.craftingMatrix[slot] = itemStack;
			}
			else if (slot < INVENTORY_START)
			{
				this.imprinterMatrix[slot - IMPRINTER_MATRIX_START] = itemStack;
			}
			else
			{
				this.containingItems[slot - INVENTORY_START] = itemStack;
			}
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int amount)
	{
		if (this.getStackInSlot(i) != null)
		{
			ItemStack stack;

			if (this.getStackInSlot(i).stackSize <= amount)
			{
				stack = this.getStackInSlot(i);
				this.setInventorySlotContents(i, null);
				return stack;
			}
			else
			{
				stack = this.getStackInSlot(i).splitStack(amount);

				if (this.getStackInSlot(i).stackSize == 0)
				{
					this.setInventorySlotContents(i, null);
				}

				return stack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot < IMPRINTER_MATRIX_START)
		{
			return this.craftingMatrix[slot];
		}
		else if (slot < INVENTORY_START)
		{
			return this.imprinterMatrix[slot - IMPRINTER_MATRIX_START];
		}
		else
		{
			return this.containingItems[slot - INVENTORY_START];
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as
	 * an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.getStackInSlot(slot) != null)
		{
			ItemStack var2 = this.getStackInSlot(slot);
			this.setInventorySlotContents(slot, null);
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.imprinter.name");
	}

	@Override
	public void openChest()
	{
		this.onInventoryChanged();
	}

	@Override
	public void closeChest()
	{
		this.onInventoryChanged();
	}

	/**
	 * Construct an InventoryCrafting Matrix on the fly.
	 * 
	 * @return
	 */
	public InventoryCrafting getCraftingMatrix()
	{
		if (this.container != null)
		{
			InventoryCrafting inventoryCrafting = new InventoryCrafting(this.container, 3, 3);

			for (int i = 0; i < this.craftingMatrix.length; i++)
			{
				inventoryCrafting.setInventorySlotContents(i, this.craftingMatrix[i]);
			}

			return inventoryCrafting;
		}

		return null;
	}

	public void replaceCraftingMatrix(InventoryCrafting inventoryCrafting)
	{
		for (int i = 0; i < this.craftingMatrix.length; i++)
		{
			this.craftingMatrix[i] = inventoryCrafting.getStackInSlot(i);
		}
	}

	public boolean isMatrixEmpty()
	{
		for (int i = 0; i < 9; i++)
		{
			if (this.craftingMatrix[i] != null)
				return false;
		}

		return true;
	}

	/**
	 * Updates all the output slots. Call this to update the Imprinter.
	 */
	@Override
	public void onInventoryChanged()
	{
		if (!this.worldObj.isRemote)
		{
			/**
			 * Makes the stamping recipe for filters
			 */
			this.isImprinting = false;

			if (this.isMatrixEmpty() && this.imprinterMatrix[0] != null && this.imprinterMatrix[1] != null)
			{
				if (this.imprinterMatrix[0].getItem() instanceof ItemImprinter)
				{
					ItemStack outputStack = this.imprinterMatrix[0].copy();
					outputStack.stackSize = 1;
					ArrayList<ItemStack> filters = ItemImprinter.getFilters(outputStack);
					boolean filteringItemExists = false;

					for (ItemStack filteredStack : filters)
					{
						if (filteredStack.isItemEqual(this.imprinterMatrix[1]))
						{
							filters.remove(filteredStack);
							filteringItemExists = true;
							break;
						}
					}

					if (!filteringItemExists)
					{
						filters.add(this.imprinterMatrix[1]);
					}

					ItemImprinter.setFilters(outputStack, filters);
					this.imprinterMatrix[2] = outputStack;
					this.isImprinting = true;
				}
			}

			if (!this.isImprinting)
			{
				this.imprinterMatrix[2] = null;

				/**
				 * Try to craft from crafting grid. If not possible, then craft from imprint.
				 */
				boolean didCraft = false;

				/**
				 * Simulate an Inventory Crafting Instance
				 */
				InventoryCrafting inventoryCrafting = this.getCraftingMatrix();

				if (inventoryCrafting != null)
				{

					ItemStack matrixOutput = CraftingManager.getInstance().findMatchingRecipe(inventoryCrafting, this.worldObj);

					if (matrixOutput != null)
					{
						this.imprinterMatrix[2] = matrixOutput;
						didCraft = true;
					}
				}

				if (this.imprinterMatrix[0] != null && !didCraft)
				{
					// System.out.println("Using imprint as grid");
					if (this.imprinterMatrix[0].getItem() instanceof ItemImprinter)
					{

						ArrayList<ItemStack> filters = ItemImprinter.getFilters(this.imprinterMatrix[0]);

						for (ItemStack outputStack : filters)
						{
							if (outputStack != null)
							{
								// System.out.println("Imprint: Geting recipe for " +
								// outputStack.toString());
								Pair<ItemStack, ItemStack[]> idealRecipe = this.getIdealRecipe(outputStack);

								if (idealRecipe != null)
								{
									// System.out.println("Imprint: found ideal recipe for  " +
									// idealRecipe.getKey().toString());
									ItemStack recipeOutput = idealRecipe.getKey();
									if (recipeOutput != null & recipeOutput.stackSize > 0)
									{
										this.imprinterMatrix[2] = recipeOutput;
										didCraft = true;
										break;
									}
								}
							}
						}
					}
				}

				if (!didCraft)
				{
					this.imprinterMatrix[2] = null;
				}
			}
		}
	}

	public void onPickUpFromSlot(EntityPlayer entityPlayer, int s, ItemStack itemStack)
	{

		if (itemStack != null)
		{
			// System.out.println("PickResult:" + worldObj.isRemote + " managing item " +
			// itemStack.toString());
			if (this.isImprinting)
			{
				this.imprinterMatrix[0] = null;
			}
			else
			{
				/**
				 * Based on the ideal recipe, consume all the items required to make such recipe.
				 */
				if (this.getIdealRecipe(itemStack) != null)
				{
					// System.out.println("PickResult: ideal recipe  ");
					ItemStack[] requiredItems = this.getIdealRecipe(itemStack).getValue().clone();

					if (requiredItems != null)
					{
						for (int i = 0; i < requiredItems.length; i++)
						{
							ItemStack sta = requiredItems[i];
							if (sta != null && sta.itemID < Block.blocksList.length && sta.getItemDamage() == 32767)
							{
								requiredItems[i] = new ItemStack(sta.itemID, sta.stackSize, 0);
							}
						}
						// System.out.println("PickResult: valid resources  ");
						for (ItemStack searchStack : requiredItems)
						{
							if (searchStack != null)
							{
								/**
								 * inventories: for (IInventory inventory :
								 * getAvaliableInventories()) { for (int i = 0; i <
								 * inventory.getSizeInventory(); i++) { ItemStack checkStack =
								 * inventory.getStackInSlot(i);
								 * 
								 * if (checkStack != null) { if (areStacksEqual(searchStack,
								 * checkStack)) { inventory.decrStackSize(i, 1);
								 * //System.out.println("Consumed Item From Chest: " +
								 * checkStack.toString()); break inventories; } } } }
								 */

								for (int i = 0; i < this.containingItems.length; i++)
								{
									ItemStack checkStack = this.containingItems[i];

									if (checkStack != null)
									{
										if (areStacksEqual(searchStack, checkStack))
										{
											this.decrStackSize(i + INVENTORY_START, 1);
											// System.out.println("Consumed Item From Inv: " +
											// checkStack.toString());
											break;
										}
									}
								}
							}
						}
					}
				}
				else
				{
					int slot = 0;
					try
					{
						InventoryCrafting inventoryCrafting = this.getCraftingMatrix();
						GameRegistry.onItemCrafted(entityPlayer, itemStack, inventoryCrafting);

						for (slot = 0; slot < inventoryCrafting.getSizeInventory(); ++slot)
						{
							ItemStack slotStack = inventoryCrafting.getStackInSlot(slot);

							if (slotStack != null)
							{
								inventoryCrafting.decrStackSize(slot, 1);

								if (slotStack.getItem().hasContainerItem())
								{
									ItemStack containerStack = slotStack.getItem().getContainerItemStack(slotStack);

									if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage())
									{
										MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(entityPlayer, containerStack));
										containerStack = null;
									}

									if (containerStack != null && !slotStack.getItem().doesContainerItemLeaveCraftingGrid(slotStack))
									{
										if (inventoryCrafting.getStackInSlot(slot) == null)
										{
											inventoryCrafting.setInventorySlotContents(slot, containerStack);
										}
										else
										{
											entityPlayer.dropPlayerItem(containerStack);
										}
									}
								}
							}
						}

						this.replaceCraftingMatrix(inventoryCrafting);
					}
					catch (Exception e)
					{
						System.out.println("Imprinter: Failed to craft item: " + itemStack.getDisplayName());
						System.out.println("Vaporizing items to prevent inf crafting");
						for (slot = slot; slot < this.craftingMatrix.length; ++slot)
						{
							ItemStack slotStack = this.getStackInSlot(slot);

							if (slotStack != null)
							{
								if (slotStack.getItem().hasContainerItem())
								{
									this.setInventorySlotContents(slot, slotStack.getItem().getContainerItemStack(slotStack));
								}
								else
								{
									this.decrStackSize(slot, 1);
								}

							}
						}
						// this.craftingMatrix = new ItemStack[9];
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required items to make the desired item.
	 */
	public Pair<ItemStack, ItemStack[]> getIdealRecipe(ItemStack outputItem)
	{
		// System.out.println("IdealRecipe: Finding  " + outputItem.toString());
		for (Object object : CraftingManager.getInstance().getRecipeList())
		{
			if (object instanceof IRecipe)
			{
				if (((IRecipe) object).getRecipeOutput() != null)
				{
					if (this.areStacksEqual(outputItem, ((IRecipe) object).getRecipeOutput()))
					{
						// System.out.println("IdealRecipe: Output Match Found");
						if (object instanceof ShapedRecipes)
						{
							if (this.hasResource(((ShapedRecipes) object).recipeItems) != null)
							{
								// System.out.println("IdealRecipe: Shaped Recipe Found");
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), ((ShapedRecipes) object).recipeItems);
							}
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.hasResource(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])) != null)
							{
								// System.out.println("IdealRecipe: Shapeless Recipe Found");
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1]));
							}
						}
						else if (object instanceof ShapedOreRecipe)
						{
							ShapedOreRecipe oreRecipe = (ShapedOreRecipe) object;
							Object[] oreRecipeInput = (Object[]) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, oreRecipe, "input");

							ArrayList<ItemStack> hasResources = this.hasResource(oreRecipeInput);

							if (hasResources != null)
							{
								// System.out.println("IdealRecipe: ShapedOre Recipe Found");
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1]));
							}
						}
						else if (object instanceof ShapelessOreRecipe)
						{
							ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) object;
							ArrayList oreRecipeInput = (ArrayList) ReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, oreRecipe, "input");

							List<ItemStack> hasResources = this.hasResource(oreRecipeInput.toArray());

							if (hasResources != null)
							{
								// System.out.println("IdealRecipe: ShapelessOre Recipe Found");
								return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1]));
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns if the following inventory has the following resource required.
	 * 
	 * @param recipeItems - The items to be checked for the recipes.
	 */
	public ArrayList<ItemStack> hasResource(Object[] recipeItems)
	{
		try
		{
			/**
			 * Simulate an imprinter.
			 */
			TileEntityImprinter dummyImprinter = new TileEntityImprinter();
			NBTTagCompound cloneData = new NBTTagCompound();
			this.writeToNBT(cloneData);
			dummyImprinter.readFromNBT(cloneData);

			// System.out.println("ResourceChecker: Looking for items");
			for (int i = 0; i < recipeItems.length; i++)
			{
				// System.out.println("ResourceChecker: Looking for " + recipeItems.toString());
			}
			/**
			 * The actual amount of resource required. Each ItemStack will only have stacksize of 1.
			 */
			ArrayList<ItemStack> actualResources = new ArrayList<ItemStack>();
			int itemMatch = 0;
			int w = 0;
			for (Object obj : recipeItems)
			{
				w++;
				if (obj instanceof ItemStack)
				{
					ItemStack recipeItem = (ItemStack) obj;
					if (recipeItem.itemID < Block.blocksList.length && recipeItem.getItemDamage() == 32767)
					{
						recipeItem = new ItemStack(recipeItem.itemID, recipeItem.stackSize, 0);
					}
					actualResources.add(recipeItem.copy());
					if (recipeItem != null)
					{
						// System.out.println("ResourceChecker: Item0" + w + " = " +
						// recipeItem.toString());
						if (this.doesItemExist(recipeItem, dummyImprinter))
						{
							// System.out.println("ResourceChecker: Match found");
							itemMatch++;
						}
					}
				}
				else if (obj instanceof ArrayList)
				{
					/**
					 * Look for various possible ingredients of the same item and try to match it.
					 */
					ArrayList ingredientsList = (ArrayList) obj;
					Object[] ingredientsArray = ingredientsList.toArray();
					// System.out.println("ResourceChecker: Obj0" + w + " = " + obj.toString());
					for (int x = 0; x < ingredientsArray.length; x++)
					{
						if (ingredientsArray[x] != null && ingredientsArray[x] instanceof ItemStack)
						{
							ItemStack recipeItem = (ItemStack) ingredientsArray[x];
							if (recipeItem.itemID < Block.blocksList.length && recipeItem.getItemDamage() == 32767)
							{
								recipeItem = new ItemStack(recipeItem.itemID, recipeItem.stackSize, 0);
							}
							actualResources.add(recipeItem.copy());

							if (recipeItem != null)
							{
								if (this.doesItemExist(recipeItem, dummyImprinter))
								{
									itemMatch++;
									break;
								}
							}
						}
					}
				}
				else
				{
					// System.out.println("ResourceChecker: Item0" + w + " = null");
					itemMatch++;
				}
			}
			boolean resourcesFound = itemMatch >= actualResources.size();
			// System.out.println("ResourceChecker: Found " + actualResources.size() + " Items and "
			// + itemMatch + " slot matches");
			// System.out.println("ResourceChecker: has all resources been found? /n A: " +
			// resourcesFound);
			return resourcesFound ? actualResources : null;
		}
		catch (Exception e)
		{
			System.out.println("Failed to find recipes in the imprinter.");
			e.printStackTrace();
		}

		return null;
	}

	private boolean doesItemExist(ItemStack recipeItem, TileEntityImprinter dummyImprinter)
	{

		if (recipeItem == null || recipeItem.itemID == 0 || recipeItem.stackSize <= 0)
		{
			return true;
		}
		// System.out.println("ResourceChecker: Checking inv for item " + recipeItem.toString());
		for (int i = 0; i < dummyImprinter.containingItems.length; i++)
		{
			ItemStack checkStack = dummyImprinter.containingItems[i];

			if (checkStack != null)
			{
				// System.out.println("ResourceChecker: -----Item in slot0" + i + " = " +
				// checkStack.toString());
				if (areStacksEqual(recipeItem, checkStack))
				{
					// TODO Do NBT Checking
					dummyImprinter.decrStackSize(i + INVENTORY_START, 1);
					// System.out.println("ResourceChecker: Found matching item " +
					// checkStack.toString());
					return true;
				}
			}
		}

		return false;
	}

	public boolean areStacksEqual(ItemStack recipeItem, ItemStack checkStack)
	{
		return recipeItem.isItemEqual(checkStack) || (recipeItem.itemID == checkStack.itemID && recipeItem.isItemStackDamageable() && !recipeItem.isItemDamaged());
	}

	/**
	 * Gets all valid inventories that imprinter can use for resources
	 */
	private List<IInventory> getAvaliableInventories()
	{
		List<IInventory> inventories = new ArrayList<IInventory>();

		if (this.searchInventories)
		{
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
			{
				TileEntity tileEntity = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), direction);

				if (tileEntity != null)
				{
					/**
					 * Try to put items into a chest.
					 */
					if (tileEntity instanceof TileEntityMulti)
					{
						Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

						if (mainBlockPosition != null)
						{
							if (mainBlockPosition.getTileEntity(this.worldObj) instanceof IInventory)
							{
								inventories.add((IInventory) mainBlockPosition.getTileEntity(this.worldObj));
							}
						}
					}
					else if (tileEntity instanceof TileEntityChest)
					{
						inventories.add((TileEntityChest) tileEntity);

						/**
						 * Try to find a double chest.
						 */
						for (int i = 2; i < 6; i++)
						{
							TileEntity chest = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(tileEntity), ForgeDirection.getOrientation(2));

							if (chest != null && chest.getClass() == tileEntity.getClass())
							{
								inventories.add((TileEntityChest) chest);
								break;
							}
						}

					}
					else if (tileEntity instanceof IInventory && !(tileEntity instanceof TileEntityImprinter))
					{
						inventories.add((IInventory) tileEntity);
					}
				}
			}
		}

		return inventories;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.searchInventories);
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			this.searchInventories = dataStream.readBoolean();
		}
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList var2 = nbt.getTagList("Items");
		this.craftingMatrix = new ItemStack[9];
		this.imprinterMatrix = new ItemStack[3];
		this.containingItems = new ItemStack[18];

		for (int i = 0; i < var2.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(i);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.getSizeInventory())
			{
				this.setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4));
			}
		}

		this.searchInventories = nbt.getBoolean("searchInventories");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagList var2 = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			if (this.getStackInSlot(i) != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) i);
				this.getStackInSlot(i).writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		nbt.setTag("Items", var2);

		nbt.setBoolean("searchInventories", this.searchInventories);
	}

	/**
	 * Tries to let the Armbot craft an item.
	 */
	@Override
	public boolean onUse(IArmbot armbot, String[] args)
	{
		this.onInventoryChanged();

		if (this.imprinterMatrix[2] != null)
		{
			armbot.grabItem(this.imprinterMatrix[2].copy());
			this.onPickUpFromSlot(null, 2, this.imprinterMatrix[2]);
			this.imprinterMatrix[2] = null;
		}

		return false;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityplayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (dir != ForgeDirection.DOWN)
		{
			return this.invSlots;
		}
		return null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return this.isStackValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return this.isStackValidForSlot(slot, itemstack);
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return this.craftingMatrix.length + this.imprinterMatrix.length;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return this.containingItems.length - 1;
	}
}
