package com.builtbroken.wowjudo.content.explosive.remote;

import com.builtbroken.wowjudo.JudoMod;
import net.minecraft.item.Item;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ItemRemote extends Item
{
    public ItemRemote()
    {
        setUnlocalizedName(JudoMod.PREFX + "explosiveRemote");
        setTextureName(JudoMod.PREFX + "C$_remote");
        setCreativeTab(JudoMod.creativeTab);
    }
}
