package iieLoadSaveEntireWorld;

import org.bukkit.Location;

public class ChunkLoaderAPI {
	/**This method loads a chunk if the chunk isn't loaded already.
	 * @param locationToLoad
	 * @return True if chunk was already loaded, False if chunk wasn't already loaded
	 */
	public static boolean loadChunk(Location locationToLoad){
		if(!(locationToLoad.getBlock().getChunk().isLoaded())){
			locationToLoad.getBlock().getChunk().load();
			return true;
		}
		return false;
	}
	

	/**This method loads a chunk if the chunk isn't loaded already.
	 * @param locationToLoad
	 * @return True if chunk was already unloaded, False if chunk wasn't already unloaded
	 */
	public static boolean unloadChunk(Location locationToUnload){
		if (locationToUnload.getBlock().getChunk().isLoaded()){
			locationToUnload.getBlock().getChunk().unload();
			return false;
		}
		return true;
	}

}
