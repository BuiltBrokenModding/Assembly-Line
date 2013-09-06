package dark.core.common.blocks;

import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import dark.core.common.DarkMain;
import dark.core.common.items.EnumMeterials;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;

public class BlockOre extends Block implements IExtraObjectInfo
{
    Icon[] icons = new Icon[EnumMeterials.values().length];

    public BlockOre(int par1, Configuration config)
    {
        super(config.getBlock("Ore", par1).getInt(), Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + "Ore");
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

    @Override @SideOnly(Side.CLIENT)
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

    @Override @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata)
    {
        if (this.icons[metadata] != null)
        {
            return this.icons[metadata];
        }
        return Block.stone.getIcon(side, metadata);
    }

    @Override
    public void loadRecipes()
    {
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
    }

    @Override
    public boolean hasExtraConfigs()
    {
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
        for (int i = 0; i < EnumMeterials.values().length; i++)
        {
            if (EnumMeterials.values()[i].doWorldGen)
            {
                OreDictionary.registerOre(EnumMeterials.values()[i].name + "Ore", new ItemStack(DarkMain.recipeLoader.blockOre.blockID, 1, i));
            }
        }

    }
}
