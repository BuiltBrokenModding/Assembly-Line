package dark.core.common.blocks;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ProcessorRecipes;
import dark.api.ProcessorRecipes.ProcessorType;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DarkMain;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.EnumOrePart;
import dark.core.common.items.ItemOreDirv;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.Pair;

public class BlockOre extends Block implements IExtraObjectInfo
{
    Icon[] icons = new Icon[EnumMaterial.values().length];

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
        TIN(EnumMaterial.TIN, "tin", "oreTin", 70, 22, 10),
        COPPER(EnumMaterial.COPPER, "copper", "copperOre", 70, 22, 10),
        SILVER(EnumMaterial.SILVER, "silver", "silverOre", 40, 15, 6),
        LEAD(EnumMaterial.LEAD, "lead", "leadOre", 50, 8, 3),
        Bauxite(EnumMaterial.ALUMINIUM, "bauxite", "bauxiteOre", 50, 8, 3);

        public String name, oreName;
        public ItemStack stack;
        public EnumMaterial mat;

        @SideOnly(Side.CLIENT)
        public Icon oreIcon;

        /* ORE GENERATOR OPTIONS */
        public boolean doWorldGen = true;
        public int ammount, branch, maxY;

        private OreData(EnumMaterial mat, String name, String oreName, int ammount, int branch, int maxY)
        {
            this.name = name;
            this.oreName = oreName;
            this.mat = mat;

            this.maxY = maxY;
            this.ammount = ammount;
            this.branch = branch;
        }

        public OreGenReplaceStone getGeneratorSettings()
        {
            if (this.doWorldGen)
            {
                ItemStack stack = new ItemStack(CoreRecipeLoader.blockOre, 1, this.ordinal());
                return (OreGenReplaceStone) new OreGenReplaceStone(this.name, this.name + "Ore", stack, this.maxY, this.ammount, this.branch).enable(DarkMain.getInstance().CONFIGURATION);
            }
            return null;
        }
    }
}
