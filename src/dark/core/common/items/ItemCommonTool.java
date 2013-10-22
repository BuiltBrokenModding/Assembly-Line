package dark.core.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;
import dark.core.prefab.IExtraInfo.IExtraItemInfo;
import dark.core.prefab.ModPrefab;

/** Flexible tool class that uses NBT to store damage and effect rather than metadata. Metadata
 * instead is used to store sub items allowing several different tools to exist within the same item
 * 
 * @author DarkGuardsman */
public class ItemCommonTool extends Item implements IExtraItemInfo
{
    protected int enchant = 5;
    public static final String BROKEN_NBT = "broken";
    public static final String REINFORCED_NBT = "reinforced";
    public static final String HEATTREATED_NBT = "heattreated";
    public static final String HANDLE_NBT = "handle";

    public ItemCommonTool()
    {
        super(DarkMain.CONFIGURATION.getItem("Items", "CommonTools", ModPrefab.getNextItemId()).getInt());
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (itemStack.getTagCompound().getBoolean("broken"))
        {
            return false;
        }
        if (EnumMaterial.getToolFromMeta(itemStack.getItemDamage()) == EnumTool.HOE)
        {
            if (!player.canPlayerEdit(x, y, z, par7, itemStack))
            {
                return false;
            }
            else
            {
                UseHoeEvent event = new UseHoeEvent(player, itemStack, world, x, y, z);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    return false;
                }

                if (event.getResult() == Result.ALLOW)
                {
                    this.damage(itemStack, 1, player);
                    return true;
                }

                int blockID = world.getBlockId(x, y, z);
                boolean air = world.isAirBlock(x, y + 1, z);

                if (par7 != 0 && air && (blockID == Block.grass.blockID || blockID == Block.dirt.blockID))
                {
                    Block block = Block.tilledField;
                    world.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

                    if (world.isRemote)
                    {
                        return true;
                    }
                    else
                    {
                        world.setBlock(x, y, z, block.blockID);
                        this.damage(itemStack, 1, player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Applies damage to the item using NBT as well uses the normal ItemStack damage checks */
    public void damage(ItemStack itemStack, int damage, EntityLivingBase entity)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());
        if (!itemStack.isItemStackDamageable() || (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode))
        {
            return;
        }
        else
        {
            if (damage > 0)
            {
                int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack);
                int k = 0;

                for (int l = 0; j > 0 && l < damage; ++l)
                {
                    if (EnchantmentDurability.negateDamage(itemStack, j, entity.worldObj.rand))
                    {
                        ++k;
                    }
                }

                damage -= k;

                if (damage <= 0)
                {
                    return;
                }
            }
            int currentDamage = itemStack.getTagCompound().getInteger("toolDamage") + damage;
            damage = Math.max(Math.min(damage, mat.maxUses), 0);
            itemStack.getTagCompound().setInteger("toolDamage", damage);
            if (currentDamage > mat.maxUses)
            {
                entity.renderBrokenItemStack(itemStack);
                itemStack.getTagCompound().setBoolean("broken", true);
                if (entity instanceof EntityPlayer)
                {
                    EntityPlayer entityplayer = (EntityPlayer) entity;
                    entityplayer.addStat(StatList.objectBreakStats[this.itemID], 1);
                }
            }
        }

    }

    @Override
    public boolean isDamaged(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (itemStack.getTagCompound().getBoolean("broken"))
        {
            return true;
        }
        return itemStack.getTagCompound().getInteger("toolDamage") > 0;
    }

    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (itemStack.getTagCompound().getBoolean("broken"))
        {
            return true;
        }
        this.damage(itemStack, 2, par2EntityLivingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World par2World, int par3, int par4, int par5, int par6, EntityLivingBase par7EntityLivingBase)
    {
        if (Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
        {
            this.damage(itemStack, 1, par7EntityLivingBase);
        }

        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return this.enchant;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        //TODO,, We will have to check on how this is done to prevent issues with the way damage is actually saved
        return false;
    }

    @Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", 3.0, 0));
        return multimap;
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block)
    {
        if (itemStack != null && block != null)
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if (itemStack.getTagCompound().getBoolean("broken"))
            {
                return 0;
            }
            EnumTool tool = EnumMaterial.getToolFromMeta(itemStack.getItemDamage());
            EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());
            if (tool.effecticVsMaterials.contains(block.blockMaterial))
            {
                return mat.materialEffectiveness;
            }
            return 1.0F;
        }
        return 0;

    }

    @Override
    public boolean canHarvestBlock(Block par1Block)
    {
        return true;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack itemStack)
    {
        if (itemStack != null && block != null)
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if (itemStack.getTagCompound().getBoolean("broken"))
            {
                return false;
            }
            EnumTool tool = EnumMaterial.getToolFromMeta(itemStack.getItemDamage());
            EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());
            if (tool.effecticVsMaterials.contains(block.blockMaterial))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            return "item." + DarkMain.getInstance().PREFIX + EnumTool.getFullName(itemStack.getItemDamage());
        }
        else
        {
            return this.getUnlocalizedName();
        }
    }

    @Override
    public int getDisplayDamage(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        int damage = itemStack.getTagCompound().getInteger("toolDamage");
        EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());
        return (damage / mat.maxUses) * 100;
    }

    @Override
    public Icon getIconFromDamage(int i)
    {
        return EnumMaterial.getToolIcon(i);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        for (EnumMaterial mat : EnumMaterial.values())
        {
            if (mat.hasTools)
            {
                mat.toolIcons = new Icon[EnumOrePart.values().length];
                for (EnumTool tool : EnumTool.values())
                {
                    if (tool.enabled)
                    {
                        mat.toolIcons[tool.ordinal()] = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + mat.simpleName + tool.name);
                    }
                }
            }
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (EnumMaterial mat : EnumMaterial.values())
        {
            if (mat.hasTools)
            {
                for (EnumTool tool : EnumTool.values())
                {
                    ItemStack stack = EnumMaterial.getTool(tool, mat);
                    if (tool.enabled && stack != null)
                    {
                        par3List.add(stack);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasExtraConfigs()
    {
        // TODO Add configs such as enable broken tools, and allow tools to be salvaged
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadOreNames()
    {
        for (EnumMaterial mat : EnumMaterial.values())
        {
            if (mat.hasTools)
            {
                for (EnumTool tool : EnumTool.values())
                {
                    ItemStack stack = EnumMaterial.getTool(tool, mat);
                    if (tool.enabled && stack != null)
                    {
                        OreDictionary.registerOre(EnumTool.getFullName(stack.getItemDamage()), stack);
                    }
                }
            }
        }

    }
}
