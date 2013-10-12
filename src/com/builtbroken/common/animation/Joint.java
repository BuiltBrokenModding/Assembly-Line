package com.builtbroken.common.animation;

import java.util.HashSet;
import java.util.Set;

import com.builtbroken.common.Vector3;

public class Joint
{
    protected Set<Joint> childJoints = new HashSet<Joint>();
    protected Joint parentJoint = null;
    protected Vector3 offset, boxmin, boxmax, pose;
    protected String name = "joint";

    public Joint(String name, Vector3 boxmin, Vector3 boxmax, Vector3 pose, Joint... joints)
    {
        this.name = name;
        this.boxmin = boxmin;
        this.boxmax = boxmax;
        this.pose = pose;
        for (Joint joint : joints)
        {
            joint.setParent(this);
            childJoints.add(joint);
        }
    }

    public Joint getParent()
    {
        return this.parentJoint;
    }

    public Vector3 getOffset()
    {
        return this.offset;
    }

    public Vector3 getBoxmin()
    {
        return this.boxmin;
    }

    public Vector3 getBoxmax()
    {
        return this.boxmax;
    }

    public Vector3 getPose()
    {
        return this.pose;
    }

    public String getString()
    {
        return this.name;
    }

    public void setParent(Joint joint)
    {
        this.parentJoint = joint;
    }

}
