package dark.api.al.coding;

/** Task in which it doesn't go right to the next task in the row. In this case the task will store
 * the entry point, and exit points. As well handle anything in between. Examples are IF statements
 * and loops.
 *
 * @author DarkGuardsman */
public interface ISplitArmbotTask extends IDeviceTask
{
    /** Point were this task is entered from. Normally is the task above it, and is never used. */
    public IDeviceTask getEntryPoint();

    /** There is always only one exit though you can do logic to pick from all your exit points */
    public IDeviceTask getExitPoint();

    /** Mainly used by the coder to understand the limit on connections */
    public int getMaxExitPoints();

    /** Set by the coder, or when this is clone, to say what task was before this. */
    public ISplitArmbotTask setEntryPoint(IDeviceTask task);

    /** Adds a possible exit point to the split off */
    public void addExitPoint(IDeviceTask task);


}
