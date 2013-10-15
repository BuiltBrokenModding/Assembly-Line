package dark.api.al.armbot;

import java.util.HashMap;

public interface IProgram
{
    /** Called when the program is added to an encoder, machine, or devices.
     * memory values. */
    public void init();

    /** Variables this program has to operate. Is still limited by the actual machine */
    public HashMap<String, Object> getDeclairedVarables();

    public IArmbotTask getNextTask();
}
