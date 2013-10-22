package com.builtbroken.common;

/** Class to track a user. Mainly used to create groups of users, and is mostly a prefab since it
 * only stores a string name. People who use this class should extend it to make better use out of
 * the class.
 * 
 * @author Robert Seifert */
public class User
{
    protected String username;

    public User(String username)
    {
        this.username = username;
    }

    public String getName()
    {
        return this.username;
    }
}
