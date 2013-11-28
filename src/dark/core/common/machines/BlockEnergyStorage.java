package dark.core.common.machines;

import java.util.List;
import java.util.Set;

import com.builtbroken.common.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.vector.Vector3;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.helpers.MathHelper;
import dark.core.prefab.machine.BlockMachine;

/** Block for energy storage devices
 * 
 * @author Rseifert */
public class BlockEnergyStorage extends BlockMachine
{
    public BlockEnergyStorage()
    {
        super(DarkMain.CONFIGURATION, "DMEnergyStorage", UniversalElectricity.machine);
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }

    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
    {
        Vector3 vec = new Vector3(x, y, z);
        int meta = vec.getBlockMetadata(world);
        if (side == (meta))
        {
            return this.iconOutput;
        }
        return vec.clone().modifyPositionFromSide(ForgeDirection.getOrientation(side)).getTileEntity(world) instanceof IConductor ? this.iconInput : this.blockIcon;

    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        world.setBlockMetadataWithNotify(x, y, z, side, 3);
        return true;
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("DCTileBatBox", TileEntityBatteryBox.class));

    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        switch (metadata / 4)
        {
            case 0:
                return new TileEntityBatteryBox();

        }
        return super.createTileEntity(world, metadata);

    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    @Override
    public int damageDropped(int metadata)
    {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(this, 1, 0);
    }

}
