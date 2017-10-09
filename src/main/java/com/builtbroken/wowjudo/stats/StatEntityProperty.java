package com.builtbroken.wowjudo.stats;

import com.google.common.collect.HashMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2017.
 */
public class StatEntityProperty implements IExtendedEntityProperties
{
    public static final String NBT_HP = "hp";

    public boolean hasChanged = true;

    private int hpIncrease = 10;
    private int speedIncrease = 0;
    private int meleeDamage = 0;
    private int foodAmount = 0;
    private int armorIncrease = 0;
    private boolean waterBreathing = false;

    AttributeModifier healthAttribute;

    public EntityPlayer entity;

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound save = new NBTTagCompound();

        save.setInteger(NBT_HP, hpIncrease);


        compound.setTag(StatHandler.PROPERTY_ID, save);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if (compound.hasKey(StatHandler.PROPERTY_ID, 10))
        {
            NBTTagCompound save = compound.getCompoundTag(StatHandler.PROPERTY_ID);
            hpIncrease = save.getInteger(NBT_HP);
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        if (entity instanceof EntityPlayer)
        {
            this.entity = (EntityPlayer) entity;
        }
    }

    public void update()
    {
        if (hasChanged && entity != null)
        {
            hasChanged = false;

            /**  See {@link net.minecraft.entity.EntityLivingBase#onUpdate()} for usage on attributes*/

            //Clear
            if (healthAttribute != null)
            {
                //TODO remove
                HashMultimap map = HashMultimap.create();
                map.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), healthAttribute);
                entity.getAttributeMap().removeAttributeModifiers(map);
            }

            //Create
            healthAttribute = new AttributeModifier("Max Health Stat Modifier", hpIncrease, 0);

            //Apply
            HashMultimap map = HashMultimap.create();
            map.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), healthAttribute);
            entity.getAttributeMap().applyAttributeModifiers(map);
        }
    }

    public void setHpIncrease(int value)
    {
        if (value != hpIncrease && value >= 0)
        {
            this.hpIncrease = value;
            hasChanged = true;
        }
    }
}
