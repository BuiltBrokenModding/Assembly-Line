package dark.assembly.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import dark.assembly.common.AssemblyLine;
@SideOnly(Side.CLIENT)
public class SoundHandler
{
    public static final SoundHandler INSTANCE = new SoundHandler();

    public static final String[] SOUND_FILES = { "conveyor.ogg" };

    @ForgeSubscribe
    public void loadSoundEvents(SoundLoadEvent event)
    {
        for (int i = 0; i < SOUND_FILES.length; i++)
        {
            event.manager.soundPoolSounds.addSound(AssemblyLine.instance.DIRECTORY_NO_SLASH + SOUND_FILES[i]);
        }
    }
}
