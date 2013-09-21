package dark.core.common.blocks;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;
import dark.core.common.items.EnumMeterials;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.Pair;

public class BlockOre extends Block implements IExtraObjectInfo
{
    Icon[] icons = new Icon[EnumMeterials.values().length];

    public BlockOre()
    {
        super(DarkMain.CONFIGURATION.getBlock("Ore", ModPrefab.getNextID()).getInt(), Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName(DarkMain.getInstance().PREFIX + "Ore");

        for (OreData data : OreData.values())
        {
            data.stack = new ItemStack(this.blockID, 1, data.ordinal());
        }
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (OreData data : OreData.values())
        {
            par3List.add(data.stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (OreData data : OreData.values())
        {
            data.oreIcon = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + data.name + "Ore");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata)
    {
        if (metadata < OreData.values().length)
        {
            return OreData.values()[metadata].oreIcon;
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
        for (OreData data : OreData.values())
        {
            OreDictionary.registerOre(data.oreName, data.stack);
        }
    }

    public static enum OreData
    {
        TIN("tin", "oreTin"),
        COPPER("copper", "copperOre"),
        SILVER("silver", "silverOre"),
        LEAD("lead","leadOre"),
        Bauxite("bauxite","bauxiteOre");

        public String name, oreName;
        public ItemStack stack;
        @SideOnly(Side.CLIENT)
        public Icon oreIcon;

        private OreData(String name, String oreName)
        {
            this.name = name;
            this.oreName = oreName;
        }
    }
}
