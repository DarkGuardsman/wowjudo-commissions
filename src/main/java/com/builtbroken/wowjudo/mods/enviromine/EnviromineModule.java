package com.builtbroken.wowjudo.mods.enviromine;

import com.builtbroken.mc.framework.mod.loadable.AbstractLoadable;
import enviromine.core.EM_Settings;
import enviromine.trackers.properties.BlockProperties;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/13/2017.
 */
public class EnviromineModule extends AbstractLoadable
{
    public static final float defaultFireTemp = 75.0f;

    @Override
    public void init()
    {
        super.init();
        //On state for block
        for (int meta = 4; meta < 8; meta++)
        {
            addNewBlockProperty("wjsurvialmod:wjFurnace", meta, defaultFireTemp);
        }
        addNewBlockProperty("wjsurvialmod:wjCampFire", 1, defaultFireTemp);

    }

    protected void addNewBlockProperty(String name, int meta, float temp)
    {
        BlockProperties blockProp = new BlockProperties(
                name,
                meta,
                false,
                -1,
                -1,
                -1,
                name,
                0,
                1,
                true,
                temp,
                -0.25f,
                0,
                false,
                false,
                false,
                false,
                "none",
                null

        );
        EM_Settings.blockProperties.put(name + "," + meta, blockProp);
    }
}
