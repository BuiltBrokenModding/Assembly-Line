package assemblyline.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;
import assemblyline.common.AssemblyLine;
import assemblyline.common.TabAssemblyLine;
import assemblyline.common.machine.TileEntityAssembly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.api.INetworkPart;

public class BlockALMachine extends BlockAdvanced
{
	public Icon machine_icon;

	public BlockALMachine(int id, Material material, String name)
	{
		super(AssemblyLine.CONFIGURATION.getBlock(name, id).getInt(), material);
		this.setUnlocalizedName(name);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = entityPlayer.getHeldItem();
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (!world.isRemote && stack != null && stack.itemID == Item.stick.itemID && ent instanceof TileEntityAssembly && entityPlayer != null)
		{
			TileEntityAssembly asm = (TileEntityAssembly) ent;
			String output = "Debug>>>";
			output += "Channel:" + (asm.getTileNetwork() != null ? asm.getTileNetwork().toString() : "Error") + "|";
			entityPlayer.sendChatToPlayer(output);
		}
		return super.onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg)
	{
		this.machine_icon = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "machine");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
	{
		return this.machine_icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2)
	{
		return this.machine_icon;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).updateNetworkConnections();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).updateNetworkConnections();
		}
	}

}