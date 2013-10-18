package dark.api.al.coding;

/** Used to tell the program that this task is used to tell the program were to go next. Used by
 * things like LOOP, IF, and GOTO statement's end catches. Not actually used by the statement itself
 * other than to help control the flow of the program
 *
 * @author DarkGuardsman */
public interface IRedirectTask extends IProcessTask
{
    /** Were does this task redirect to */
    public IProcessTask getExit();
}
