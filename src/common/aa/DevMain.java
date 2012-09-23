package aa;

import java.io.File;


import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "DevBox", name = "Dev Box", version = "Unknown")
@NetworkMod(channels = { "DevD" }, clientSideRequired = true, serverSideRequired = false)

public class DevMain{	
	
    public DevMain instance;
    @SidedProxy(clientSide = "aa.DevProxy", serverSide = "aa.DevProxy")
	public static DevProxy proxy;
    Block devBlock = new BlockDev(3533);
	
	 @PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
	  proxy.preInit();  
	  GameRegistry.registerBlock(devBlock);
	}
	@Init
	public void load(FMLInitializationEvent evt)
    {
		//register
		proxy.init();
		GameRegistry.registerTileEntity(TileEntityAntiMob.class, "DevAntiMob");
		//Names and lang stuff
		
	}
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
		
	}

}
