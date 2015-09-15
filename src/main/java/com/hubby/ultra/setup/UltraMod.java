package com.hubby.ultra.setup;

import com.hubby.shared.utils.ConfigHelper;
import com.hubby.shared.utils.DefaultConfigPropertyListener;
import com.hubby.ultra.UltraCommandHooks;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//import cpw.mods.fml.common.network.NetworkMod; // not used in 1.7

@Mod(modid = "ultramod", name = "The Ultra MOD", version = "1.8")
// @NetworkMod(clientSideRequired=true) // not used in 1.7
public class UltraMod {

	// The instance of your mod that Forge uses.
	@Instance(value = "ultramod")
	public static UltraMod instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "com.hubby.ultra.setup.client.ClientProxy", serverSide = "com.hubby.ultra.setup.CommonProxy")
	public static CommonProxy proxy;
	
	// Access to the information about this MOD
	public static final String MOD_ID = UltraMod.class.getAnnotation(Mod.class).modid();
	public static final String MOD_VERSION = UltraMod.class.getAnnotation(Mod.class).version();
	public static final String MOD_NAME = "ultra";

	@EventHandler
	// used in 1.6.2
	// @PreInit // used in 1.5.2
	public void preInit(FMLPreInitializationEvent event) {

		// Load potions, note this must be done before retrieving the config
		// file for some reason
		//extendPotionClass();

		//extendBlockiquidClass();

		//NitroInterface.setupDefaultKeyBindings();
		
		ConfigHelper helper = ConfigHelper.getInstance();
		helper.addPropertyListener(new DefaultConfigPropertyListener());
		helper.addPropertyListener(new ConfigPropertyListener());
		helper.openConfiguration(event.getSuggestedConfigurationFile(), "1.0");

		// Register custom event hooks
		FMLCommonHandler.instance().bus().register(new UltraCommandHooks());
		//MinecraftForge.EVENT_BUS.register(new NitroEventHooks());
		//MinecraftForge.EVENT_BUS.register(new NitroRenderEntityPlayer());
	}

	@EventHandler
	// used in 1.6.2
	// @Init // used in 1.5.2
    public void load(FMLInitializationEvent event) throws Exception {

		// Register renderers
		proxy.registerRenderers();
		
		UltraRegistry.register();

		//NitroInterface.registerFluids();

		// Register blocks
//		GameRegistry.registerBlock(NitroInterface.nitroBlock, "nitro");
//		GameRegistry.registerBlock(NitroInterface.nitroOre, "nitroOre");
//		GameRegistry.registerBlock(NitroInterface.nitroFireOre, "nitroFireOre");
//		GameRegistry.registerBlock(NitroInterface.nitroBlockChest, NitroItemChest.class, "nitroBlockChest");

		// register items
//		GameRegistry.registerItem(NitroInterface.nitroIngot, "nitroIngot");
//		GameRegistry.registerItem(NitroInterface.nitroFireIngot, "nitroFireIngot");
//		GameRegistry.registerItem(NitroInterface.nitroSword, "nitroSword");
//		GameRegistry.registerItem(NitroInterface.nitroPickaxe, "nitroPickaxe");
//		GameRegistry.registerItem(NitroInterface.nitroArmorHelemet, "nitroHelemt");
//		GameRegistry.registerItem(NitroInterface.nitroArmorChest, "nitroChest");
//		GameRegistry.registerItem(NitroInterface.nitroArmorLegs, "nitroLegs");
//		GameRegistry.registerItem(NitroInterface.nitroArmorBoots, "nitroBoots");
//		GameRegistry.registerItem(NitroInterface.nitroBackpack, "nitroBackpack");
//		GameRegistry.registerItem(NitroInterface.nitroSnorkel, "nitroSnorkel");
//		GameRegistry.registerItem(NitroInterface.nitroNightGoggles, "nitroNightGoggles");
//		GameRegistry.registerItem(NitroInterface.nitroFireArmorHelemet, "nitroFireHelemt");
//		GameRegistry.registerItem(NitroInterface.nitroFireArmorChest, "nitroFireChest");
//		GameRegistry.registerItem(NitroInterface.nitroFireArmorLegs, "nitroFireLegs");
//		GameRegistry.registerItem(NitroInterface.nitroFireArmorBoots, "nitroFireBoots");
//		GameRegistry.registerItem(NitroInterface.nitroGrenade, "nitroGrenade");
//		GameRegistry.registerItem(NitroInterface.nitroAxe, "nitroAxe");
//		GameRegistry.registerItem(NitroInterface.nitroHoe, "nitroHoe");
//		GameRegistry.registerItem(NitroInterface.nitroSpade, "nitroSpade");
//		GameRegistry.registerItem(NitroInterface.nitroGlowStick, "nitroGlowStick");
//		GameRegistry.registerItem(NitroInterface.nitroFireSword, "nitroFireSword");
//		GameRegistry.registerItem(NitroInterface.nitroFireball, "nitroFireball");
//		GameRegistry.registerItem(NitroInterface.nitroFirePickaxe, "nitroFirePickaxe");
//		GameRegistry.registerItem(NitroInterface.nitroFireAxe, "nitroFireAxe");
//		GameRegistry.registerItem(NitroInterface.nitroFireHoe, "nitroFireHoe");
//		GameRegistry.registerItem(NitroInterface.nitroFireSpade, "nitroFireSpade");
//		GameRegistry.registerItem(NitroInterface.nitroTeleporter, "nitroTeleporter");
//        // GameRegistry.registerItem(NitroInterface.nitroItemPotion, "nitroItemPotion");

		// Register world generatprs
//		GameRegistry.registerWorldGenerator(new NitroGenerator(), 0);
//
//		// Register smelting
//        GameRegistry.addSmelting(NitroInterface.nitroOre, new ItemStack(NitroInterface.nitroIngot, 2, 0), NitroInterface.nitroOreSmeltingXp);
//        GameRegistry.addSmelting(NitroInterface.nitroFireOre, new ItemStack(NitroInterface.nitroFireIngot, 2, 0), NitroInterface.nitroOreSmeltingXp);
//
//        ArrayList<ItemStack> backpacks = new ArrayList<ItemStack>();
//        NitroInterface.nitroBackpack.getSubItems(NitroInterface.nitroBackpack, null, backpacks);
//        NitroItemBackpack backpack1 = (NitroItemBackpack) backpacks.get(0).getItem();
//        NitroItemBackpack backpack2 = (NitroItemBackpack) backpacks.get(1).getItem();
//
//		// Register recipes]
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroArmorHelemet, 1), new Object[] { "NNN", "N N", 'N', NitroInterface.nitroIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroArmorChest, 1), new Object[] { "N N", "NNN", "NNN", 'N', NitroInterface.nitroIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroArmorLegs, 1), new Object[] { "NNN", "N N", "N N", 'N', NitroInterface.nitroIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroArmorBoots, 1), new Object[] { "N N", "N N", 'N', NitroInterface.nitroIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireArmorHelemet, 1), new Object[] { "NNN", "N N", 'N', NitroInterface.nitroFireIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireArmorChest, 1), new Object[] { "N N", "NNN", "NNN", 'N', NitroInterface.nitroFireIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireArmorLegs, 1), new Object[] { "NNN", "N N", "N N", 'N', NitroInterface.nitroFireIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireArmorBoots, 1), new Object[] { "N N", "N N", 'N', NitroInterface.nitroFireIngot });
//		GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroSword, 2), new Object[] { "N", "N", "W", 'N', NitroInterface.nitroBlock,'W', Block.blockRegistry.getObjectById(5) });
//		GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroPickaxe, 2), new Object[] { "NNN", " W ", " W ", 'N', NitroInterface.nitroBlock, 'W', Block.blockRegistry.getObjectById(5) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroGrenade, 2), new Object[] { "C", "N", 'C', Item.itemRegistry.getObjectById(263), 'N', NitroInterface.nitroBlock });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroSpade, 2), new Object[] { "N", "S", "S", 'N', NitroInterface.nitroBlock, 'S', Item.itemRegistry.getObjectById(280) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroHoe, 2), new Object[] { "NN", " S", " S", 'N', NitroInterface.nitroBlock, 'S', Item.itemRegistry.getObjectById(280) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroAxe, 2), new Object[] { "NN", "NS", " S", 'N', NitroInterface.nitroBlock, 'S', Item.itemRegistry.getObjectById(280) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroGlowStick, 2), new Object[] { "N", "N", "N", 'N', NitroInterface.nitroIngot });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireSword, 2), new Object[] { "F", "F", "O", 'F', Item.itemRegistry.getObjectById(327), 'O', Block.blockRegistry.getObjectById(49) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroFireball, 5), new Object[] { "FF", "FF", 'F', Item.itemRegistry.getObjectById(327) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroBackpack, 1), new Object[] { "LLL", "WLW", "SWS", 'L', Item.itemRegistry.getObjectById(334), 'W', Block.blockRegistry.getObjectById(35), 'S', Item.itemRegistry.getObjectById(287) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroBackpack, 1, 1), new Object[] { "LLL", "WBW", "LLL", 'L', Item.itemRegistry.getObjectById(334), 'B', NitroInterface.nitroBackpack, 'W', Block.blockRegistry.getObjectById(35) });
//        GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroBackpack, 1, 2), new Object[] { "LLL", "WBW", "LLL", 'L', Item.itemRegistry.getObjectById(334), 'B', new ItemStack(NitroInterface.nitroBackpack, 1, 1), 'W', Block.blockRegistry.getObjectById(35) });
//
//        // Add recipe for nitro teleporter
//        ArrayList<ItemStack> potions = (ArrayList<ItemStack>) NitroInterface.getNitroItemPotions(true);
//        for (int i = 0; i < potions.size(); ++i) {
//            GameRegistry.addRecipe(new ItemStack(NitroInterface.nitroTeleporter, 1), new Object[] { "NNN", "NPN", "NNN", 'N', NitroInterface.nitroBlock, 'P', potions.get(i)});
//        }
//
//		// Register key bindings
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyRain));
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyLights));
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyTime));
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyOptions));
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyEffect));
//		ClientRegistry.registerKeyBinding((KeyBinding) NitroInterface.nitroKeyBindings.get(NitroInterface.nitroKeyBackpack));
//
//		// Register NitroEntityFireball
//        EntityRegistry.registerModEntity(NitroEntityFireball.class, "nitroFireball", NitroInterface.getUniqueID(), this, 160, 1, false);
//
//		// Register NitroEntityGrenade
//        EntityRegistry.registerModEntity(NitroEntityGrenade.class, "nitroGrenade", NitroInterface.getUniqueID(), this, 160, 1, false);
//
//		// Register NitroEntityDestroyer
//        EntityRegistry.registerGlobalEntityID(NitroEntityDestroyer.class, "nitroEntityDestroyer", NitroInterface.getUniqueID(), 3515848, 12102);
//        EntityRegistry.registerModEntity(NitroEntityDestroyer.class, "nitroEntityDestroyer", NitroInterface.getUniqueID(), this, 80, 3, true);
//        EntityRegistry.addSpawn(NitroEntityDestroyer.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.plains);
//
//		// Register NitroEntityDeathPhantom
//        EntityRegistry.registerGlobalEntityID(NitroEntityDeathPhantom.class, "nitroEntityDeathPhantom", NitroInterface.getUniqueID(), 0x000000, 0xFFFFFF);
//        EntityRegistry.registerModEntity(NitroEntityDeathPhantom.class, "nitroEntityDeathPhantom", NitroInterface.getUniqueID(), this, 80, 3, true);
//        EntityRegistry.addSpawn(NitroEntityDeathPhantom.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.forest);
//        EntityRegistry.addSpawn(NitroEntityDeathPhantom.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.plains);
//        EntityRegistry.addSpawn(NitroEntityDeathPhantom.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.beach);
//        EntityRegistry.addSpawn(NitroEntityDeathPhantom.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.jungle);
//
//		// Register NitroEntityZombie
//        EntityRegistry.registerGlobalEntityID(NitroEntityZombie.class, "nitroEntityZombie", NitroInterface.getUniqueID(), 0xFF0000, 0xFFFFFF);
//        EntityRegistry.registerModEntity(NitroEntityZombie.class, "nitroEntityZombie", NitroInterface.getUniqueID(), this, 80, 3, true);
//        EntityRegistry.addSpawn(NitroEntityZombie.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.forest);
//        EntityRegistry.addSpawn(NitroEntityZombie.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.plains);
//        EntityRegistry.addSpawn(NitroEntityZombie.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.beach);
//        EntityRegistry.addSpawn(NitroEntityZombie.class, 5, 2, 6, EnumCreatureType.monster, BiomeGenBase.jungle);
//
//		// Register TileEntities
//        GameRegistry.registerTileEntity(NitroBlockTileEntity.class, NitroInterface.nitroBlockTileEntityID);
//        ClientRegistry.registerTileEntity(NitroChestTileEntity.class, NitroInterface.nitroChestTileEntityID, new NitroChestTileEntityRenderer());
//
//		// Register our gui's/network
//		NetworkRegistry.INSTANCE.registerGuiHandler(this, this.proxy);
//        NitroInterface.networkChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(NitroInterface.networkChannelName);
//
//        // Load any other dependencies
//        NitroInterface.load();
//
		proxy.registerPacketHandler();
	}

	@EventHandler
	// used in 1.6.2
	// @PostInit // used in 1.5.2
	public void postInit(FMLPostInitializationEvent event) {
	}

	// Make the getFlowDirection func public
