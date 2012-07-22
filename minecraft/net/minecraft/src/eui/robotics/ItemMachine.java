package net.minecraft.src.eui.robotics;
import java.util.ArrayList;

import net.minecraft.src.*;

public class ItemMachine extends ItemBlock {       

        public ItemMachine(int id) {
                super(id);
                setMaxDamage(0);
                setHasSubtypes(true);
        }
        @Override
        public int getMetadata(int metadata)
        {
            return metadata;
        }
        @Override
        public String getItemName()
        {
            return "Machine";
        }       
    }

