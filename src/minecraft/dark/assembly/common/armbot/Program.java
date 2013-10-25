package dark.assembly.common.armbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.ITask;
import dark.api.al.coding.TaskRegistry;
import dark.core.prefab.helpers.NBTFileHelper;

public class Program implements IProgram
{
    protected Vector2 currentPos = new Vector2(0, 0);
    protected ITask currentTask;
    protected IProgrammableMachine machine;
    protected HashMap<Vector2, ITask> tasks = new HashMap();
    protected HashMap<String, Object> varables = new HashMap();
    protected int width = 0, hight = 0;
    boolean started = false;

    @Override
    public void init(IProgrammableMachine machine)
    {
        this.machine = machine;
        int w = 0;
        int h = 0;
        List<Vector2> removeList = new ArrayList<Vector2>();
        for (Entry<Vector2, ITask> entry : this.tasks.entrySet())
        {
            if (entry.getValue() != null)
            {
                entry.getValue().setProgram(this);
                if (entry.getValue().getPosition().intX() > w)
                {
                    w = entry.getValue().getPosition().intX();
                }
                if (entry.getValue().getPosition().intY() > h)
                {
                    h = entry.getValue().getPosition().intY();
                }
            }
            else
            {
                //Should be rare that one of the slots would be null
                removeList.add(entry.getKey());
            }
        }
        for (Vector2 vec : removeList)
        {
            this.tasks.remove(vec);
        }
        this.width = w;
        this.hight = h;
    }

    @Override
    public IProgrammableMachine getMachine()
    {
        return this.machine;
    }

    @Override
    public HashMap<String, Object> getDeclairedVarables()
    {
        return varables;
    }

    @Override
    public ITask getNextTask()
    {
        if (!started)
        {
            this.currentTask = this.getTaskAt(currentPos);
            this.currentPos.add(new Vector2(1, 0));
        }
        return this.currentTask;
    }

    @Override
    public ITask getTaskAt(Vector2 vector2)
    {
        if (vector2 != null)
        {
            return this.tasks.get(new Vector2(vector2.intX(), vector2.intY()));
        }
        return null;
    }

    @Override
    public HashMap<Vector2, ITask> getTaskMap()
    {
        return this.tasks;
    }

    @Override
    public void setTaskAt(Vector2 vector2, ITask task)
    {
        if (vector2 != null)
        {
            if (task != null)
            {
                task.setPosition(vector2);
                if (task.getPosition().x > this.width)
                {
                    this.width = (int) task.getPosition().x;
                }
                if (task.getPosition().y > this.hight)
                {
                    this.hight = (int) task.getPosition().y;
                }
                this.tasks.put(new Vector2(vector2.intX(), vector2.intY()), task);
            }
            else if (this.tasks.containsKey(vector2))
            {
                this.tasks.remove(vector2);
                if (vector2.intY() == this.hight && !this.isThereATaskInRow(this.hight))
                {
                    this.hight--;
                }
                else if (!this.isThereATaskInRow(vector2.intY()))
                {
                    this.moveAll(vector2.intY(), true);
                }
            }
        }
    }

    public boolean isThereATaskInRow(int row)
    {
        Vector2 vec = new Vector2(0, row);
        Vector2 slide = new Vector2(1, 0);
        for (int x = 0; x <= this.width; x++)
        {
            if (this.getTaskAt(vec) != null)
            {
                return true;
            }
            vec.add(slide);
        }
        return false;
    }

    public boolean isThereATaskInColume(int colume)
    {
        Vector2 vec = new Vector2(colume, 0);
        Vector2 slide = new Vector2(0, 1);
        for (int y = 0; y <= this.width; y++)
        {
            if (this.getTaskAt(vec) != null)
            {
                return true;
            }
            vec.add(slide);
        }
        return false;
    }

