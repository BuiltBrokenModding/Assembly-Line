package dark.core.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;

public class ItemCommonTool extends Item
{

    public float efficiencyOnProperMaterial = 4.0F;
    protected int enchant = 5;

    public ItemCommonTool()
    {
        super(DarkMain.CONFIGURATION.getItem("Items", "CommonTools", DarkMain.getNextItemId()).getInt());
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public void damage(ItemStack itemStack, int damage, EntityLivingBase entity)
    {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        this.setDamage(itemStack, itemStack.getTagCompound().getInteger("toolDamage") + damage);
    }

    public void setDamage(ItemStack itemStack, int damage)
    {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());

        damage = Math.max(Math.min(damage, mat.maxUses), 0);
        itemStack.getTagCompound().setInteger("toolDamage", damage);
    }

    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
        this.damage(itemStack, 2, par2EntityLivingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World par2World, int par3, int par4, int par5, int par6, EntityLivingBase par7EntityLivingBase)
    {
        if ((double) Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
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
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
        if (par1ItemStack != null)
        {
            EnumTool tool = EnumMaterial.getToolFromMeta(par1ItemStack.getItemDamage());
            EnumMaterial mat = EnumMaterial.getToolMatFromMeta(par1ItemStack.getItemDamage());
            if (tool.effecticVsMaterials.contains(par2Block.blockMaterial) || par2Block.blockMaterial.isToolNotRequired())
            {
                return mat.materialEffectiveness;
            }
        }
        return 1.0F;
    }

    /** FORGE: Overridden to allow custom tool effectiveness */
    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta)
    {
        return getStrVsBlock(stack, block);
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
                mat.itemIcons = new Icon[EnumOrePart.values().length];
                for (EnumTool tool : EnumTool.values())
                {
                    if (tool.enabled)
                    {
                        mat.itemIcons[tool.ordinal()] = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + mat.simpleName + tool.name);
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
                    if (tool.enabled && stack != null && mat.toolIcons[tool.ordinal()] != null)
                    {
                        par3List.add(stack);
                    }
                }
            }
        }
    }

}
