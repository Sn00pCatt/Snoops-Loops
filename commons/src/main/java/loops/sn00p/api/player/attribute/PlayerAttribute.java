package loops.sn00p.api.player.attribute;

import loops.sn00p.api.player.save.SaveManager;

import java.util.UUID;

/**
 *
 * An interface designed for storing specific data for each mod / plugin about a player.
 *
 * All implementations should stick to only keeping functions visible that operate on this object (i.e. no public getters
 * or setters) to follow SOLID rules.
 *
 * @param <A> The manager class
 */
public interface PlayerAttribute<A> {

    /**
     *
     * Gets the attribute owner's UUID
     *
     * @return The owner's UUID
     */
    UUID getUuid();

    /**
     *
     * Implementation for loading the data for the player attribute if no
     * {@link SaveManager} is set
     *
     */
    void load();

    /**
     *
     * Implementation for saving the data from the player attribute if no
     * {@link SaveManager} is set
     *
     */
    void save();

    /**
     *
     * Called before a {@link SaveManager} saves this class
     *
     */
    default void preSave() {}

    /**
     *
     * Called after a {@link SaveManager} saves this class
     *
     */
    default void postSave() {}

    /**
     *
     * Called before a {@link SaveManager} loads this class
     *
     */
    default void preLoad() {}

    /**
     *
     * Called after a {@link SaveManager} loads this class
     *
     */
    default void postLoad() {}

}
