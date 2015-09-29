package com.hubby.ultra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyNamedObjectInterface;
import com.hubby.utils.HubbySavePersistentDataHelper;
import com.hubby.utils.HubbyColor.ColorMode;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;

/**
 * This class holds the logic on how waypoints are to function
 * and what their structure should look like
 * @author davidleistiko
 */
public class UltraTeleportWaypoint {

	/**
	 * Members
	 */
    private BlockPos _pos;
	private int _id;
	private HubbyColor _color;
	private float _rotationY;
	private float _rotationX;
	private String _name;
	
	private static boolean _loaded = false;
	private static boolean _hasChanges = false;
	private static int _idCounter = 1;

	/**
	 * Constants
	 */
	private static final ArrayList<UltraTeleportWaypoint> WAYPOINTS = new ArrayList<UltraTeleportWaypoint>();
	public static final String SAVE_FILENAME = "nitroTeleportData.dat";

	/**
	 * Constructor
	 * @param name - the name of the waypoint to save
	 * @param color - the color value used as an identifier
	 * @param pos - the position for the waypoint
	 * @param yaw - the y rotation of the player at the waypoint
	 * @param pitch - the x rotation of the player at the waypoint
	 */
	public UltraTeleportWaypoint(String name, int color, BlockPos pos, float rotationX, float rotationY) {
		_pos = pos;
		_rotationY = rotationX;
		_rotationX = rotationY;
		_id = ++_idCounter;
		_color = new HubbyColor(color, ColorMode.MINECRAFT);
		_name = name;
		_hasChanges = true;
		
		// add the new waypoint
		WAYPOINTS.add(this);
	}

	/**
	 * Default constructor
	 */
	public UltraTeleportWaypoint() {
		this(HubbyNamedObjectInterface.MISSING_NAME, 0xFFFFFFFF, new BlockPos(0.0d, 0.0d, 0.0d), 0.0f, 0.0f);
	}

	/**
	 * Returns an immutable list containing all of the waypoints
	 * @return List - the list of all waypoints
	 */
	public static ImmutableList<UltraTeleportWaypoint> getWaypoints() {
		return ImmutableList.copyOf(WAYPOINTS);
	}
	
	/**
	 * Save any changes that may have occurred while this gui was open
	 */
	public static void save() {
        NBTTagCompound compoundToSave = UltraTeleportWaypoint.writeToNBT();
        HubbySavePersistentDataHelper.getInstance().saveTagCompound(UltraTeleportWaypoint.SAVE_FILENAME, compoundToSave);
	}
	
	/**
	 * Loads the teleport waypoints from the saved filename
	 */
	public static void load() {
	    NBTTagCompound compound = HubbySavePersistentDataHelper.getInstance().loadTagCompound(UltraTeleportWaypoint.SAVE_FILENAME);
	    UltraTeleportWaypoint.readFromNBT(compound);
	}
	
	/**
	 * Removes all waypoints that have a matching name to that of the
	 * name of that is passed into this function
	 * @param waypointName - the name to match against
	 * @return
	 */
	public static Integer removeWaypointByName(final String waypointName) {
	    List<UltraTeleportWaypoint> matches = UltraTeleportWaypoint.search(waypointName);
	    Iterator<UltraTeleportWaypoint> it = matches.iterator();
	    while (it.hasNext()) {
	        WAYPOINTS.remove(it.next());
	    }
	    
	    save();
	    return matches.size();
	}
	
	/**
	 * Remove all waypoints by name that are in the contained list
	 * @param waypoints - the names of the waypoints to remove
	 * @return Integer - the number of waypoints actually removed
	 */
	public static Integer removeWaypoints(List<String> waypoints) {
	    Integer count = 0;
	    Iterator<String> it = waypoints.iterator();
	    while (it.hasNext()) {
	        count += removeWaypointByName(it.next());
	    }
	    
	    save();
	    return count;
	}
	
	/*
	 * Removes all current waypoints
	 */
	public static Integer removeAllWaypoints() {
	    Integer count = WAYPOINTS.size();
	    WAYPOINTS.clear();
	    save();
	    return count;
	}
	
	/**
	 * Checks if we have a waypoint that exists with the name provided
	 * @param name - the name to search for
	 * @return boolean - does the name exist in the list
	 */
	public static boolean containsWaypoint(String name) {
	    List<UltraTeleportWaypoint> matches = UltraTeleportWaypoint.search(name);
	    return matches.size() > 0;
	}

	/**
	 * Checks if we have been loaded yet?
	 * @return boolean - are we loaded?
	 */
	public static boolean isLoaded() {
		return _loaded;
	}

	/**
	 * This lets us know if we need to update/refresh to account for new or
	 * removed waypoints
	 * @return boolean - do we have changes?
	 */
	public static boolean hasChanges() {
		return _hasChanges;
	}

