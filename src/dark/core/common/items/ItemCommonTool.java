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
    public boolean isDamaged(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound().getInteger("toolDamage") > 0;
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
    public float getStrVsBlock(ItemStack itemStack, Block block)
    {
        if (itemStack != null && block != null)
        {
            EnumTool tool = EnumMaterial.getToolFromMeta(itemStack.getItemDamage());
            EnumMaterial mat = EnumMaterial.getToolMatFromMeta(itemStack.getItemDamage());
            if (tool.effecticVsMaterials.contains(block.blockMaterial))
            {
                return mat.materialEffectiveness;
            }
        }
        return 1.0F;
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

}
