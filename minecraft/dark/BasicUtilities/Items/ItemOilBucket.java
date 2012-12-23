package dark.BasicUtilities.Items;

import dark.BasicUtilities.BasicUtilitiesMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import universalelectricity.prefab.UETab;

public class ItemOilBucket extends ItemBucket
{
	public ItemOilBucket(int id, int texture)
	{
		super(id, BasicUtilitiesMain.oilMoving.blockID);
		this.setIconIndex(texture);
		this.setCreativeTab(UETab.INSTANCE);
		this.setContainerItem(Item.bucketEmpty);
		this.setItemName("bucketOil");
	}

	@Override
	public String getTextureFile()
	{
		return BasicUtilitiesMain.ITEM_PNG;
	}

	@ForgeSubscribe
	public void onBucketFill(FillBucketEvent event)
	{
		if (event.current.itemID == Item.bucketEmpty.shiftedIndex)
		{
			World worldObj = event.world;
			MovingObjectPosition position = event.target;

			int blockID = worldObj.getBlockId(position.blockX, position.blockY, position.blockZ);

			if ((blockID == BasicUtilitiesMain.oilStill.blockID || blockID == BasicUtilitiesMain.oilMoving.blockID) && worldObj.getBlockMetadata(position.blockX, position.blockY, position.blockZ) == 0)
			{
				worldObj.setBlockWithNotify(position.blockX, position.blockY, position.blockZ, 0);
				event.result = new ItemStack(BasicUtilitiesMain.itemOilBucket);
				event.current.stackSize--;
				event.setResult(Result.ALLOW);
			}
		}
	}
}