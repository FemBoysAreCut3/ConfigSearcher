package femboys.are.cute.configsearcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConfigSearcher extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("searchconfigs").setExecutor(this);
        getCommand("searchjars").setExecutor(this);
        getLogger().info("ConfigSearcher has been enabled!");
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <query>");
            return true;
        }

        final String query = combineArgs(args);

        if (command.getName().equalsIgnoreCase("searchconfigs")) {
            sender.sendMessage(ChatColor.YELLOW + "Searching all configurations in the server folder for: " + query);

            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    searchConfigsRecursive(new File("."), query, sender);
                }
            });
            return true;

        } else if (command.getName().equalsIgnoreCase("searchjars")) {
            sender.sendMessage(ChatColor.YELLOW + "Searching all JAR files in the plugins folder for: " + query);

            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    searchJarsInFolder(getDataFolder().getParentFile(), query, sender);
                }
            });
            return true;
        }

        return false;
    }

    private String combineArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void searchConfigsRecursive(File dir, String query, CommandSender sender) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                searchConfigsRecursive(file, query, sender);
            } else {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".yml") || name.endsWith(".properties") || name.endsWith(".txt") || name.endsWith(".json")) {
                    searchInFile(file, query, sender, file.getPath());
                }
            }
        }
    }

    private void searchInFile(File file, String query, CommandSender sender, String identifier) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.toLowerCase().contains(query.toLowerCase())) {
                    sender.sendMessage(ChatColor.GREEN + "[" + identifier + ":" + lineNumber + "] " + ChatColor.WHITE + line.trim());
                }
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception ignored) {}
            }
        }
    }

    private void searchJarsInFolder(File dir, String query, CommandSender sender) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                searchInsideJar(file, query, sender);
            }
        }
    }

    private void searchInsideJar(File jarFile, String query, CommandSender sender) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(jarFile));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName().toLowerCase();
                    if (name.endsWith(".yml") || name.endsWith(".properties") || name.endsWith(".txt") || name.endsWith(".json") || name.endsWith(".xml")) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
                        String line;
                        int lineNumber = 0;
                        boolean matched = false;
                        StringBuilder results = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            lineNumber++;
                            if (line.toLowerCase().contains(query.toLowerCase())) {
                                matched = true;
                                results.append(ChatColor.GRAY).append("  Line ").append(lineNumber).append(": ").append(ChatColor.WHITE).append(line.trim()).append("\n");
                            }
                        }

                        if (matched) {
                            sender.sendMessage(ChatColor.GOLD + "[JAR: " + jarFile.getName() + " -> " + entry.getName() + "]");
                            sender.sendMessage(results.toString());
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (Exception e) {
        } finally {
            if (zis != null) {
                try { zis.close(); } catch (Exception ignored) {}
            }
        }
    }
}