	/**
	 * Return the color identifier for the waypoints
	 * @return int - the compacted color value
	 */
	public int getColor() {
		return (int) _color.getPackedColor(ColorMode.MINECRAFT);
	}
	
	/**
	 * Returns the position of the waypoint
	 * @return BlockPos - the position of the waypoint
	 */
	public BlockPos getPos() {
		return _pos;
	}

	/**
	 * Returns the rotation around the x-axis
	 * @return float - the x rotation
	 */
	public float getRotationX() {
		return _rotationX;
	}

	/**
	 * Returns the rotation around the y-axis
	 * @return float - the y rotation
	 */
	public float getRotationY() {
		return _rotationY;
	}

	/**
	 * Returns the waypoint name
	 * @return
	 */
	public String getWaypointName() {
		return _name;
	}

	/**
	 * Returns the total number of saved waypoints
	 * @return int - the number of waypoints
	 */
	public static int getWaypointCount() {
		return WAYPOINTS.size();
	}

	/**
	 * Generate the save data that will store all
	 * of the relevant information about our current
	 * list of waypoints
	 * @return NBTTagCompound - the save data
	 */
	public static NBTTagCompound writeToNBT() {

		_hasChanges = false;

		NBTTagList tagList = new NBTTagList();
		for (UltraTeleportWaypoint waypoint : WAYPOINTS) {
			NBTTagCompound waypointCompound = new NBTTagCompound();
			waypoint.writeWaypointData(waypointCompound);
			tagList.appendTag(waypointCompound);
		}
		
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setTag("waypoints", tagList);
		return tagCompound;
	}

	/**
	 * Generate our list of waypoints from the saved data passed in
	 * @param tagCompound - the saved data
	 */
	public static void readFromNBT(NBTTagCompound tagCompound) {

		// If we have some changes then we should probably save those now
		// before they get overwritten when this saved data is loaded
		if (_hasChanges) {
			HubbySavePersistentDataHelper.getInstance().saveTagCompound(
					UltraTeleportWaypoint.SAVE_FILENAME,
					UltraTeleportWaypoint.writeToNBT());
		}

		// we have nothing to do here...
		if (tagCompound == null) {
			return;
		}

		WAYPOINTS.clear();

		_hasChanges = false;
		_loaded = true;

		// read in the waypoint data
		NBTTagList tagList = tagCompound.getTagList("waypoints", 10);
		if (tagList != null) {
			for (int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound waypointCompound = tagList.getCompoundTagAt(i);
				
				// NOTE:
				// A side effect of calling the UltraTeleportWaypoint constructor
				// is that it will automatically get registered with the WAYPOINTS
				// list
				UltraTeleportWaypoint waypoint = new UltraTeleportWaypoint();
				waypoint.readWaypointData(waypointCompound);
			}
		}
	}

	/**
	 * Marks the waypoint data as needing to be re-saved
	 */
	public static void notufyHasChanges() {
		_hasChanges = true;
	}

	/**
	 * Define the data that we add to the NBTTagComnpound
	 * so that we can serialize
	 * @param tagCompound
	 */
	private void writeWaypointData(NBTTagCompound tagCompound) {
		tagCompound.setInteger("id", _id);
		tagCompound.setFloat("rotationX", _rotationX);
		tagCompound.setFloat("rotationY", _rotationY);
		tagCompound.setString("name", _name);
		tagCompound.setDouble("posX", _pos.getX());
		tagCompound.setDouble("posY", _pos.getY());
		tagCompound.setDouble("posZ", _pos.getZ());
		tagCompound.setInteger("color", getColor());
	}

	/**
	 * Reads the saved waypoint data from the NBTTagCompound
	 * @param tagCompound - the compound to read the save data from
	 */
	private void readWaypointData(NBTTagCompound tagCompound) {
		
		double posX = tagCompound.getDouble("posX");
		double posY = tagCompound.getDouble("posY");
		double posZ = tagCompound.getDouble("posZ");
		
		_pos = new BlockPos(posX, posY, posZ);
		_id = tagCompound.getInteger("id");
		_name = tagCompound.getString("name");
		_rotationX = tagCompound.getFloat("rotationX");
		_rotationY = tagCompound.getFloat("rotationY");
		_color = new HubbyColor(tagCompound.getInteger("color"), ColorMode.MINECRAFT);
	}
	
	/**
	 * Searches the waypoints, looking for any with the name provided
	 * @param name - the name to filter the results by
	 * @return
	 */
	private static List<UltraTeleportWaypoint> search(final String name) {
        final Predicate<UltraTeleportWaypoint> predicate = new Predicate<UltraTeleportWaypoint>() {
            @Override
            public boolean apply(UltraTeleportWaypoint waypoint) {
                return name.equals(waypoint._name);
            }
         };
         Iterable<UltraTeleportWaypoint> iter = Iterables.filter(WAYPOINTS, predicate);
         return Lists.newArrayList(iter.iterator());
	}
}
