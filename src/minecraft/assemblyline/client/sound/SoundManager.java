package assemblyline.client.sound;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundManager
{
	@ForgeSubscribe
	public void onSound(SoundLoadEvent event)
	{
		try
		{
			event.manager.soundPoolSounds.addSound("assemblyline/conveyor.wav", getClass().getResource("conveyor.wav"));

		}
		catch (Exception e)
		{
			System.err.println("Failed to register one or more sounds.");
		}
	}
}
