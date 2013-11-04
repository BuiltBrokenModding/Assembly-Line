package dark.farmtech.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.IExtraInfo.IExtraItemInfo;
import dark.farmtech.FarmTech;

/** Item for storing all kinds of food based items including meats, fruits, pies, cakes, breads, etc
 * we have 1000s of meta to work with :)
 *
 * @author DarkGuardsman */
public class ItemFood extends Item implements IExtraItemInfo
{
    public ItemFood(int par1)
    {
        super(FarmTech.CONFIGURATION.getItem("Food", FarmTech.getNextID()).getInt());
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabFood);
        this.setUnlocalizedName("FarmFood");
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
    {
        FarmFood food = FarmFood.get(itemStack);
        if (player != null && food != null)
        {
            --itemStack.stackSize;
            //TODO add stats and achievements for some of the foods
            world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

            if (!world.isRemote && food.hasPotion && food.potionID > 0 && world.rand.nextFloat() < food.potionChance)
            {
                player.addPotionEffect(new PotionEffect(food.potionID, food.potionDuration * 20, food.potionAmp));
            }
        }
        return itemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.eat;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (player.canEat(true))
        {
            player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        }
        return itemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (FarmFood food : FarmFood.values())
        {
            food.icon = par1IconRegister.registerIcon(FarmTech.instance.PREFIX + "food_" + food.name);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int par1)
    {
        FarmFood food = FarmFood.get(par1);
        if (food != null)
        {
            return food.icon;
        }
        return this.itemIcon;
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(this.itemID, 1, 0));
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return true;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        for (FarmFood food : FarmFood.values())
        {
            food.foodValue = (float) config.get("FoodValue", food.name, food.foodValue).getDouble(food.foodValue);
            food.saturation = (float) config.get("FoodSaturation", food.name, food.saturation).getDouble(food.saturation);
        }
    }

    @Override
    public void loadOreNames()
    {
        for (FarmFood food : FarmFood.values())
        {
            OreDictionary.registerOre(food.name, new ItemStack(this, 1, food.ordinal()));
        }
    }

    /** enum that stores data for each meta value that represents a food object for the item
     *
     * @author DarkGuardsman */
    public static enum FarmFood
    {
        TurkeyRaw("TurkeyRaw", 1, 2, 0.3F),
        TurkeyCooked("TurkeyCooked", 6, 0.6F);
        /** Metadata id that this item converts to for each recipe creation */
        final int cookedID;
        /** Name used for translation and icons */
        final String name;
        float foodValue;
        float saturation;
        boolean hasPotion = false;
        int potionID, potionDuration, potionAmp;
        float potionChance;
        Icon icon;

        private FarmFood(String name, float foodValue, float saturation)
        {
            this(name, -1, foodValue, saturation);
        }

        private FarmFood(String name, int cookedID, float foodValue, float saturation)
        {
            this.name = name;
            this.cookedID = cookedID;
            //Means the food is raw and can harm the player if not cooked
            if (cookedID != -1)
            {
                this.hasPotion = true;
                this.potionID = Potion.hunger.id;
                this.potionDuration = 30;
                this.potionAmp = 0;
                this.potionChance = 0.3F;
            }
        }

        private FarmFood(String name, int cookedID, float foodValue, float saturation, int pID, int pD, int pA, float pC)
        {
            this.name = name;
            this.cookedID = cookedID;
            this.hasPotion = true;
            this.potionID = pID;
            this.potionDuration = pD;
            this.potionAmp = pA;
            this.potionChance = pC;
        }

        public static FarmFood get(ItemStack stack)
        {
            return get(stack != null ? stack.getItemDamage() : -1);
        }

        public static FarmFood get(int meta)
        {
            if (meta >= 0 && meta < FarmFood.values().length)
            {
                return FarmFood.values()[meta];
            }
            return null;
        }
    }

}
