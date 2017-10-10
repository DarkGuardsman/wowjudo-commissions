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
    private int speedIncrease = 10;
    private int meleeDamage = 0;
    private int foodAmount = 0;
    private int armorIncrease = 0;
    private int airIncrease = 0;

    AttributeModifier healthAttribute;
    AttributeModifier speedAttribute;

    public EntityPlayer entity;

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound save = new NBTTagCompound();

        save.setInteger(NBT_HP, getHpIncrease());


        compound.setTag(StatHandler.PROPERTY_ID, save);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if (compound.hasKey(StatHandler.PROPERTY_ID, 10))
        {
            NBTTagCompound save = compound.getCompoundTag(StatHandler.PROPERTY_ID);
            setHpIncrease(save.getInteger(NBT_HP));
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
        if (entity != null)
        {
            //Handle chance of more points being allocated then possible
            if (getPointsUsed() > getMaxPointUsed())
            {
                reset();
            }

            //Update attributes
            if (hasChanged)
            {
                hasChanged = false;

                //Clear
                HashMultimap map = HashMultimap.create();
                if (healthAttribute != null)
                {
                    map.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), healthAttribute);
                }
                if (speedAttribute != null)
                {
                    map.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), speedAttribute);
                }
                entity.getAttributeMap().removeAttributeModifiers(map);

                //Create
                healthAttribute = new AttributeModifier("Max Health Stat Modifier", getHpIncrease() * StatHandler.HEALTH_SCALE, 0);
                speedAttribute = new AttributeModifier("Speed Stat Modifier", getSpeedIncrease() * StatHandler.SPEED_SCALE, 0);

                //Apply
                map.clear();
                map.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), healthAttribute);
                map.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), speedAttribute);
                entity.getAttributeMap().applyAttributeModifiers(map);
            }
        }
    }

    public void reset()
    {
        setHpIncrease(0);
        setSpeedIncrease(0);
        setMeleeDamage(0);
        setFoodAmount(0);
        setArmorIncrease(0);
        setAirIncrease(0);
        hasChanged = true;
    }

    public int getPointsUsed()
    {
        int points = 0;

        return points;
    }

    public int getMaxPointUsed()
    {
        return entity.experienceLevel;
    }

    public int getHpIncrease()
    {
        return hpIncrease;
    }

    public void setHpIncrease(int value)
    {
        if (value != hpIncrease && value >= 0)
        {
            this.hpIncrease = value > StatHandler.HEALTH_MAX ? StatHandler.HEALTH_MAX : value;
            hasChanged = true;
        }
    }

    public int getSpeedIncrease()
    {
        return speedIncrease;
    }

    public void setSpeedIncrease(int value)
    {
        if (value != speedIncrease && value >= 0)
        {
            this.speedIncrease = value > StatHandler.SPEED_MAX ? StatHandler.SPEED_MAX : value;
            hasChanged = true;
        }
    }

    public int getMeleeDamage()
    {
        return meleeDamage;
    }

    public void setMeleeDamage(int value)
    {
        if (value != meleeDamage && value >= 0)
        {
            this.meleeDamage = value;
            hasChanged = true;
        }
    }

    public int getFoodAmount()
    {
        return foodAmount;
    }

    public void setFoodAmount(int value)
    {
        if (value != foodAmount && value >= 0)
        {
            this.foodAmount = value;
            hasChanged = true;
        }
    }

    public int getArmorIncrease()
    {
        return armorIncrease;
    }

    public void setArmorIncrease(int value)
    {
        if (value != armorIncrease && value >= 0)
        {
            this.armorIncrease = value;
            hasChanged = true;
        }
    }

    public int getAirIncrease()
    {
        return airIncrease;
    }

    public void setAirIncrease(int value)
    {
        if (value != airIncrease && value >= 0)
        {
            this.airIncrease = value;
            hasChanged = true;
        }
    }
}
