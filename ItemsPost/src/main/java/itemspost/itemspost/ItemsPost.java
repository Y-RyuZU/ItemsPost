package itemspost.itemspost;

import itemspost.interfaces.*;
import itemspost.v1_12_2.PostSettingsCommand1_12_2;
import itemspost.v1_12_2.TakePost1_12_2;
import itemspost.v1_14_4.PostSettingsCommand1_14_4;
import itemspost.v1_14_4.TakePost1_14_4;
import itemspost.v1_15_2.PostSettingsCommand1_15_2;
import itemspost.v1_15_2.TakePost1_15_2;
import itemspost.v1_16_4.PostSettingsCommand1_16_4;
import itemspost.v1_16_4.TakePost1_16_4;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ItemsPost extends JavaPlugin implements Listener {
    private static ItemsPost plugin;
    public static MojangAPI MAPI = new MojangAPI();
    public TakePostBase TPB;
    public PostSettingsCommand TSC;
    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        TPB = null;
        TSC = null;
        if(version.equals("v1_12_R1")){
            TPB = new TakePost1_12_2(plugin);
            TSC = new PostSettingsCommand1_12_2(plugin);
        }
        if(version.equals("v1_14_R1")){
            TPB = new TakePost1_14_4(plugin);
            TSC = new PostSettingsCommand1_14_4(plugin);
        }
        if(version.equals("v1_15_R1")){
            TPB = new TakePost1_15_2(plugin);
            TSC = new PostSettingsCommand1_15_2(plugin);
        }
        if(version.equals("v1_16_R1")){
            TPB = new TakePost1_16_4(plugin);
            TSC = new PostSettingsCommand1_16_4(plugin);
        }
        if(TPB == null) {
            Bukkit.getPluginManager().disablePlugin(plugin);
        } else {
            getLogger().info(ChatColor.GREEN + "ItemsPostがEnableになりました");
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(TPB , this);
            File PostsDirectory = new File(plugin.getDataFolder(), "posts");
            if (!PostsDirectory.exists()) PostsDirectory.mkdirs();
            getCommand("ip").setExecutor(TSC);
            getCommand("itemspost").setExecutor(TSC);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GREEN + "ItemsPostがDisableになりました");
    }
}
