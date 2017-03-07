package com.builtbroken.wowjudo;

import com.builtbroken.wowjudo.content.explosive.remote.ItemRemote;
import com.builtbroken.wowjudo.content.explosive.tile.BlockExplosive;
import com.builtbroken.wowjudo.content.explosive.tile.TileEntityExplosive;
import com.builtbroken.wowjudo.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
@cpw.mods.fml.common.Mod(modid = JudoMod.DOMAIN, name = "Wowjudo's Mod", version = JudoMod.VERSION)
public class JudoMod
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

    public static PacketManager packetHandler;

    @SidedProxy(clientSide = "com.builtbroken.wowjudo.ClientProxy", serverSide = "com.builtbroken.wowjudo.CommonProxy")
    public static CommonProxy proxy;

    public static Block blockExplosive;

    public static Item itemExplosiveRemote;

    public static CreativeTabs creativeTab;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        creativeTab = new CreativeTabs("wowjudo")
        {
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem()
            {
                return itemExplosiveRemote;
            }
        };

        blockExplosive = new BlockExplosive();
        GameRegistry.registerBlock(blockExplosive, "wjExplosive");
        GameRegistry.registerTileEntity(TileEntityExplosive.class, "wjExplosive");

        itemExplosiveRemote = new ItemRemote();
        GameRegistry.registerItem(itemExplosiveRemote, "wjExplosiveRemote");


        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        packetHandler = new PacketManager("wowjudo");
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
