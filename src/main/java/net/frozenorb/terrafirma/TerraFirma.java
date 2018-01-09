package net.frozenorb.terrafirma;

import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.util.ClassUtils;
import net.frozenorb.terrafirma.claim.Claim;
import net.frozenorb.terrafirma.claim.param.ClaimOwner;
import net.frozenorb.terrafirma.claim.param.ClaimOwnerTypeParameter;
import net.frozenorb.terrafirma.claim.param.ClaimTypeParameter;
import net.frozenorb.terrafirma.visual.ClaimDrawTask;
import net.frozenorb.terrafirma.visual.SelectionDrawTask;
import net.frozenorb.terrafirma.data.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TerraFirma extends JavaPlugin {
    /* Singleton instance getter */
    @Getter private static TerraFirma instance;

    @Getter private StorageHandler storageHandler;

    @Override
    public void onEnable() {

        instance = this;

        storageHandler = new StorageHandler();
        storageHandler.loadClaims();

        new SelectionDrawTask().runTaskTimerAsynchronously(getInstance(), 1L, 2L);
        new ClaimDrawTask().runTaskTimerAsynchronously(getInstance(), 20L, 20L);

        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.terrafirma.command");

        FrozenCommandHandler.registerParameterType(ClaimOwner.class, new ClaimOwnerTypeParameter());
        FrozenCommandHandler.registerParameterType(Claim.class, new ClaimTypeParameter());

        registerListeners();

    }

    @Override
    public void onDisable() {
        storageHandler.closeMongo();
    }

    /**
     * Registers all of our listeners in the net.frozeorb.terrafirma.listener package
     */
    private void registerListeners() {

        ClassUtils.getClassesInPackage(this, "net.frozenorb.terrafirma.listener").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
