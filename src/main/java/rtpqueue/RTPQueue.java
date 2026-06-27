package rtpqueue;

import rtpqueue.commands.RTPAdminCommand;
import rtpqueue.commands.RTPQueueCommand;
import rtpqueue.elo.EloManager;
import rtpqueue.kit.KitManager;
import rtpqueue.listeners.*;
import rtpqueue.queue.QueueManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RTPQueue extends JavaPlugin {

    private static RTPQueue instance;
    private EloManager eloManager;
    private KitManager kitManager;
    private QueueManager queueManager;
    private MatchRespawnListener matchRespawnListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        eloManager = new EloManager(this);
        kitManager = new KitManager(this);
        queueManager = new QueueManager(this);

        getCommand("rtpqueue").setExecutor(new RTPQueueCommand(this));
        getCommand("rtpadmin").setExecutor(new RTPAdminCommand(this));

        matchRespawnListener = new MatchRespawnListener(this);

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new QueueListener(this), this);
        getServer().getPluginManager().registerEvents(new KitEditorListener(this), this);
        getServer().getPluginManager().registerEvents(new MatchListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandBlockListener(this), this);
        getServer().getPluginManager().registerEvents(matchRespawnListener, this);

        getLogger().info("RTPQueue enabled.");
    }

    @Override
    public void onDisable() {
        if (queueManager != null) queueManager.shutdown();
        if (eloManager != null) eloManager.saveAll();
        if (kitManager != null) kitManager.saveAll();
        getLogger().info("RTPQueue disabled.");
    }

    public static RTPQueue getInstance() {
        return instance;
    }

    public EloManager getEloManager() {
        return eloManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public MatchRespawnListener getMatchRespawnListener() {
        return matchRespawnListener;
    }
}