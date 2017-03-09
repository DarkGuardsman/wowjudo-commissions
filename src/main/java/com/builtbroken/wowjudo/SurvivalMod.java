package com.builtbroken.wowjudo;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.wowjudo.content.campfire.BlockCampFire;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import com.builtbroken.wowjudo.content.crafting.BlockCraftingTable;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import com.builtbroken.wowjudo.content.explosive.remote.ItemRemote;
import com.builtbroken.wowjudo.content.explosive.tile.BlockExplosive;
import com.builtbroken.wowjudo.content.explosive.tile.ItemBlockExplosive;
import com.builtbroken.wowjudo.content.explosive.tile.TileEntityExplosive;
import com.builtbroken.wowjudo.content.logs.ItemLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
@cpw.mods.fml.common.Mod(modid = SurvivalMod.DOMAIN, name = "Wowjudo's Survival Mod", version = SurvivalMod.VERSION)
public class SurvivalMod
{
    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final String DOMAIN = "wowjudo";
    public static final String PREFX = DOMAIN + ":";

    /** Information output thing */
    public static final Logger logger = LogManager.getLogger("SBM-GrapplingHook");

    @SidedProxy(clientSide = "com.builtbroken.wowjudo.ClientProxy", serverSide = "com.builtbroken.wowjudo.CommonProxy")
    public static CommonProxy proxy;

    public static BlockExplosive blockExplosive;
    public static BlockCampFire blockCampFire;
    public static BlockCraftingTable blockCraftingTable;

    public static ItemRemote itemExplosiveRemote;
    public static ItemLog itemLog;

    public static CreativeTabs creativeTab;

    @Mod.Instance(DOMAIN)
    public static SurvivalMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        creativeTab = new CreativeTabs("wowjudo")
        {
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem()
            {
                return Item.getItemFromBlock(blockCampFire);
            }
        };

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        blockExplosive = new BlockExplosive();
        GameRegistry.registerBlock(blockExplosive, ItemBlockExplosive.class, "wjExplosive");
        GameRegistry.registerTileEntity(TileEntityExplosive.class, "wjExplosive");

        blockCampFire = new BlockCampFire();
        GameRegistry.registerBlock(blockCampFire, "wjCampFire");
        GameRegistry.registerTileEntity(TileEntityCampfire.class, "wjCampFire");

        blockCraftingTable = new BlockCraftingTable();
        GameRegistry.registerBlock(blockCraftingTable, "wjCraftingTable");
        GameRegistry.registerTileEntity(TileEntityCraftingTable.class, "wjCraftingTable");

        itemExplosiveRemote = new ItemRemote();
        GameRegistry.registerItem(itemExplosiveRemote, "wjExplosiveRemote");

        itemLog = new ItemLog();
        GameRegistry.registerItem(itemLog, "wjLog");
        MinecraftForge.EVENT_BUS.register(itemLog);
        GameRegistry.registerFuelHandler(itemLog);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Register ore dictionary support
        for (ItemLog.LogTypes type : ItemLog.LogTypes.values())
        {
            OreDictionary.registerOre("logWood", new ItemStack(itemLog, 1, type.ordinal()));
            OreDictionary.registerOre("log", new ItemStack(itemLog, 1, type.ordinal()));
            if (type == ItemLog.LogTypes.DARK_OAK)
            {
                OreDictionary.registerOre("logBigOak", new ItemStack(itemLog, 1, type.ordinal()));
            }
            else if (type != ItemLog.LogTypes.GENERIC)
            {
                OreDictionary.registerOre("log" + LanguageUtility.capitalizeFirst(type.localization), new ItemStack(itemLog, 1, type.ordinal()));
            }
        }

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Food
        TileEntityCampfire.addRecipe(Items.porkchop, new ItemStack(Items.cooked_porkchop), 0.35F);
        TileEntityCampfire.addRecipe(Items.beef, new ItemStack(Items.cooked_beef), 0.35F);
        TileEntityCampfire.addRecipe(Items.chicken, new ItemStack(Items.cooked_chicken), 0.35F);
        TileEntityCampfire.addRecipe(Items.potato, new ItemStack(Items.baked_potato), 0.35F);

        ItemFishFood.FishType[] afishtype = ItemFishFood.FishType.values();
        for (int j = 0; j < afishtype.length; ++j)
        {
            ItemFishFood.FishType fishtype = afishtype[j];

            if (fishtype.func_150973_i())
            {
                TileEntityCampfire.addRecipe(new ItemStack(Items.fish, 1, fishtype.func_150976_a()), new ItemStack(Items.cooked_fished, 1, fishtype.func_150976_a()), 0.35F);
            }
        }
        //Dye
        TileEntityCampfire.addRecipe(Blocks.cactus, new ItemStack(Items.dye, 1, 2), 0.2F);

        //Charcoal
        TileEntityCampfire.addRecipe(Blocks.log, new ItemStack(Items.coal, 1, 1), 0.15F);
        TileEntityCampfire.addRecipe(Blocks.log2, new ItemStack(Items.coal, 1, 1), 0.15F);

        //TODO config load recipes

        proxy.postInit();
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        //TODO maybe modify recipes?
    }
}
