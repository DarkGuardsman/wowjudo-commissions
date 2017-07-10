package com.builtbroken.wowjudo.content.ex;

import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.prefab.explosive.AbstractExplosiveHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2017.
 */
public class ExDamage extends AbstractExplosiveHandler
{
    public ExDamage()
    {
        super("wowjudo.damage");
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double size, NBTTagCompound tag)
    {
        return new BlastDamage(this).setLocation(world, x, y, z).setYield(size).setCause(triggerCause).setAdditionBlastData(tag);
    }
}
