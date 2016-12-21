package com.builtbroken.assemblyline.content.rail.carts;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.api.rails.ITransportCartHasCargo;
import com.builtbroken.mc.prefab.entity.cart.EntityAbstractCart;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.filters.IInventoryFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Cart for all transport rail carts for assembly line
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/21/2016.
 */
public class EntityCart extends EntityAbstractCart implements ITransportCartHasCargo
{
    /** Internal var for cart type, never access directly */
    private CartTypes _cartType = CartTypes.EMPTY;
    /** Inventory for the cart, use {@link #getInventory()} to access */
    protected ExternalInventory inventory;
    /** Filter for insertion of items into cart, use {@link #getInventoryFilter()} */
    protected IInventoryFilter filter;

    /**
     * Creates a new cart
     *
     * @param world
     */
    public EntityCart(World world)
    {
        super(world);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //Checks if the size changed, No change should result in zero
        if (Math.abs(getType().length - length - getType().width - width) > 0.1)
        {
            length = getType().length;
            width = getType().width;
            markBoundsInvalid();
        }
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (getType() == CartTypes.STACK || getType() == CartTypes.JABBA_BARREL || getType() == CartTypes.CRATE)
        {
            //TODO sync inventory to client
        }
    }

    @Override
    public ExternalInventory getInventory()
    {
        if (inventory == null)
        {
            inventory = new ExternalInventory(this, getType().inventorySize);
        }
        return inventory;
    }

    @Override
    public ItemStack toStack()
    {
        return new ItemStack(AssemblyLine.itemCart, 1, getType().ordinal());
    }

    /**
     * Sets the type of the cart, should
     * never be run after the cart is created
     *
     * @param cartType - type of the cart
     */
    public void setType(final CartTypes cartType)
    {
        _cartType = cartType;
        this.width = getType().width;
        this.length = getType().length;
        markBoundsInvalid();
        markForClientSync();
    }

    /**
     * Gets the type of cart
     *
     * @return cart type
     */
    public CartTypes getType()
    {
        return _cartType;
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setType(CartTypes.values()[nbt.getInteger("cartType")]);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("cartType", getType().ordinal());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeInt(getType().ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        setType(CartTypes.values()[additionalData.readInt()]);
    }

    @Override
    public IInventoryFilter getInventoryFilter()
    {
        return null;
    }
}
