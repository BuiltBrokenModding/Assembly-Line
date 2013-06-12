package dark.core.faction.entity;

public enum CharStats
{
	Strength("Strength"), endurance("endurance"), dexterity("dexterity"), Intelligence("intelligence"), wit("wits"), memor("recall"), willpower("Will"), perception("charm"), luck("Luck");
	
	public String name;
	
	private CharStats(String name)
	{
		this.name = name;
	}
}
