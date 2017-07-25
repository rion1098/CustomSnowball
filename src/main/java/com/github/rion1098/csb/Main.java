package com.github.rion1098.csb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public static Plugin plugin;
	public static double yukidama;
	public static boolean dropBall;
	public static boolean catchBall;
	private String prefix = ChatColor.AQUA + "[CSB]" + ChatColor.RESET;
	private String warnprefix = ChatColor.RED + "[CSB WARN]" + ChatColor.RESET;

	public void onEnable()
	{
		plugin = this;

		getServer().getPluginManager().registerEvents(new EListener(), this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		Main.yukidama = getConfig().getDouble("yukidama");
		Main.dropBall = getConfig().getBoolean("dropBall");
		if ( ! getConfig().contains("catchBall")) {
			getConfig().set("catchBall", false);
		}
		Main.catchBall = getConfig().getBoolean("catchBall");


	}

	public static Plugin getInstance() {
		return plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if (cmd.getName().equalsIgnoreCase("csb") || cmd.getName().equalsIgnoreCase("CustomSnowBall")) {
			sender.sendMessage(ChatColor.GREEN + "===============[Custom Snowball]===============");
			sender.sendMessage(ChatColor.AQUA + "/yukidama <ダメージ量>" + ChatColor.WHITE + " :  雪玉のダメージ量を変える");
			sender.sendMessage(ChatColor.AQUA + "/dropball <true / false>" + ChatColor.WHITE + " :  雪玉のドロップを変える");
			sender.sendMessage(ChatColor.AQUA + "/catchBall <true / false>" + ChatColor.WHITE + " :  同チームのボールが当たった時に即時取得させるか");
			sender.sendMessage(ChatColor.GREEN + "     引数なしで実行した場合、現在の値を返す。");
		}

		if (cmd.getName().equalsIgnoreCase("yukidama")){
			if (args.length < 1 || args.length > 1) {
				sender.sendMessage(prefix + "現在雪玉のダメージ量は" + ChatColor.YELLOW + yukidama + ChatColor.AQUA + "です。");
				return true;
			}

			double number;
			try {
				number = Double.valueOf(args[0]).doubleValue();
			} catch (NumberFormatException e) {
				sender.sendMessage(warnprefix + "指定された数字が認識できませんでした。");
				return true;
			}
			getConfig().set("yukidama", number);
			Main.yukidama = number;
			Bukkit.broadcastMessage(prefix + "雪玉のダメージを " + number + " に設定しました。");
			saveConfig();
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dropBall")) {
			if (args.length < 1 || args.length > 1) {
				if (dropBall) {
					sender.sendMessage( prefix + "現在雪玉のドロップは" + ChatColor.GREEN + "有効" + ChatColor.AQUA + "です。");
					return true;
				} else if (! dropBall) {
					sender.sendMessage(prefix + "現在雪玉のドロップは" + ChatColor.RED + "無効" + ChatColor.AQUA + "です。");
					return true;
				}
			}


			if (args[0].equalsIgnoreCase("true")) {
				dropBall = true;
				getConfig().set("dropBall", dropBall);
				saveConfig();
				sender.sendMessage(prefix + "雪玉のドロップを有効にしました。");
			} else if (args[0].equalsIgnoreCase("false")) {
				dropBall = false;
				getConfig().set("dropBall", dropBall);
				saveConfig();
				sender.sendMessage( prefix + "雪玉のドロップを無効にしました。");
				return true;
			}

		}

		if (cmd.getName().equalsIgnoreCase("catchBall")) {
			if (args.length < 1 || args.length > 1) {
				if (catchBall) {
					sender.sendMessage( prefix + "現在パス機能は" + ChatColor.GREEN + "有効" + ChatColor.AQUA + "です。");
					return true;
				} else if (! catchBall) {
					sender.sendMessage(prefix + "現在パス機能は" + ChatColor.RED + "無効" + ChatColor.AQUA + "です。");
					return true;
				}
			}


			if (args[0].equalsIgnoreCase("true")) {
				catchBall = true;
				getConfig().set("catchBall", catchBall);
				saveConfig();
				sender.sendMessage(prefix + "パス機能を有効にしました。");
			} else if (args[0].equalsIgnoreCase("false")) {
				catchBall = false;
				getConfig().set("catchBall", catchBall);
				saveConfig();
				sender.sendMessage( prefix + "パス機能を無効にしました。");
				return true;
			}
		}

		return false;
	}
}
