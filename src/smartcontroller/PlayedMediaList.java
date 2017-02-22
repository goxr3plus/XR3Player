/*
 * 
 */
package smartcontroller;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The Class PlayedSongs.
 */
public class PlayedMediaList {

    /** The LinkedHashSet */
    Set<String> set = new LinkedHashSet<>(); // order is preserved

    /**
     * Add a new item.
     *
     * @param item
     *            the item
     */
    public void add(String item) {
	set.add(item);
    }

    /**
     * Check if a media has been already played.
     *
     * @param item
     *            the item
     * @return true, if successful
     */
    public boolean contains(String item) {
	return set.contains(item);
    }

    /**
     * Prints all the Songs that had been played.
     */
    public void printPlayedSongs() {
	set.stream().forEach(System.out::println);
    }

    /**
     * Renames the media with this name if exists [ in list ].
     *
     * @param oldName
     *            the old name
     * @param newName
     *            the new name
     * @return true, if successful
     */
    public boolean renameMedia(String oldName, String newName) {
	if (set.remove(oldName))
	    return set.add(newName);

	return false;
    }

    /**
     * Returns the Set.
     *
     * @return the sets the
     */
    public Set<String> getSet() {
	return set;
    }

}
