package itemspost.itemspost;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TakePost implements Listener {

    @EventHandler
    public void TakeOutPost(PlayerJoinEvent e) {
        File PostDirectory = new File(ItemsPost.getPlugin().getDataFolder(), "posts/" + e.getPlayer() + ".yml");
        if (!PostDirectory.exists()) PostDirectory.mkdirs();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(PostDirectory);
        } catch (FileNotFoundException error) {
            return;
        } catch (IOException error) {
            error.printStackTrace();
        } catch (InvalidConfigurationException error) {
            error.printStackTrace();
        }
        List<String> removecommands = new ArrayList<>();
        List<ItemStack> removeitems = getItemStackListformConfig(config);
        boolean recive = false;
        for(String command : config.getStringList("Commands")) {
            String replacecommand = command.replace("<player>" , e.getPlayer().getName());
            if(replacecommand.split(":")[0].equals("item")) {
                if(isPlayerInventoryFull(e.getPlayer())) {
                    e.getPlayer().sendMessage(ChatColor.RED + "受け取りそびれたアイテムがあります");
                    e.getPlayer().sendMessage(ChatColor.RED + "受け取るにはインベントリに空きを作り再度ログインしてください");
                    config.getStringList("Commands").removeAll(removecommands);
                    try {
                        config.save(PostDirectory);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return;
                } else {
                    ItemsPost.getPlugin().getServer().dispatchCommand(ItemsPost.getPlugin().getServer().getConsoleSender() , replacecommand);
                    recive = true;
                }
            }
            if(replacecommand.split(":")[0].equals("command")) {
                ItemsPost.getPlugin().getServer().dispatchCommand(ItemsPost.getPlugin().getServer().getConsoleSender() , replacecommand);
            }
            removecommands.add(command);
        }
        for(String key : config.getConfigurationSection("Items").getKeys(false)) {
            if(isPlayerInventoryFull(e.getPlayer())) {
                e.getPlayer().sendMessage(ChatColor.RED + "受け取りそびれたアイテムがあります");
                e.getPlayer().sendMessage(ChatColor.RED + "受け取るにはインベントリに空きを作り再度ログインしてください");
                config.set("Items." , null);
                for(ItemStack item : removeitems) {
                    config.set("Items." + config.getConfigurationSection("Items").getKeys(false).size() , item);
                }
                try {
                    config.save(PostDirectory);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            } else {
                e.getPlayer().getInventory().addItem(config.getItemStack("Items." + key));
                config.set("Items." + key , null);
                removeitems.remove(config.getItemStack("Items." + key));
                recive = true;
            }
        }
        if(recive) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "アイテムを受け取りました");
        }
        PostDirectory.delete();
    }

    private boolean isPlayerInventoryFull(Player p) {
        boolean isFull = true;
        for (ItemStack item : p.getInventory().getContents()) {
            if(item == null) {
                isFull = false;
                break;
            }
        }
        return isFull;
    }

    private List<ItemStack> getItemStackListformConfig(FileConfiguration config) {
        List<ItemStack> items = new ArrayList<>();
        for(String key : config.getConfigurationSection("Items").getKeys(false)) {
            items.add(config.getItemStack("Items" + key));
        }
        return items;
    }
}
