package dark.assembly.common.armbot;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.TaskRegistry;

public class Program implements IProgram
{
    protected Vector2 currentPos = new Vector2(0, 0);
    protected IDeviceTask currentTask;
    protected HashMap<Vector2, IDeviceTask> tasks = new HashMap();

    @Override
    public void init()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public HashMap<String, Object> getDeclairedVarables()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDeviceTask getNextTask()
    {
        this.currentTask = this.getTaskAt(currentPos);
        this.currentPos.add(new Vector2(1, 0));
        return this.currentTask;
    }

    @Override
    public IDeviceTask getTaskAt(Vector2 vector2)
    {
        if (vector2 != null)
        {
            return this.tasks.get(new Vector2(vector2.intX(), vector2.intY()));
        }
        return null;
    }

    @Override
    public void setTaskAt(Vector2 vector2, IDeviceTask task)
    {
        if (vector2 != null)
        {
            if (task != null)
            {
                this.tasks.put(new Vector2(vector2.intX(), vector2.intY()), task);
            }
            else if (this.tasks.containsKey(vector2))
            {
                this.tasks.remove(vector2);
            }
        }
    }

    @Override
    public void reset(boolean full)
    {
        this.currentTask = null;
    }

    @Override
    public void setVar(String name, Object object)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getVar(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagList taskList = new NBTTagList();
        for (Entry<Vector2, IDeviceTask> entry : this.tasks.entrySet())
        {
            entry.getValue().setPosition(entry.getKey());
            NBTTagCompound task = entry.getValue().save(new NBTTagCompound());
            task.setString("methodName", entry.getValue().getMethodName());
            task.setInteger("positionX", entry.getKey().intX());
            task.setInteger("positionY", entry.getKey().intY());
            taskList.appendTag(task);
        }
        nbt.setTag("tasks", taskList);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        NBTTagList taskList = nbt.getTagList("tasks");
        for (int s = 0; s < taskList.tagCount(); ++s)
        {
            NBTTagCompound tag = (NBTTagCompound) taskList.tagAt(s);
            IDeviceTask task = TaskRegistry.getCommand(tag.getString("methodName"));
            if (task != null)
            {
                task = task.clone();
                if (task != null)
                {
                    task.load(tag);
                    task.setPosition(new Vector2(nbt.getInteger("positionX"), nbt.getInteger("positionY")));
                    this.tasks.put(task.getPosition(), task);
                }
            }
        }

    }

}
