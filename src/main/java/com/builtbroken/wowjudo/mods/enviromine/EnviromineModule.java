package com.builtbroken.wowjudo.mods.enviromine;

import com.builtbroken.mc.framework.mod.loadable.AbstractLoadable;
import enviromine.core.EM_Settings;
import enviromine.trackers.properties.BlockProperties;
import enviromine.utils.EnviroUtils;
import net.minecraft.init.Blocks;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/13/2017.
 */
public class EnviromineModule extends AbstractLoadable
{
    @Override
    public void init()
    {
        super.init();
        for (int meta = 0; meta < 4; meta++)
        {
            BlockProperties blockProp = new BlockProperties(
                    "wjsurvialmod:wjCampFire",
                    meta,
                    false,
                    -1,
                    -1,
                    -1,
                    "wjsurvialmod:wjCampFire",
                    0,
                    1,
                    true,
                    75.0f,
                    -0.25f,
                    0,
                    false,
                    false,
                    false,
                    false,
                    EnviroUtils.getDefaultStabilityType(Blocks.cobblestone).name,
                    null

            );
            EM_Settings.blockProperties.put("wjsurvialmod:wjCampFire," + meta, blockProp);
        }
    }
}
