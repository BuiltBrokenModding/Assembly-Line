package dark.api.al.coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Used to both register task and fake machines for the encoder to use to create new programs.
 *
 * @author DarkGuardsman */
public class ArmbotTaskManager
{
    /** A class of all available commands.
     *
     * String - Command name. Command - The actual command class. */
    private static final Set<IDeviceTask> COMMANDS = new HashSet<IDeviceTask>();

    private static final HashMap<String, IArmbot> SUDO_BOTS = new HashMap<String, IArmbot>();

    /** Registers a command and tells armbots that it exists */
    public static void registerCommand(IDeviceTask task)
    {
        if (!COMMANDS.contains(task))
        {
            COMMANDS.add(task);
        }
    }

    /** returns the first command with the same name */
    public static IDeviceTask getCommand(String name)
    {
        for (IDeviceTask command : COMMANDS)
        {
            if (command.getMethodName().equalsIgnoreCase(name))
            {
                return command;
            }
        }
        return null;
    }

    /** Gets all commands with the given name though there should only be one */
    public static List<IDeviceTask> getCommands(String name)
    {
        List<IDeviceTask> tasks = new ArrayList<IDeviceTask>();
        for (IDeviceTask command : COMMANDS)
        {
            if (command.getMethodName().equalsIgnoreCase(name))
            {
                tasks.add(command);
            }
        }
        return tasks;
    }

    /** Don't actually register the real machine. Register a fake version so that a code can use it
     * for simulations */
    public static void registerMachine(String name, IArmbot bot)
    {
        if (!SUDO_BOTS.containsKey(name))
        {
            SUDO_BOTS.put(name, bot);
        }
    }

    /** Do not edit the return or you will change the behavior of all machine that use this list
     *
     * @return The list of registered sudo machines for the encoder to check against */
    public static HashMap<String, IArmbot> getSudoMachines()
    {
        return SUDO_BOTS;
    }

    /** Get one of the sudo bots in the hashmap. Make sure to clone before editing */
    public static IArmbot getBot(String string)
    {
        return SUDO_BOTS.get(string);
    }
}
