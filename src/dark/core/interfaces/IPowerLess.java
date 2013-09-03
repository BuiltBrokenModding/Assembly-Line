package dark.core.interfaces;

public interface IPowerLess
{
    /** Should this run without power */
    public boolean runPowerLess();

    /** Set if this should run powerless */
    public void setPowerLess(boolean bool);
}
