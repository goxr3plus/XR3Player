/*
 * 
 */
package smartcontroller;

import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class PlayedSongs.
 */
public class PlayedSongs {

	/** The set. */
	Set<String> set = new LinkedHashSet<String>(); // order is preserved

	/**
	 * Add a new item.
	 *
	 * @param item the item
	 */
	public void add(String item) {
		set.add(item);
	}

	/**
	 * Check if a song has been already played.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean contains(String item) {
		return set.contains(item);
	}

	/**
	 * Prints all the Songs that had been played.
	 */
	public void printPlayedSongs() {
		set.stream().forEach(s -> System.out.println(s));
	}

	/**
	 * Renames the song with this name if exists in list.
	 *
	 * @param oldName the old name
	 * @param newName the new name
	 * @return true, if successful
	 */
	public boolean renameSong(String oldName, String newName) {
		if (set.remove(oldName))
			return set.add(newName);

		return false;
	}
	
	/**
	 * Returns the Set.
	 *
	 * @return the sets the
	 */
	public Set<String> getSet(){
		return set;
	}

}
