package dark.core.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import dark.core.DarkMain;
import dark.core.items.EnumMeterials;

public class BlockOre extends Block
{
    Icon[] icons = new Icon[EnumMeterials.values().length];

    public BlockOre(int par1, Configuration config)
    {
        super(config.getBlock("Ore", par1).getInt(), Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + "Ore");
    }

    public static void regiserOreNames()
    {
        for (int i = 0; i < EnumMeterials.values().length; i++)
        {
            if (EnumMeterials.values()[i].doWorldGen)
            {
                OreDictionary.registerOre(EnumMeterials.values()[i].name + "Ore", new ItemStack(DarkMain.recipeLoader.blockOre.blockID, 1, i));
            }
        }
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < EnumMeterials.values().length; i++)
        {
            if (EnumMeterials.values()[i].doWorldGen)
            {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (int i = 0; i < EnumMeterials.values().length; i++)
        {
            if (EnumMeterials.values()[i].doWorldGen)
            {
                this.icons[i] = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + EnumMeterials.values()[i].name + "Ore");
            }
        }
    }

    @Override
    public Icon getIcon(int side, int metadata)
    {
        if (this.icons[metadata] != null)
        {
            return this.icons[metadata];
        }
        return Block.stone.getIcon(side, metadata);
    }
}
