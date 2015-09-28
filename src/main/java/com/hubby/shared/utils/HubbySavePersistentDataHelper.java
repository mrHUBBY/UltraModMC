package com.hubby.shared.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.ISaveFormat;

import net.minecraftforge.fml.client.FMLClientHandler;

/**
 * This utility class aids in providing a service that allows
 * for arbitrary data to be saved to disk and then loaded at
 * a later time. The saved data will persist across different runs
 * of minecraft.
 * @author davidleistiko
 */
public class HubbySavePersistentDataHelper {

	/**
	 * Exception used to identify the case where save data has been misplaced
	 * and is no longer in the proper location
	 * @author davidleistiko
	 */
	public class SavePersistentDataHelperLoadMismatchException extends Exception {
		public SavePersistentDataHelperLoadMismatchException(String message) {
			super(message);
		}
	}

	/**
	 * Access to the singleton instance of the save helper
	 */
	private static HubbySavePersistentDataHelper _instance = null;
	public static final HubbySavePersistentDataHelper getInstance() {
		if (_instance == null) { 
			_instance = new HubbySavePersistentDataHelper(); 
		}
		return _instance;
	}

	/**
	 * Retrieve the list of all currently saved games
	 * @return List - the list of saved games
	 * @throws AnvilConverterException
	 */
	public List getSavedGameList() throws AnvilConverterException {
		List results = null;
        ISaveFormat isaveformat = Minecraft.getMinecraft().getSaveLoader();
        results = isaveformat.getSaveList();
        Collections.sort(results);
        return results;
	}

	/**
	 * Returns the folder name for the currently loaded save game, or it 
	 * returns the empty string in the case that we do not have
	 * @return String - the name of the save-game folder
	 */
	// an integrated server
	public String getLoadedSaveGameFolderName() {
	    if (Minecraft.getMinecraft().getIntegratedServer() != null) {
	        return Minecraft.getMinecraft().getIntegratedServer().getFolderName();
	    }
	    else if (Minecraft.getMinecraft().mcDataDir != null) {
	        return Minecraft.getMinecraft().mcDataDir.getPath();
	    }
	    return "";
	}

	/**
	 *  Returns the world name of the currently loaded save game. 
	 *  If we do not have the integrated server then by default return the empty string.
	 * @return String - the name of the saved world
	 */
	public String getLoadedSaveGameWorldName() {
	    if (Minecraft.getMinecraft().getIntegratedServer() != null) {
	        return Minecraft.getMinecraft().getIntegratedServer().getWorldName();
	    }
	    else if (Minecraft.getMinecraft().theWorld != null) {
	        return Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName();
	    }
		return "";
	}

	/**
	 * Returns the File object of the actively loaded save game, or 
	 * returns null in the case that we do not have the name of the 
	 * current save game folder
	 * @return File - the directory containing the save game
	 */
	public File getLoadedSaveGameDirectory() {
		if (getLoadedSaveGameFolderName().isEmpty()) {
			return null;
		}
		String dir = FMLClientHandler.instance().getSavesDir().getPath() + File.separator + getLoadedSaveGameFolderName();
		return new File(dir);
	}

	/** 
	 * Attempts to write a tagCompund to a file on disk. if the file does not exist or
	 * we run into some IO problems then return null as well.
	 * @param filename - the filename to save the compound to
	 * @param tagCompound - the data to save
	 * @return boolean - did we successfully write the data?
	 */
	public boolean saveTagCompound(String filename, NBTTagCompound tagCompound) {

		// get the default save dir
		File dir = this.getLoadedSaveGameDirectory();
		if (dir == null) {
			return false;
		}

		// Build filename and attempt to wrtie tagCompound
		File fileToWrite = new File(dir, filename);
		try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite.getPath());

            tagCompound.setString("saveFolderName", getLoadedSaveGameFolderName());

            CompressedStreamTools.writeCompressed(tagCompound, fileOutputStream);
            fileOutputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Attempts to read the file specified by filename, if the file could not found
	 * return null otherwise return the tagCompound read from the file
	 * @param filename - the name of the file to load
	 * @return NBTTagCompound - the loaded compound (null if file does not exist)
	 */
	public NBTTagCompound loadTagCompound(String filename) {

		// get out default save directory
		File dir = this.getLoadedSaveGameDirectory();
		if (dir == null) {
			return null;
		}

		// Build file name to open, return null if the file does not exist
		File fileToRead = new File(dir, filename);
		if (!fileToRead.exists()) {
			return null;
		}

		// Attempt to read tagCompound
		try {
			NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(new FileInputStream(fileToRead));

			if (!tagCompound.getString("saveFolderName").equals(getLoadedSaveGameFolderName())) {
				 throw new SavePersistentDataHelperLoadMismatchException("When attempting to read from " + fileToRead.getPath() +
						 									   ", the NBTTagCompound retrieved listed the save folder name as " +
						 									   tagCompound.getString("saveFolderName") + " instead of the correct and current save folder, " +
						 									   getLoadedSaveGameFolderName() + ".");
			}
			return tagCompound;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Failed...
		return null;
	}
}
