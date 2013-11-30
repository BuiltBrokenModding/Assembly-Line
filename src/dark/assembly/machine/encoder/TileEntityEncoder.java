package dark.assembly.machine.encoder;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dark.api.al.coding.IProcessTask;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.TaskRegistry;
import dark.assembly.armbot.Program;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.machine.TileEntityMachine;

public class TileEntityEncoder extends TileEntityMachine implements ISidedInventory
{
    private ItemStack disk;
    private IInventoryWatcher watcher;
    public static final String PROGRAM_ID = "program", PROGRAM_CHANGE = "programChange", REMOVE_TASK = "removeTask";
    protected IProgram program;

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        if (watcher != null)
        {
            watcher.inventoryChanged();
        }
    }

    @Override
    public String getInvName()
    {
        return "Encoder";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public void setWatcher(IInventoryWatcher watcher)
    {
        this.watcher = watcher;
    }

    public IInventoryWatcher getWatcher()
    {
        return this.watcher;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (this.disk != null)
        {
            NBTTagCompound diskNBT = new NBTTagCompound();
            this.disk.writeToNBT(diskNBT);
            nbt.setCompoundTag("disk", diskNBT);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        NBTTagCompound diskNBT = nbt.getCompoundTag("disk");

        if (diskNBT != null)
        {
            this.disk = ItemStack.loadItemStackFromNBT(diskNBT);
        }

    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dis, Player player)
    {
        try
        {
            boolean su = super.simplePacket(id, dis, player);
            if (!su)
            {
                if (this.worldObj.isRemote)
                {
                    if (id.equalsIgnoreCase(TileEntityEncoder.PROGRAM_ID))
                    {
                        if (dis.readBoolean())
                        {
                            Program program = new Program();
                            program.load(PacketManager.readNBTTagCompound(dis));
                            this.program = program;
                        }
                        else
                        {
                            this.program = null;
                        }
                        return true;
                    }
                }
                else
                {
                    if (id.equalsIgnoreCase(TileEntityEncoder.PROGRAM_CHANGE))
                    {

                        IProcessTask task = TaskRegistry.getCommand(dis.readUTF());
                        task.setPosition(dis.readInt(), dis.readInt());
                        task.load(PacketManager.readNBTTagCompound(dis));
                        this.program.setTaskAt(task.getCol(), task.getRow(), task);
                        this.sendGUIPacket();

                        return true;
                    }
                    else if (id.equalsIgnoreCase(TileEntityEncoder.REMOVE_TASK))
                    {
                        this.program.setTaskAt(dis.readInt(), dis.readInt(), null);
                        this.sendGUIPacket();
                        return true;
                    }
                }
            }
            return su;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return true;
        }
    }

    /** Sends a gui packet only to the given player */
    @Override
    public void sendGUIPacket(EntityPlayer entity)
    {
        if (entity != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            boolean exists = this.program != null;
            if (exists)
            {
                this.program.save(tag);
            }
            PacketDispatcher.sendPacketToPlayer(PacketHandler.instance().getTilePacket(DarkMain.CHANNEL, this, this.program, exists, tag), (Player) entity);
        }
    }

    public void removeTask(Vector2 vec)
    {
        if (vec != null)
        {
            PacketDispatcher.sendPacketToServer(PacketHandler.instance().getTilePacket(DarkMain.CHANNEL, this, vec.intX(), vec.intY()));
        }
    }

    public void updateTask(IProcessTask task)
    {
        if (task != null)
        {
            PacketDispatcher.sendPacketToServer(PacketHandler.instance().getTilePacket(DarkMain.CHANNEL, this, task.getCol(), task.getRow(), task.save(new NBTTagCompound())));
        }
    }

    @Override
    public boolean isInvNameLocalized()
    {
        //TODO ?
        return false;
    }
}
