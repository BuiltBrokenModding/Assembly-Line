package dark.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import dark.core.common.DarkMain;

public class DarkCoreModContainer extends DummyModContainer
{
    public DarkCoreModContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "DarkCoreLoader";
        meta.name = "Dark Core Loader";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList("DarkGuardsman aka DarkCow");
        meta.description = "Core mod loader and asm transformer for Dark's Core Machine.";
        meta.url = "www.BuiltBroken.com";
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        LinkedList<ArtifactVersion> deps = new LinkedList<ArtifactVersion>();
        deps.add(VersionParser.parseVersionReference("DarkCore@[0.2.0.85,)"));
        return deps;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event)
    {

    }

    @Subscribe
    public void init(FMLInitializationEvent event)
    {

    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return VersionParser.parseRange("[1.6.2,)");
    }
}
