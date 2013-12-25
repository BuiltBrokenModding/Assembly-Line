package com.builtbroken.assemblyline.multipart;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;

public class MultipartAL implements IPartFactory
{
	public static MultipartAL INSTANCE;

	public MultipartAL()
	{
		MultiPartRegistry.registerParts(this, new String[] { "assembly_line_basic_wire"});
		MultipartGenerator.registerTrait("universalelectricity.api.energy.IConductor", "com.builtbroken.assemblyline.multipart.TraitConductor");
	}

	@Override
	public TMultiPart createPart(String name, boolean client)
	{
		if (name.equals("assembly_line_basic_wire"))
		{
			return new PartBasicWire();
		}
		return null;
	}
}