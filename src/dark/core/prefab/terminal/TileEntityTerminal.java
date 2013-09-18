package dark.core.prefab.terminal;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dark.core.network.PacketHandler;
import dark.core.prefab.access.AccessLevel;
import dark.core.prefab.access.ISpecialAccess;
import dark.core.prefab.access.UserAccess;
import dark.core.prefab.machine.TileEntityEnergyMachine;
import dark.core.prefab.machine.TileEntityMachine.SimplePacketTypes;

/** @author Calclavia, DarkGuardsman */
public abstract class TileEntityTerminal extends TileEntityEnergyMachine implements IPacketReceiver, ITerminal
{

    /** A list of everything typed inside the terminal */
    private final List<String> terminalOutput = new ArrayList<String>();

    /** The amount of lines the terminal can store. */
    public static final int SCROLL_SIZE = 15;

    /** Used on client side to determine the scroll of the terminal. */
    private int scroll = 0;

    public TileEntityTerminal()
    {
        super(0, 0);
    }

    public TileEntityTerminal(float wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEntityTerminal(float wattsPerTick, float maxEnergy)
    {
        super(wattsPerTick, maxEnergy);
    }

    public void senGUIPacket(EntityPlayer entity)
    {
        if (!this.worldObj.isRemote)
        {
            PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) entity);
        }
    }

    /** Sends all NBT data. Server -> Client */
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return PacketHandler.instance().getPacket(this.getChannel(), this, SimplePacketTypes.NBT.name, nbt);
    }

    /** Sends all Terminal data Server -> Client */
    public void sendTerminalOutputToClients()
    {
        List data = new ArrayList();
        data.add(SimplePacketTypes.TERMINAL_OUTPUT.name);
        data.add(this.getTerminalOuput().size());
        data.addAll(this.getTerminalOuput());

        Packet packet = PacketHandler.instance().getPacket(this.getChannel(), this, data.toArray());

        for (Object entity : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - 10, yCoord - 10, zCoord - 10, xCoord + 10, yCoord + 10, zCoord + 10)))
        {
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).openContainer.getClass().equals(this.getContainer()))
            {
                PacketDispatcher.sendPacketToPlayer(packet, (Player) entity);
            }
        }
    }

    /** Send a terminal command Client -> server */
    public void sendCommandToServer(EntityPlayer entityPlayer, String cmdInput)
    {
        if (this.worldObj.isRemote)
        {
            Packet packet = PacketHandler.instance().getPacket(this.getChannel(), this, SimplePacketTypes.GUI_COMMAND.name, entityPlayer.username, cmdInput);
            PacketDispatcher.sendPacketToServer(packet);
        }
    }

    @Override
    public boolean simplePacket(String id, DataInputStream dis, EntityPlayer player)
    {
        try
        {
            if (!super.simplePacket(id, dis, player))
            {
                if (this.worldObj.isRemote)
                {
                    if (id.equalsIgnoreCase(SimplePacketTypes.TERMINAL_OUTPUT.name))
                    {
                        int size = dis.readInt();

                        List<String> oldTerminalOutput = new ArrayList(this.terminalOutput);
                        this.terminalOutput.clear();

                        for (int i = 0; i < size; i++)
                        {
                            this.terminalOutput.add(dis.readUTF());
                        }

                        if (!this.terminalOutput.equals(oldTerminalOutput) && this.terminalOutput.size() != oldTerminalOutput.size())
                        {
                            this.setScroll(this.getTerminalOuput().size() - SCROLL_SIZE);
                        }
                        return true;
                    }
                }
                else
                {
                    if (id.equalsIgnoreCase(SimplePacketTypes.GUI_COMMAND.name))
                    {
                        CommandRegistry.onCommand(this.worldObj.getPlayerEntityByName(dis.readUTF()), this, dis.readUTF());
                        this.sendTerminalOutputToClients();
                        return true;
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> getTerminalOuput()
    {
        return this.terminalOutput;
    }

    @Override
    public boolean addToConsole(String msg)
    {
        if (!this.worldObj.isRemote)
        {
            int usedLines = 0;

            msg.trim();
            if (msg.length() > 23)
            {
                msg = msg.substring(0, 22);
            }

            this.getTerminalOuput().add(msg);
            this.sendTerminalOutputToClients();
            return true;
        }

        return false;
    }

    @Override
    public void scroll(int amount)
    {
        this.setScroll(this.scroll + amount);
    }

    @Override
    public void setScroll(int length)
    {
        this.scroll = Math.max(Math.min(length, this.getTerminalOuput().size()), 0);
    }

    @Override
    public int getScroll()
    {
        return this.scroll;
    }

}
