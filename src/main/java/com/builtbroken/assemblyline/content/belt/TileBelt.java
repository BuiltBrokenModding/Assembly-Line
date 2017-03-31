package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/** Basic implementation of a conveyor belt that stores items inside the block
 * Created by DarkGuardsman on 8/31/2015.
 */
public class TileBelt extends Tile implements IPacketIDReceiver
{
    /** ItemStack currently on the belt */
    protected ItemStack stackOnBelt;
    /** Position of the item on the belt */
    protected int beltPositionStep = 0;

    /** Facing direction (NORTH, SOUTH, EAST, WEST) */
    protected ForgeDirection facingDirection = ForgeDirection.NORTH;
    /** cached direction based on belt metadata */
    protected ForgeDirection placementSide;

    protected BeltType beltType = BeltType.NORMAL;

    public TileBelt()
    {
        this("conveyorBelt");
    }

    public TileBelt(String name)
    {
        super(name, Material.iron);
        this.bounds = new Cube(0, 0, 0, 1, .4f, 1);
    }

    @Override
    public void update()
    {
        super.update();
        this.firstTick();
        if (ticks % 5 == 0 && stackOnBelt != null)
        {
            updateItemPosition();
        }
    }

    protected void updateItemPosition()
    {
        beltPositionStep++;
        if (beltPositionStep >= beltType.maxPositions)
        {
            moveItemToNextBlock();
        }
    }

    protected void moveItemToNextBlock()
    {
        Pos pos = toPos().add(facingDirection);
        TileEntity tile = pos.getTileEntity(world());

        if (tile instanceof TileBelt)
        {
            if (beltType != BeltType.ELEVATED && ((TileBelt) tile).getFacingDirection() != getFacingDirection().getOpposite())
            {
                if (((TileBelt) tile).stackOnBelt == null)
                {
                    ((TileBelt) tile).setItemOnBelt(stackOnBelt);
                    setItemOnBelt(null);
                }
            }
        }
        //TODO check if the block doesn't have a collision box in the area we want to drop an item
        else if (pos.isAirBlock(world()))
        {
            InventoryUtility.dropItemStack(world(), pos.add(0.5), stackOnBelt);
            setItemOnBelt(null);
        }
    }

    public void setItemOnBelt(ItemStack stack)
    {
        if (stackOnBelt != null && stack == null)
        {
            stackOnBelt = null;
            beltPositionStep = 0;
        } else if (stackOnBelt == null && stack != null)
        {
            stackOnBelt = stack.copy();
        }
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        this.placementSide = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    public Tile newTile()
    {
        return new TileBelt();
    }

    public ForgeDirection getPlacementSide()
    {
        return placementSide;
    }

    public void setPlacementSide(ForgeDirection side)
    {
        if (beltType != BeltType.UP && beltType != BeltType.DOWN)
        {
            if (side != placementSide)
            {
                world().setBlockMetadataWithNotify(xi(), yi(), zi(), side.ordinal(), 3);
                this.placementSide = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
            }
        } else
        {
            AssemblyLine.INSTANCE.logger().error("Something attempted to set a belt to an invalid side, Belt: " + this, new RuntimeException());
        }
    }

    public ForgeDirection getFacingDirection()
    {
        return facingDirection;
    }

    /**
     * Sets the facing direction of the belt
     *
     * @param dir - direction, can't be UP or DOWN
     */
    public void setFacingDirection(ForgeDirection dir)
    {
        if (dir != ForgeDirection.UP && dir != ForgeDirection.DOWN)
        {
            if (dir != facingDirection)
            {
                this.facingDirection = dir;
                if (isServer())
                {
                    //TODO send packet to all clients in area
                }
            }
        } else
        {
            AssemblyLine.INSTANCE.logger().error("Something attempted to set a belt to an invalid dir, Belt: " + this, new RuntimeException());
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if(isClient())
        {
            if(id == 1 || id == 0)
            {
                byte bt = buf.readByte();
                byte fd = buf.readByte();
                if(bt >= 0 && bt < BeltType.values().length)
                {

                }

                if(id == 1)
                {
                    this.stackOnBelt = ByteBufUtils.readItemStack(buf);
                }
            }
        }
        return false;
    }

    @Override
    public PacketTile getDescPacket()
    {
        if(stackOnBelt == null)
        {
            return new PacketTile(this, 1, (byte)beltType.ordinal(), (byte)facingDirection.ordinal());
        }
        return new PacketTile(this, 0, (byte)beltType.ordinal(), (byte)facingDirection.ordinal(), stackOnBelt);
    }

    /**
     * Helper enum to store types of belts and data that goes with each belt
     */
    public enum BeltType
    {
        NORMAL(16),
        ELEVATED(16),
        UP(27),
        DOWN(27);

        public final int maxPositions;

        BeltType(int maxPositions)
        {
            this.maxPositions = maxPositions;
        }
        //TODO store model
        //TODO store texture
    }
}
