package dark.fluid.common.item;

import hydraulic.api.IReadOut;

import java.util.List;

import dark.fluid.common.TabFluidMech;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemTools extends ItemBasic
{
	private int spawnID;

	public ItemTools(int id)
	{
		super("lmTool", id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setMaxStackSize(1);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		int meta = itemStack.getItemDamage();
		if (meta == 0)
		{
			return "item."+"PipeGauge";
		}
		return "item."+this.getUnlocalizedName() + "." + meta;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int x, int y, int z, int side, float par8, float par9, float par10)
	{
		if (!par3World.isRemote)
		{
			int meta = itemStack.getItemDamage();
			TileEntity blockEntity = par3World.getBlockTileEntity(x, y, z);

			// pipe Guage
			if (meta == 0)
			{

				if (blockEntity instanceof IReadOut)
				{
					String output = ((IReadOut) blockEntity).getMeterReading(player, ForgeDirection.getOrientation(side));
					if (output.length() > 100)
					{
						output = output.substring(0, 100);
					}
					output.trim();
					player.sendChatToPlayer("ReadOut: " + output);
				}
			}
			else if (meta == 1)
			{

			}

		}

		return false;
	}
}
