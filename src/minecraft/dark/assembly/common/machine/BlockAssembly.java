package dark.assembly.common.machine;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.INetworkPart;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.TabAssemblyLine;
import dark.core.blocks.BlockMachine;

public abstract class BlockAssembly extends BlockMachine
{
	public Icon machine_icon;

	public BlockAssembly(int id, Material material, String name)
	{
		super(name, AssemblyLine.CONFIGURATION, id, material);
		this.setUnlocalizedName(name);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

}