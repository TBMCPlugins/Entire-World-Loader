package iieLoadSaveEntireWorld;

public class ConfigManager implements Runnable {
	
	static boolean isNew(String name)
	{
		return !Main.config.contains("unfinished." + name);
	}
	static void addNew(String name, int width, int[] lowerleft, int[] center)
	{
		String path = "unfinished." + name + ".";
		Main.config.set(path + "width", width);
		Main.config.set(path + "currentRegion.x", center[0]);
		Main.config.set(path + "currentRegion.z", center[1]);
		Main.config.set(path + "lowerleft.x", lowerleft[0]);
		Main.config.set(path + "lowerleft.z", lowerleft[1]);
		Main.config.set(path + "n", 1);
		Main.config.set(path + "c", 1);
		Main.config.set(path + "D", 1);
		Main.config.set(path + "d", 0);
		Main.config.set(path + "B", 0);
	}
	static void resume(String name)
	{
		
	}
	static void saveProgress()
	{
		if (LoadSaveProcess.inProgress)
		{
			String path = "unfinished." + LoadSaveProcess.process.worldname + ".";
			
		}
		
	}
	static void finish()
	{
		
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
