package itemspost.v1_14_4;

import itemspost.interfaces.MojangAPI;
import itemspost.interfaces.TakePostBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TakePost1_14_4 extends TakePostBase {
    JavaPlugin plugin;

    public TakePost1_14_4(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @EventHandler
    public void TakeOutPost(PlayerJoinEvent e) {
        File PostFile = new File(plugin.getDataFolder(), "posts/" + e.getPlayer().getUniqueId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(PostFile);
        } catch (FileNotFoundException error) {
            return;
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
        List<String> removecommands = new ArrayList<>();
        List<ItemStack> items = (List<ItemStack>) config.get("Items" , new ArrayList());
        List<ItemStack> removeitems = new ArrayList<>();
        boolean recive = false;
        for(String command : config.getStringList("Commands")) {
            String replacecommand = command.replace("<player>" , e.getPlayer().getName());
            String consolecommand = replacecommand.split(":")[1].replace("<player>" , e.getPlayer().getName());
            if(replacecommand.split(":")[0].equals("item")) {
                if(isPlayerInventoryFull(e.getPlayer())) {
                    e.getPlayer().sendMessage(ChatColor.RED + "受け取りそびれたアイテムがあります");
                    e.getPlayer().sendMessage(ChatColor.RED + "受け取るにはインベントリに空きを作り再度ログインしてください");
                    config.getStringList("Commands").removeAll(removecommands);
                    try {
                        config.save(PostFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return;
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender() , consolecommand);
                    recive = true;
                }
            }
            if(replacecommand.split(":")[0].equals("command")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender() , consolecommand);
            }
            removecommands.add(command);
        }
        for(ItemStack item : items) {
            if(isPlayerInventoryFull(e.getPlayer())) {
                e.getPlayer().sendMessage(ChatColor.RED + "受け取りそびれたアイテムがあります");
                e.getPlayer().sendMessage(ChatColor.RED + "受け取るにはインベントリに空きを作り再度ログインしてください");
                break;
            } else {
                e.getPlayer().getInventory().addItem(item);
                removeitems.add(item);
                recive = true;
            }
        }
        if(recive) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "アイテムを受け取りました");
        }
        items.removeAll(removeitems);
        config.set("Items" , items);
        try {
            config.save(PostFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if(items.size() == 0) {
            PostFile.delete();
        }
    }

    private boolean isPlayerInventoryFull(Player p) {
        boolean isFull = true;
        for (ItemStack item : p.getInventory().getStorageContents()) {
            if(item == null) {
                isFull = false;
                break;
            }
        }
        return isFull;
    }

}
