package com.hubby.utils;

/**
 * Class that stores common objects used by the hubby mod lib
 * @author davidleistiko
 */
public class HubbyRegistry {
    /**
     * This will monitor the player's inventory every frame and monitor when a change occurs and will then
     * handle that incident by logging the details of the change so I can see what is going on.
     */
    private static HubbyRefreshedCheckPlayerInventory refreshCheckPlayerInventoryObject = null;
    

    /**
     * Registers all objects here
     */
    public static void register() {
        refreshCheckPlayerInventoryObject = new HubbyRefreshedCheckPlayerInventory("refreshCheckPlayerInventory", HubbyRefreshedObjectInterface.AVERAGE_PRIORITY);
    }
}
