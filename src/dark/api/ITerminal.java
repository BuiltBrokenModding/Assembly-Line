package dark.api;

import java.util.List;

import dark.core.interfaces.IScroll;

public interface ITerminal extends ISpecialAccess, IScroll
{
    /** Gets an output of the string stored in the console. */
    public List<String> getTerminalOuput();

    /** Adds a string to the console. Server side only. */
    public boolean addToConsole(String msg);
}
