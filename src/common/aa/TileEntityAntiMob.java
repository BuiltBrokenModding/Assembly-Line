package aa;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.TileEntity;

public class TileEntityAntiMob extends TileEntity {
	@Override
	 public void updateEntity()
		{    	
		 List<Entity> ee = worldObj.loadedEntityList;
		 List<Entity> mobs = new ArrayList<Entity>();
		 for(int i = 0; i <ee.size(); i++)
		 {
			 if(ee.get(i) instanceof EntityMob || ee.get(i) instanceof EntitySlime)
			 {
				 mobs.add(ee.get(i));
			 }
		 }
		 for(int j =0; j < mobs.size();j++)
		 {
			Entity mod =  mobs.get(j);
			if(mod.getDistance(xCoord, yCoord, zCoord) < 40)
			{ 
				mobs.get(j).setDead();
				mobs.remove(j);
			
			}
		 }
		 
		}
}
