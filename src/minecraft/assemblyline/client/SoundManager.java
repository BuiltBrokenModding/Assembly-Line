package assemblyline.client;

import java.net.URL;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import assemblyline.common.AssemblyLine;

public class SoundManager
{
	public static final SoundManager INSTANCE = new SoundManager();

	public static final String[] SOUND_FILES = { "conveyor.ogg" };

	@ForgeSubscribe
	public void loadSoundEvents(SoundLoadEvent event)
	{
		for (int i = 0; i < SOUND_FILES.length; i++)
		{
			URL url = this.getClass().getResource(AssemblyLine.DIRECTORY + SOUND_FILES[i]);

			event.manager.soundPoolSounds.addSound(AssemblyLine.DIRECTORY + SOUND_FILES[i], url);

			if (url == null)
			{
				System.out.println("Invalid sound file: " + SOUND_FILES[i]);
			}
		}
	}
}
