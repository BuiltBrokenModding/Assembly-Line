package dark.core.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;
import dark.core.prefab.items.ItemBasic;

/** A metadata item containing parts of various machines in Liquid Mechanics Mod.
 * 
 * @author Rs */
public class ItemParts extends ItemBasic
{

    public ItemParts(int par1, Configuration config)
    {
        super(par1, "DMParts", config);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItemDamage() < Parts.values().length)
        {
            return "item." + Parts.values()[itemStack.getItemDamage()].name;
        }
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int meta)
    {
        if (meta < Parts.values().length)
        {
            return Parts.values()[meta].icon;
        }
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        for (Parts part : Parts.values())
        {
            part.icon = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + part.name);
        }
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public void getSubItems(int blockID, CreativeTabs tab, List itemStackList)
    {
        for (int meta = 0; meta < Parts.values().length; meta++)
        {
            itemStackList.add(new ItemStack(this, 1, meta));
        }
    }

    public static enum Parts
    {
        Bronze("BronzeTube"),
        Iron("IronTube"),
        Obby("ObbyTube"),
        Nether("NetherTube"),
        Seal("LeatherSeal"),
        SlimeSeal("SlimeSeal"),
        Tank("UnfinishedTank"),
        Valve("ValvePart");

        public String name;
        public Icon icon;

        private Parts(String name)
        {
            this.name = name;
        }
    }
}
