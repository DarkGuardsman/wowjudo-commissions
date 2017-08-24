package com.builtbroken.wowjudo.content.ex;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2017.
 */
public class BlastDamage extends BlastSimplePath<BlastDamage>
{
    public final float energyPerMeter = 100;

    public BlastDamage(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public IWorldEdit changeBlock(BlockPos location)
    {
        return new BlockEdit(oldWorld(), xi(), yi(), zi());
    }

    @Override
    public float getEnergy(BlockPos location, double distance)
    {
        double start = size * energyPerMeter;
        return (float) (start - location.distance(blockCenter) * energyPerMeter);
    }
}