    /** Move all tasks at the row and in the direction given.
     * 
     * @param row - row number or Y value of the position from the task
     * @param up - true will move all the tasks up one, false will move all the tasks down one */
    public void moveAll(int row, boolean up)
    {
        List<ITask> moveList = new ArrayList<ITask>();
        final Vector2 moveDown = up ? new Vector2(-1, 0) : new Vector2(1, 0);
        Vector2 targetPos;
        ITask tagetTask;
        /* Gather all task and remove them so they can be re-added wither there new positions */
        for (int x = 0; x <= this.width; x++)
        {
            for (int y = row; y <= this.hight; y++)
            {
                targetPos = new Vector2(x, y);
                tagetTask = this.getTaskAt(targetPos);
                if (tagetTask != null)
                {
                    //Add the task to the move list
                    moveList.add(tagetTask);
                    //Removes the task
                    this.tasks.remove(targetPos);
                }
            }
        }
        /* Update all the task locations */
        for (ITask moveTask : moveList)
        {
            moveTask.setPosition(moveTask.getPosition().add(moveDown));
            this.setTaskAt(moveTask.getPosition(), moveTask);
        }
        //TODO send to the client the updates map and key to unlock the delete button
    }

    @Override
    public void insertTask(Vector2 vector2, ITask task)
    {
        if (vector2 != null && task != null)
        {
            if (this.getTaskAt(vector2) != null)
            {
                this.moveAll(vector2.intY(), false);
            }
            else
            {
                this.setTaskAt(vector2, task);
            }
        }
    }

    @Override
    public void reset()
    {
        if (this.currentTask != null)
        {
            NBTTagCompound tag = this.currentTask.save(new NBTTagCompound());
            this.currentTask = TaskRegistry.getCommand(this.currentTask.getMethodName()).clone();
            this.currentTask.load(tag);
            this.setTaskAt(this.currentTask.getPosition(), this.currentTask);
            this.currentTask = null;
        }
        this.currentPos = new Vector2(0, 0);
    }

    @Override
    public void setVar(String name, Object object)
    {
        if (name != null)
        {
            if (object != null)
            {
                this.varables.put(name, object);
            }
            else
            {
                this.varables.remove(name);
            }
        }

    }

    @Override
    public Object getVar(String name)
    {
        return this.varables.get(name);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        //Save process list
        NBTTagList taskList = new NBTTagList();
        for (Entry<Vector2, ITask> entry : this.tasks.entrySet())
        {
            entry.getValue().setPosition(entry.getKey());
            NBTTagCompound task = entry.getValue().save(new NBTTagCompound());
            if (entry.getKey().equals(this.currentTask.getPosition()))
            {
                task.setBoolean("currentTask", true);
                entry.getValue().saveProgress(task);
            }
            task.setString("methodName", entry.getValue().getMethodName());
            task.setInteger("positionX", entry.getKey().intX());
            task.setInteger("positionY", entry.getKey().intY());
            taskList.appendTag(task);
        }
        nbt.setTag("tasks", taskList);
        //save varables
        taskList = new NBTTagList();
        for (Entry<String, Object> var : this.varables.entrySet())
        {
            taskList.appendTag(NBTFileHelper.saveObject(var.getKey(), var.getValue()));
        }
        nbt.setTag("vars", taskList);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        //Load process list
        NBTTagList taskList = nbt.getTagList("tasks");
        for (int s = 0; s < taskList.tagCount(); ++s)
        {
            NBTTagCompound tag = (NBTTagCompound) taskList.tagAt(s);
            ITask task = TaskRegistry.getCommand(tag.getString("methodName"));
            if (task != null)
            {
                task = task.clone();
                if (task != null)
                {
                    task.load(tag);
                    task.setPosition(new Vector2(nbt.getInteger("positionX"), nbt.getInteger("positionY")));
                    this.tasks.put(task.getPosition(), task);
                    if (tag.getBoolean("currentTask"))
                    {
                        this.currentTask = task;
                        task.loadProgress(tag);
                        this.currentPos = task.getPosition();
                    }
                    if (task.getPosition().x > this.width)
                    {
                        this.width = (int) task.getPosition().x;
                    }
                    if (task.getPosition().y > this.hight)
                    {
                        this.hight = (int) task.getPosition().y;
                    }
                }
            }
        }
        taskList = nbt.getTagList("vars");
        for (int s = 0; s < taskList.tagCount(); ++s)
        {
            NBTTagCompound tag = (NBTTagCompound) taskList.tagAt(s);
            this.varables.put(tag.getName(), NBTFileHelper.loadObject(tag, tag.getName()));
        }
    }

    @Override
    public Program clone()
    {
        Program program = new Program();
        program.load(this.save(new NBTTagCompound()));
        program.reset();
        return program;
    }

    @Override
    public Vector2 getSize()
    {
        return new Vector2(this.width, this.hight);
    }

}