//	public void extendBlockiquidClass() {
//		try {
//            Method m = BlockLiquid.class.getDeclaredMethod("getFlowDirection", IBlockAccess.class, int.class, int.class, int.class, Material.class);
//			m.setAccessible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	// Extends the potion class so that we can create new potions
//	public void extendPotionClass() {
//
//		Potion[] potionTypes = null;
//
//		// Attempt to make more potion slots
//		try {
//			for (Field f : Potion.class.getDeclaredFields()) {
//				f.setAccessible(true);
//
//                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
//					Field modfield = Field.class.getDeclaredField("modifiers");
//					modfield.setAccessible(true);
//					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
//
//					potionTypes = (Potion[]) f.get(null);
//					final Potion[] newPotionTypes = new Potion[256];
//                    System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
//					f.set(null, newPotionTypes);
//				}
//			}
//        }
//        catch (Exception e) {
//		}
//
//		// Iterate over all nitro potions and set requirements/amplifiers
//		for (int i = 0; i < NitroInterface.nitroPotions.size(); ++i) {
//			Potion potion = (Potion) NitroInterface.nitroPotions.get(i);
//            String potionRequirement = (String) NitroInterface.nitroPotionRequirements.get(i);
//            String potionAmplifier = (String) NitroInterface.nitroPotionAmplifiers.get(i);
//			int potionId = potion.getId();
//
//			// We need to get potionRequirements/potionAmplifiers to modify
//			Field f2 = null;
//			Field f3 = null;
//			Field modifiersField = null;
//
//			try {
//				modifiersField = Field.class.getDeclaredField("modifiers");
//				modifiersField.setAccessible(true);
//            }
//            catch (Exception e) {
//				continue;
//			}
//
//			// Search for the fields using reflection
//			for (Field f : PotionHelper.class.getDeclaredFields()) {
//				f.setAccessible(true);
//
//				// Handle potion requirements
//                if (f.getName().equals("field_77927_l") || f.getName().equals("potionRequirements")) {
//					f2 = f;
//					f2.setAccessible(true);
//					try {
//                        modifiersField.setInt(f2, f2.getModifiers() & ~Modifier.FINAL);
//                    }
//                    catch (Exception e) {
//					}
//                }
//                // Handle potion amplifiers
//                else if (f.getName().equals("field_77928_m") || f.getName().equals("potionAmplifiers")) {
//					f3 = f;
//					f3.setAccessible(true);
//					try {
//                        modifiersField.setInt(f3, f3.getModifiers() & ~Modifier.FINAL);
//                    }
//                    catch (Exception e) {
//					}
//				}
//			}
//
//			// Actually modify the requirements
//			HashMap myPotionRequirements = null;
//			try {
//				myPotionRequirements = (HashMap) f2.get(null);
//                myPotionRequirements.put(Integer.valueOf(potionId), potionRequirement);
//				f2.set(null, myPotionRequirements);
//            }
//            catch (Exception e) {
//			}
//
//			// Actually modify the amplifiers
//			HashMap myPotionAmplifiers = null;
//			try {
//				myPotionAmplifiers = (HashMap) f3.get(null);
//                myPotionAmplifiers.put(Integer.valueOf(potionId), potionAmplifier);
//				f3.set(null, myPotionAmplifiers);
//            }
//            catch (Exception e) {
//			}
//		}
//	}
}