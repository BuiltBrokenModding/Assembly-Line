package dark.fluid.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import dark.core.api.IToolReadOut;
import dark.core.api.IToolReadOut.EnumTools;
import dark.fluid.common.TabFluidMech;

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
			return "item." + "PipeGauge";
		}
		return "item." + this.getUnlocalizedName() + "." + meta;
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

				if (blockEntity instanceof IToolReadOut)
				{
					String output = ((IToolReadOut) blockEntity).getMeterReading(player, ForgeDirection.getOrientation(side), EnumTools.PIPE_GUAGE);
					if (output.length() > 100)
					{
						output = output.substring(0, 100);
					}
					output.trim();
					player.sendChatToPlayer(ChatMessageComponent.func_111066_d("ReadOut: " + output));
				}
			}
			else if (meta == 1)
			{

			}

		}

		return false;
	}
}
