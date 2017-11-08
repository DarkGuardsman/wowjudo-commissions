package com.builtbroken.wowjudo;

import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import com.builtbroken.wowjudo.content.campfire.TileEntityRenderCampFire;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import com.builtbroken.wowjudo.content.crafting.TileEntityRenderCraftingTable;
import com.builtbroken.wowjudo.content.explosive.tile.ISBRExplosive;
import com.builtbroken.wowjudo.content.furnace.ISBRDualFurnace;
import com.builtbroken.wowjudo.content.furnace.TESRDualFurnace;
import com.builtbroken.wowjudo.content.furnace.TileWrapperDualFurnace;
import com.builtbroken.wowjudo.content.wall.ISBRWall;
import com.builtbroken.wowjudo.stats.gui.GuiStats;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ClientProxy extends CommonProxy
{
    KeyBinding openGuiKey;

    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new ISBRExplosive());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCampfire.class, new TileEntityRenderCampFire());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTable.class, new TileEntityRenderCraftingTable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWrapperDualFurnace.class, new TESRDualFurnace());

        openGuiKey = new KeyBinding("key." + SurvivalMod.DOMAIN + ":gui.stats.open", Keyboard.KEY_Y, "itemGroup." + SurvivalMod.DOMAIN);
        ClientRegistry.registerKeyBinding(openGuiKey);

        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void loadJsonContentHandlers()
    {
        RenderingRegistry.registerBlockHandler(new ISBRDualFurnace());
        ClientDataHandler.INSTANCE.addBlockRenderer(SurvivalMod.DOMAIN + ":wall", new ISBRWall());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) //Prevent key bind from working on loading screen and main menu
        {
            World world = mc.theWorld;
            EntityClientPlayerMP player = mc.thePlayer;

            if (world != null && player != null && openGuiKey.isPressed())
            {
                GuiScreen gui = mc.currentScreen;
                if (!(gui instanceof GuiStats))
                {
                    if (gui != null)
                    {
                        gui.onGuiClosed();
                    }
                    Minecraft.getMinecraft().displayGuiScreen(new GuiStats(player));
                }
            }
        }
    }
}
