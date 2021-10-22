package br.com.strixcloud.lstop;

import br.com.strixcloud.lstop.bukkit.command.TopDonatorsCommand;
import br.com.strixcloud.lstop.bukkit.command.lstop.LsTopCommand;
import br.com.strixcloud.lstop.bukkit.listener.LegendChatListener;
import br.com.strixcloud.lstop.entities.util.ConfigFile;
import br.com.strixcloud.lstop.entities.util.DateDuration;
import br.com.strixcloud.lstop.provider.config.IDisplayProvider;
import br.com.strixcloud.lstop.provider.config.IStorageProvider;
import br.com.strixcloud.lstop.provider.config.impl.YamlDisplayProvider;
import br.com.strixcloud.lstop.provider.config.impl.YamlStorageProvider;
import br.com.strixcloud.lstop.provider.hologram.IHologramProvider;
import br.com.strixcloud.lstop.provider.hologram.impl.HDHologramProvider;
import br.com.strixcloud.lstop.provider.log.IStrixLogger;
import br.com.strixcloud.lstop.provider.log.impl.StrixLogger;
import br.com.strixcloud.lstop.provider.lojasquare.ILSProvider;
import br.com.strixcloud.lstop.provider.lojasquare.impl.HttpLSProvider;
import br.com.strixcloud.lstop.provider.lojasquare.impl.LSVersion;
import br.com.strixcloud.lstop.provider.request.IRequestProvider;
import br.com.strixcloud.lstop.provider.request.impl.HttpRequestProvider;
import br.com.strixcloud.lstop.services.hologram.load.HologramLoadController;
import br.com.strixcloud.lstop.services.lojasquare.load.LojasquareLoadController;
import br.com.strixcloud.lstop.task.HologramsUpdateTask;
import br.com.strixcloud.lstop.task.LojasquareUpdateTask;
import lombok.Getter;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Timer;

public class StrixLSTop extends JavaPlugin {

    @Getter private IRequestProvider requestProvider;
    @Getter private ILSProvider lsProvider;
    @Getter private IDisplayProvider displayProvider;
    @Getter private IHologramProvider hologramProvider;
    @Getter private IStorageProvider storageProvider;

    @Getter private ConfigFile configFile;
    @Getter private ConfigFile storageFile;
    @Getter private IStrixLogger sLogger;

    private Timer timer;
    private BukkitTask bukkitTask;

    public void onEnable() {
        var duration = new DateDuration();
        if (!load()) return;

        long ms = duration.calculate();
        sLogger.info(String.format("§dLoaded plugin in §f%sms", ms));

        var sender = Bukkit.getConsoleSender();
        sender.sendMessage("§d  ___ _       _     _    ___ _____         ");
        sender.sendMessage("§d / __| |_ _ _(_)_ _| |  / __|_   _|__ _ __ ");
        sender.sendMessage("§d \\__ \\  _| '_| \\ \\ / |__\\__ \\ | |/ _ \\ '_ \\ §7§l"+this.getDescription().getVersion());
        sender.sendMessage("§d |___/\\__|_| |_/_\\_\\____|___/ |_|\\___/ .__/");
        sender.sendMessage("§d                                     |_|   ");
        sender.sendMessage("  §7§lCoding your dreams §8| §5§lStrix Cloud");
        sender.sendMessage("§8 ----------------------------------");
    }

    @Override
    public void onDisable() {
        unload();

        var sender = Bukkit.getConsoleSender();
        sender.sendMessage("§c  ___ _       _     _    ___ _____         ");
        sender.sendMessage("§c / __| |_ _ _(_)_ _| |  / __|_   _|__ _ __ ");
        sender.sendMessage("§c \\__ \\  _| '_| \\ \\ / |__\\__ \\ | |/ _ \\ '_ \\ §7§l"+this.getDescription().getVersion());
        sender.sendMessage("§c |___/\\__|_| |_/_\\_\\____|___/ |_|\\___/ .__/");
        sender.sendMessage("§c                                     |_|   ");
        sender.sendMessage("  §7§lBye Bye §8| §5§lStrix Cloud");
        sender.sendMessage("§8 ----------------------------------");
    }

    private boolean load() {
        setupConfiguration();
        setupLS();
        if (!loadData()) return false;
        setupDisplay();
        loadBukkit();
        loadTasks();
        return true;
    }

    private void unload() {
        if (hasPluginLoaded("HolographicDisplays") && hologramProvider != null)
            hologramProvider.getHolograms().forEach(hologramProvider::delete);
        if (timer != null) timer.cancel();
        if (bukkitTask != null) bukkitTask.cancel();
    }

    /*
        Setup
     */

    private void setupConfiguration() {
        configFile = new ConfigFile(this, "config.yml");
        storageFile = new ConfigFile(this, "storage.yml");
    }

    private void setupLS() {
        requestProvider = new HttpRequestProvider();
        sLogger = new StrixLogger(this, configFile);
        var authToken = configFile.getConfig().getString("Lojasquare.secret-key");
        if (!configFile.getConfig().contains("Lojasquare.api-version")) {
            configFile.getConfig().set("Lojasquare.api-version", "LS_V1");
            configFile.saveConfig();
        }
        var version = LSVersion.valueOf(configFile.getConfig().getString("Lojasquare.api-version").toUpperCase());
        lsProvider = new HttpLSProvider(requestProvider, authToken, version);
    }

    private void setupDisplay() {
        displayProvider = new YamlDisplayProvider(configFile);
        displayProvider.load();

        storageProvider = new YamlStorageProvider(storageFile);

        if (hasPluginLoaded("Legendchat")) {
            Bukkit.getPluginManager().registerEvents(new LegendChatListener(displayProvider), this);
            sLogger.info("LegendChat hook initialized, tag 'top_donator'");
        } else {
            sLogger.warn("LegendChat not loaded, disabling integration");
        }

        if (hasPluginLoaded("HolographicDisplays")) {
            hologramProvider = new HDHologramProvider();
            HologramLoadController.getInstance().handle();
            sLogger.info("HolographicDisplays hook initialized, displays holograms enabled");
        } else {
            sLogger.warn("HolographicDisplays not loaded, disabling holograms module");
        }
    }

    private void loadBukkit() {
        getCommand("topdonators").setExecutor(new TopDonatorsCommand());
        getCommand("strixlstop").setExecutor(new LsTopCommand());
    }

    private void loadTasks() {
        timer = new Timer();

        var lsTaskDelay = configFile.getConfig().getInt("Tasks.lojasquare-update") * 60 * 1000;
        var holoTaskDelay = configFile.getConfig().getInt("Tasks.holograms-update") * 60 * 20;
        timer.schedule(new LojasquareUpdateTask(), lsTaskDelay, lsTaskDelay);
        if (hasPluginLoaded("HolographicDisplays"))
            bukkitTask = new HologramsUpdateTask().runTaskTimer(this, holoTaskDelay, holoTaskDelay);
    }

    public boolean hasPluginLoaded(String name) {
        var pl = Bukkit.getPluginManager().getPlugin(name);
        return pl != null && pl.isEnabled();
    }

    /*
        Loading
     */

    private boolean loadData() {
        var valid = LojasquareLoadController.getInstance().handle();
        if (!valid) Bukkit.getPluginManager().disablePlugin(this);
        return valid;
    }

    public static StrixLSTop getInstance() {
        return getPlugin(StrixLSTop.class);
    }
}
