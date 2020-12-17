package itemspost.v1_14_4;

import itemspost.interfaces.MojangAPI;
import itemspost.interfaces.PostSettingsCommand;
import itemspost.interfaces.TakePostBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostSettingsCommand1_14_4 extends PostSettingsCommand {
    JavaPlugin plugin;

    public PostSettingsCommand1_14_4(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ip") || command.getName().equalsIgnoreCase("itemspost")) {
            if (!sender.hasPermission("ip.op")) {
                sender.sendMessage(ChatColor.RED + "ぽまえけんげんないやろ");
                return true;
            }
            if (args.length <= 0) {
                sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
                sender.sendMessage(ChatColor.BLUE + "/" + label + " reload :リロード");
                sender.sendMessage(ChatColor.BLUE + "/" + label + " item [add/remove] [MCID] :プレイヤーのポストのアイテムを編集します");
                sender.sendMessage(ChatColor.BLUE + "/" + label + " command [add/remove] [MCID] [item:/command...][command:/command...][etc...] :プレイヤーのポストのコマンドを編集します");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "リロード完了");
                return true;
            }

            if (args[0].equalsIgnoreCase("item")) {
                if (args.length <= 1) {
                    sender.sendMessage(ChatColor.BLUE + "/" + label + " item [add/remove] [MCID]");
                    sender.sendMessage(ChatColor.BLUE + "指定したプレイヤーのポストのアイテムを追加、削除します");
                    sender.sendMessage(ChatColor.BLUE + "アイテムを所持して実行してください");
                    return true;
                }
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if(item == null) {
                        sender.sendMessage(ChatColor.RED + "アイテムを手に持ってください");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("add")) {
                        if (args.length <= 2) {
                            sender.sendMessage(ChatColor.RED + "/" + label + " item add [MCID]");
                            sender.sendMessage(ChatColor.BLUE + "指定したプレイヤーのポストのアイテムを追加します");
                            sender.sendMessage(ChatColor.BLUE + "アイテムを所持して実行してください");
                            return true;
                        }
                        String uuid = MojangAPI.getUuid(args[2]);
                        if(uuid.equals("invalid name") || uuid.equals("error")) {
                            sender.sendMessage(ChatColor.RED + "その名前のプレイヤーは存在しません");
                            return true;
                        }
                        File PostFile = new File(plugin.getDataFolder(), "posts/" + uuid + ".yml");
                        YamlConfiguration config = new YamlConfiguration();
                        try {
                            config.load(PostFile);
                        } catch (FileNotFoundException e) {
                        } catch (IOException | InvalidConfigurationException e) {
                            e.printStackTrace();
                        }
                        List<ItemStack> items = (List<ItemStack>) config.getList("Items" , new ArrayList<>());
                        items.add(item);
                        config.set("Items" , items);
                        try {
                            config.save(PostFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(ChatColor.GREEN + "プレイヤーのポストにアイテムを追加しました");
                    }

                    if (args[1].equalsIgnoreCase("remove")) {
                        if (args.length <= 2) {
                            sender.sendMessage(ChatColor.RED + "/" + label + "item remove [MCID]");
                            sender.sendMessage(ChatColor.BLUE + "指定したプレイヤーのポストのアイテムを削除します");
                            sender.sendMessage(ChatColor.BLUE + "アイテムを所持して実行してください");
                            return true;
                        }
                        String uuid = MojangAPI.getUuid(args[2]);
                        if(uuid.equals("invalid name") || uuid.equals("error")) {
                            sender.sendMessage(ChatColor.RED + "その名前のプレイヤーは存在しません");
                            return true;
                        }
                        File PostFile = new File(plugin.getDataFolder(), "posts/" + uuid + ".yml");
                        YamlConfiguration config = new YamlConfiguration();
                        try {
                            config.load(PostFile);
                        } catch (FileNotFoundException e) {
                            sender.sendMessage(ChatColor.RED + "そのプレイヤーにはポストがありません");
                            return true;
                        } catch (IOException | InvalidConfigurationException e) {
                            e.printStackTrace();
                        }
                        List<ItemStack> items = (List<ItemStack>) config.getList("Items" , new ArrayList<>());
                        if(items.contains(item)) {
                            items.remove(item);
                            config.set("Items" , items);
                        } else {
                            sender.sendMessage(ChatColor.RED + "そのプレイヤーのポストにはそのアイテムがありません");
                            return true;
                        }
                        try {
                            config.save(PostFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(ChatColor.GREEN + "プレイヤーのポストからアイテムを削除しました");
                    }
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("command")) {
                if (args.length <= 1) {
                    sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
                    sender.sendMessage(ChatColor.BLUE + "/" + label + " command [add/remove] MCID [item:/command...][command:/command...][etc...]");
                    sender.sendMessage(ChatColor.BLUE + "コマンドを入れる際は[]で囲い");
                    sender.sendMessage(ChatColor.BLUE + "コマンドごとの[]の囲いの間はあけないでください");
                    sender.sendMessage(ChatColor.BLUE + "コマンドの前にはcommand: または item:のラベルを入れてください");
                    sender.sendMessage(ChatColor.BLUE + "ここで設定したコマンドはすべてコンソールで実行されます");
                    sender.sendMessage(ChatColor.BLUE + "コマンド内で<player>を使用するとプレイヤー名に置き換わります");
                    sender.sendMessage(ChatColor.GOLD + "----------各ラベルの説明----------");
                    sender.sendMessage(ChatColor.BLUE + "command: 通常のコマンドラベルを設定します");
                    sender.sendMessage(ChatColor.BLUE + "item: 主にItemのGiveコマンドで使用します実行する前に指定されたプレイヤーのインベントリの空きを確認します");
                    return true;
                }
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 4) {
                        sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
                        sender.sendMessage(ChatColor.BLUE + "/" + label + " command add MCID [item:/command...][command:/command...][etc...]");
                        sender.sendMessage(ChatColor.BLUE + "コマンドを入れる際は[]で囲い");
                        sender.sendMessage(ChatColor.BLUE + "コマンドごとの[]の囲いの間はあけないでください");
                        sender.sendMessage(ChatColor.BLUE + "コマンドの前にはcommand: または item:のラベルを入れてください");
                        sender.sendMessage(ChatColor.BLUE + "ここで設定したコマンドはすべてコンソールで実行されます");
                        sender.sendMessage(ChatColor.BLUE + "コマンド内で<player>を使用するとプレイヤー名に置き換わります");
                        sender.sendMessage(ChatColor.GOLD + "----------各ラベルの説明----------");
                        sender.sendMessage(ChatColor.BLUE + "command: 通常のコマンドラベルを設定します");
                        sender.sendMessage(ChatColor.BLUE + "item: 主にItemのGiveコマンドで使用します実行する前に指定されたプレイヤーのインベントリの空きを確認します");
                        return true;
                    }
                    String uuid = MojangAPI.getUuid(args[2]);
                    if(uuid.equals("invalid name") || uuid.equals("error")) {
                        sender.sendMessage(ChatColor.RED + "その名前のプレイヤーは存在しません");
                        return true;
                    }
                    File PostFile = new File(plugin.getDataFolder(), "posts/" + uuid + ".yml");
                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        config.load(PostFile);
                    } catch (FileNotFoundException e) {
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                    String commands = "";
                    for(int i = 3 ; args.length > i ; i++) {
                        if(i == 3) {
                            commands += args[i];
                        } else {
                            commands += " " + args[i];
                        }
                    }
                    List<String> InCommands = config.getStringList("Commands");
                    List<String> CheckCommands = Arrays.asList(commands.split("]"));
                    List<String> SetCommands = new ArrayList<>();
                    for(String setcommand : CheckCommands) {
                        if(!(setcommand.split(":")[0].equals("[command") || setcommand.split(":")[0].equals("[item"))) {
                            sender.sendMessage(ChatColor.RED + "ラベルを使用してください");
                            return true;
                        }
                    }
                    for(String setcommand : CheckCommands) {
                        SetCommands.add(setcommand.replace("[" , "").replace("/" , ""));
                    }
                    InCommands.addAll(SetCommands);
                    config.set("Commands" , InCommands);
                    try {
                        config.save(PostFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.GREEN + "プレイヤーのポストにコマンドを追加しました");
                }

                if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length != 4) {
                        sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
                        sender.sendMessage(ChatColor.BLUE + "/" + label + " command remove MCID [{item/command}:/command...]");
                        sender.sendMessage(ChatColor.BLUE + "コマンドを入れる際は[]で囲い");
                        sender.sendMessage(ChatColor.BLUE + "コマンドごとの[]の囲いの間はあけないでください");
                        sender.sendMessage(ChatColor.BLUE + "コマンドの前にはcommand: または item:のラベルを入れてください");
                        sender.sendMessage(ChatColor.BLUE + "ここで設定したコマンドはすべてコンソールで実行されます");
                        sender.sendMessage(ChatColor.BLUE + "コマンド内で<player>を使用するとプレイヤー名に置き換わります");
                        sender.sendMessage(ChatColor.GOLD + "----------各ラベルの説明----------");
                        sender.sendMessage(ChatColor.BLUE + "command: 通常のコマンドラベルを設定します");
                        sender.sendMessage(ChatColor.BLUE + "item: 主にItemのGiveコマンドで使用します実行する前に指定されたプレイヤーのインベントリの空きを確認します");
                        return true;
                    }
                    if(!(args[3].split(":")[0].equals("[command") || args[3].split(":")[0].equals("[item"))) {
                        sender.sendMessage(ChatColor.RED + "ラベルを使用してください");
                        return true;
                    }
                    String uuid = MojangAPI.getUuid(args[2]);
                    if(uuid.equals("invalid name") || uuid.equals("error")) {
                        sender.sendMessage(ChatColor.RED + "その名前のプレイヤーは存在しません");
                        return true;
                    }
                    File PostFile = new File(plugin.getDataFolder(), "posts/" + uuid + ".yml");
                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        config.load(PostFile);
                    } catch (FileNotFoundException e) {
                        sender.sendMessage(ChatColor.RED + "そのプレイヤーにはポストがありません");
                        return true;
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                    if(!config.getStringList("Commands").contains(args[3].replace("]" , "").replace("[" , ""))) {
                        sender.sendMessage(ChatColor.RED + "そのプレイヤーのポストにはこのコマンドは登録されていません");
                        return true;
                    }
                    List<String> commands = config.getStringList("Commands");
                    commands.remove(args[3].replace("]" , "").replace("[" , "").replace("/" , ""));
                    try {
                        config.save(PostFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.GREEN + "プレイヤーのポストからコマンドを削除しました");
                }
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
            sender.sendMessage(ChatColor.BLUE + "/" + label + " reload :リロード");
            sender.sendMessage(ChatColor.BLUE + "/" + label + " item [add/remove] MCID :プレイヤーのポストのアイテムを編集します");
            sender.sendMessage(ChatColor.BLUE + "/" + label + " command [add/remove] MCID [give:/command...][command:/command...][etc...] :プレイヤーのポストにコマンドの編集します");
            return true;
        }
        return true;
    }

}
