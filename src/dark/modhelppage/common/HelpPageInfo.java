package dark.modhelppage.common;

import cpw.mods.fml.common.registry.ItemData;

public class HelpPageInfo
{
    private final ItemData data;
    private String name;
    private String body;

    public HelpPageInfo(ItemData data)
    {
        this.data = data;
    }
}
