package dark.api.al.coding;

/** Task in which it doesn't go right to the next task in the row. In this case the task will store
 * the entry point, and exit points. As well handle anything in between. Examples are IF statements
 * and loops.
 *
 * @author DarkGuardsman */
public interface ILogicTask extends IProcessTask
{
    /** There is always only one exit though you can do logic to pick from all your exit points. Exit
     * is the next task rather than the exit of the statement. Use #IRedirectTask to force the logic back to this task. */
    public IProcessTask getExitPoint();

    /** Mainly used by the encoder to understand the limit on connections */
    public int getMaxExitPoints();

    /** Adds a possible exit point to the split off */
    public void addExitPoint(IProcessTask task);

}
