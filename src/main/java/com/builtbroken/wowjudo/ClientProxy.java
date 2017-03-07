package com.builtbroken.wowjudo;

import com.builtbroken.wowjudo.content.explosive.tile.ISBRExplosive;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new ISBRExplosive());
    }
}
