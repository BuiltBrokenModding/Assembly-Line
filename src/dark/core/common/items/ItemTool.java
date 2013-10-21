package dark.core.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;

public class ItemTool extends Item
{
    protected List<Material> blocksEffectiveAgainstPick = new ArrayList<Material>();
    protected List<Material> blocksEffectiveAgainstAx = new ArrayList<Material>();
    protected List<Material> blocksEffectiveAgainstSpade = new ArrayList<Material>();

    public float efficiencyOnProperMaterial = 4.0F;
    protected int maxUses, enchant;

    /** Damage versus entities. */
    public float damageVsEntity;

    public ItemTool(String name, int maxUses, float effective, int enchant)
    {
        super(DarkMain.CONFIGURATION.getItem("Items", "Tool:" + name, DarkMain.getNextItemId()).getInt());
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTools);
        this.maxUses = maxUses;
        this.enchant = enchant;
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
        int meta = itemStack.getItemDamage();
        int maxDamage = 1000;
        switch (meta % 10)
        {
            case 0:
                maxDamage = 450;
                break;
            case 1:
                maxDamage = 900;
                break;
            case 2:
                maxDamage = 100;
                break;
        }

        damage = Math.max(Math.min(damage, maxDamage), 0);
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
        //We will have to check on how this is done to prevent issues
        return false;
    }

    @Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", (double) this.damageVsEntity, 0));
        return multimap;
    }

    @Override
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
        if (par1ItemStack.get)
            if (this.blocksEffectiveAgainst.contains(par2Block.blockMaterial))
            {
                return this.efficiencyOnProperMaterial;
            }

        return 1.0F;
    }

    /** FORGE: Overridden to allow custom tool effectiveness */
    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta)
    {
        if (ForgeHooks.isToolEffective(stack, block, meta))
        {
            return efficiencyOnProperMaterial;
        }
        return getStrVsBlock(stack, block);
    }

}
