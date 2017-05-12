package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.api.explosive.IBlast;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.explosive.IExplosiveDamageable;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.util.EnumFacing;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/12/2017.
 */
public class TileNodeWall extends TileNode implements IExplosiveDamageable
{
    public static float energyCostPerHP = 10;
    int hp = -1;

    public TileNodeWall()
    {
        super("blast.wall", SurvivalMod.DOMAIN);
    }

    public int getHp()
    {
        if (hp == -1)
        {
            hp = 20;
        }
        return hp;
    }

    @Override
    public float getEnergyCostOfTile(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        float energyCost = getHp() * energyCostPerHP;
        hp -= Math.floor(Math.max(1, energy / energyCostPerHP));
        return energyCost;
    }

    @Override
    public IBlastEdit getBlockEditOnBlastImpact(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        if (hp > 0)
        {
            return null;
        }
        return new BlockEdit(world(), xi(), yi(), zi());
    }
}
