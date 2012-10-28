package assemblyline.interaction;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityMachineInput extends TileEntityElectricityReceiver implements IPacketReceiver,IInventory {
	public float energyReq = .1f;
	public float energyMax = 10f;
	public float energyStor = 0f;
	private ItemStack[] containingItems = new ItemStack[1];
	public ForgeDirection dir = ForgeDirection.DOWN;
	private int count = 0;
	@Override
	public double wattRequest() {
		return energyMax-energyStor;
	}
	@Override
	public void updateEntity()
    {	
		if(count++ >=10){
			count = 0;
			if(!isDisabled())
			{
				int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				ForgeDirection searchPosition = ForgeDirection.getOrientation(this.getBeltDirection());
				dir = searchPosition;
				try
				{
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord+searchPosition.offsetX,yCoord+ searchPosition.offsetY,zCoord+ searchPosition.offsetZ, xCoord+searchPosition.offsetX+1,yCoord+ searchPosition.offsetY+1,zCoord+ searchPosition.offsetZ+1);
					TileEntity bEnt = worldObj.getBlockTileEntity(xCoord+searchPosition.getOpposite().offsetX,yCoord+ searchPosition.getOpposite().offsetY,zCoord+ searchPosition.getOpposite().offsetZ);
					List<EntityItem> itemsBehind = worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);
					ItemStack tItem = this.containingItems[0];
					if(itemsBehind.size() > 0 && this.energyStor > this.energyReq && bEnt instanceof IInventory)
					{ energyStor -= energyReq; 
						
						for(EntityItem entity : itemsBehind)
						{
							ItemStack eStack = entity.item;
							int ite = eStack.stackSize;
							if(bEnt instanceof TileEntityChest)
							{
								TileEntityChest bEntChest2 = null;
								TileEntityChest bEntChest = (TileEntityChest)bEnt;
								for(int i = 2; i<6; i++)
								{
									ForgeDirection si = ForgeDirection.getOrientation(i);
									if(worldObj.getBlockTileEntity(xCoord+dir.getOpposite().offsetX+si.offsetX, yCoord+dir.getOpposite().offsetY+si.offsetY, zCoord+dir.getOpposite().offsetZ+si.offsetZ) instanceof TileEntityChest)
									{
										bEntChest2 = (TileEntityChest) worldObj.getBlockTileEntity(xCoord+dir.getOpposite().offsetX+si.offsetX, yCoord+dir.getOpposite().offsetY+si.offsetY, zCoord+dir.getOpposite().offsetZ+si.offsetZ);
										break;
									}
								}
							if(eStack != null && eStack.stackSize > 0){
								for(int i =0; i < bEntChest.getSizeInventory(); i++)
								{
									
									ItemStack stack = bEntChest.getStackInSlot(i);
									if(stack == null)
									{
										bEntChest.setInventorySlotContents(i, eStack);
										entity.setDead();
										eStack = null;
										break;
									}else
									if(stack.getItem().equals(eStack.getItem()) && stack.getItemDamage() == eStack.getItemDamage())
									{
										int rej = Math.max((stack.stackSize + eStack.stackSize) - stack.getItem().getItemStackLimit(), 0);
										stack.stackSize = Math.min(Math.max((stack.stackSize + eStack.stackSize - rej),0),stack.getItem().getItemStackLimit());
										eStack.stackSize = rej;
										bEntChest.setInventorySlotContents(i, stack);
										if(eStack.stackSize <= 0)
										{
											entity.setDead();
											eStack = null;
											break;
										}
										
									}
									
								}
							}
								if(bEntChest2 != null && eStack != null && eStack.stackSize > 0)
								{
									for(int i =0; i < bEntChest2.getSizeInventory(); i++)
									{
										ItemStack stack = bEntChest2.getStackInSlot(i);
										if(stack == null)
										{
											bEntChest2.setInventorySlotContents(i, eStack);
											entity.setDead();
											eStack = null;
											break;
										}else
										if(stack.getItem().equals(eStack.getItem()) && stack.getItemDamage() == eStack.getItemDamage())
										{
											int rej = Math.max((stack.stackSize + eStack.stackSize) - stack.getItem().getItemStackLimit(), 0);
											stack.stackSize = Math.min(Math.max((stack.stackSize + eStack.stackSize - rej),0),stack.getItem().getItemStackLimit());
											eStack.stackSize = rej;
											bEntChest2.setInventorySlotContents(i, stack);
											if(eStack.stackSize <= 0)
											{
												entity.setDead();
												eStack = null;
												break;
											}
										}
									}
								}if(entity != null && eStack != null){
								if(eStack != null && eStack.stackSize <= 0)
								{
									entity.setDead();
									eStack = null;
									break;
								}else
								{
									entity.setDead();
						            EntityItem var23 = new EntityItem(worldObj, entity.posX, entity.posY + 0.1D, entity.posZ, eStack);
						            worldObj.spawnEntityInWorld(var23);
								}}
							
							}//end chest trade
							//TODO setup for ISideInventory
						}
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
	    
			}
	    }
    }
	public int getBeltDirection()
	{
		int meta = worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		if(meta >= 4 && meta < 8) 
		{
			switch(meta)
			{
			case 4: return 2;
			case 5: return 5;
			case 6: return 3;
			case 7: return 4;
			}
		}
		return 0;
	}
	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		if(side == dir ||side == dir.getOpposite())
		{
			return false;
		}
		return true;
	}
	 @Override
	    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.readFromNBT(par1NBTTagCompound);
	        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
	        this.containingItems = new ItemStack[this.getSizeInventory()];

	        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
	        {
	            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
	            byte var5 = var4.getByte("Slot");

	            if (var5 >= 0 && var5 < this.containingItems.length)
	            {
	                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
	            }
	        }
	    }
	    /**
	     * Writes a tile entity to NBT.
	     */
	    @Override
	    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.writeToNBT(par1NBTTagCompound);
	        NBTTagList var2 = new NBTTagList();

	        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
	        {
	            if (this.containingItems[var3] != null)
	            {
	                NBTTagCompound var4 = new NBTTagCompound();
	                var4.setByte("Slot", (byte)var3);
	                this.containingItems[var3].writeToNBT(var4);
	                var2.appendTag(var4);
	            }
	        }

	        par1NBTTagCompound.setTag("Items", var2);
	    }
	    @Override
	    public int getSizeInventory()
	    {
	        return this.containingItems.length;
	    }
	    @Override
	    public ItemStack getStackInSlot(int par1)
	    {
	        return this.containingItems[par1];
	    }
	    @Override
	    public ItemStack decrStackSize(int par1, int par2)
	    {
	        if (this.containingItems[par1] != null)
	        {
	            ItemStack var3;

	            if (this.containingItems[par1].stackSize <= par2)
	            {
	                var3 = this.containingItems[par1];
	                this.containingItems[par1] = null;
	                return var3;
	            }
	            else
	            {
	                var3 = this.containingItems[par1].splitStack(par2);

	                if (this.containingItems[par1].stackSize == 0)
	                {
	                    this.containingItems[par1] = null;
	                }

	                return var3;
	            }
	        }
	        else
	        {
	            return null;
	        }
	    }
	    @Override
	    public ItemStack getStackInSlotOnClosing(int par1)
	    {
	        if (this.containingItems[par1] != null)
	        {
	            ItemStack var2 = this.containingItems[par1];
	            this.containingItems[par1] = null;
	            return var2;
	        }
	        else
	        {
	            return null;
	        }
	    }
	    @Override
	    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	    {
	        this.containingItems[par1] = par2ItemStack;

	        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
	        {
	            par2ItemStack.stackSize = this.getInventoryStackLimit();
	        }
	    }
	    @Override
	    public String getInvName()
	    {
	        return "Ejector";
	    }
	    @Override
	    public int getInventoryStackLimit()
	    {
	    	//TODO change
	        return 0;
	    }
	    @Override
	    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	    {
	        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	    }
	    @Override
	    public void openChest() { }
	    @Override
	    public void closeChest() { }
		@Override
		public void onReceive(TileEntity sender, double amps, double voltage,
				ForgeDirection side) {
			this.energyStor+=(amps*voltage);
			
		}
		@Override
		public void handlePacketData(NetworkManager network, int packetType,
				Packet250CustomPayload packet, EntityPlayer player,
				ByteArrayDataInput dataStream) {
			
		}
}
