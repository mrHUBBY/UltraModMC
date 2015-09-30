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
    public static HubbyRefreshedCheckPlayerInventory REFRESH_CHECK_PLAYER_INVENTORY = new HubbyRefreshedCheckPlayerInventory("refreshCheckPlayerInventory", HubbyRefreshedObjectInterface.AVERAGE_PRIORITY);;
    
    /**
     * Registers all objects here
     */
    public static void register() {
    }
}
