package dark.api.al.armbot;

/** Task in which it doesn't go right to the next task in the row. In this case the task will store
 * the entry point, and exit points. As well handle anything in between. Examples are IF statements
 * and loops.
 *
 * @author DarkGuardsman */
public interface ISplitArmbotTask extends IArmbotTask
{
    /** Point were this task is entered from. Normally is the task above it, and is never used. */
    public IArmbotTask getEntryPoint();

    /** There is always only one exit though you can do logic to pick from all your exit points */
    public IArmbotTask getExitPoint();

    /** Mainly used by the coder to understand the limit on connections */
    public int getMaxExitPoints();

    /** Set by the coder, or when this is clone, to say what task was before this. */
    public ISplitArmbotTask setEntryPoint(IArmbotTask task);

    /** Adds a possible exit point to the split off */
    public void addExitPoint(IArmbotTask task);


}
