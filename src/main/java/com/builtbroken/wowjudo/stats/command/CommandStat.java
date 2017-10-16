package com.builtbroken.wowjudo.stats.command;

import com.builtbroken.mc.core.commands.prefab.ModularCommand;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/16/2017.
 */
public class CommandStat extends ModularCommand
{
    public CommandStat()
    {
        super("stats");
        addCommand(new CommandStatSet());
        addCommand(new CommandStatGet());
    }
}
