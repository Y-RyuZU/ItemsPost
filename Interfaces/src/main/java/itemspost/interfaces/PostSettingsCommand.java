package itemspost.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public abstract class PostSettingsCommand implements CommandExecutor {

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
