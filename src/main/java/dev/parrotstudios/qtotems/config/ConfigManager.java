package dev.parrotstudios.qtotems.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ConfigManager {
    private static FileConfiguration config;
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        ConfigManager.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static void reloadConfig() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static String getString(String path) {
        return config.getString(path);
    }

    public static String getString(String path, String def) {
        return config.getString(path, def);
    }

    public static int getInt(String path) {
        return config.getInt(path);
    }

    public static int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public static double getDouble(String path) {
        return config.getDouble(path);
    }

    public static double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public static long getLong(String path) {
        return config.getLong(path);
    }

    public static long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public static boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public static boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public static List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    public static List<String> getStringList(String path, List<String> def) {
        return config.getStringList(path).isEmpty() ? def : config.getStringList(path);
    }

    public static ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public static void set(String path, Object value) {
        config.set(path, value);
    }

    public static void save() {
        plugin.saveConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
}
