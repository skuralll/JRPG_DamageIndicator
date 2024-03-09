package com.skuralll.jrpg_damageindicator

import org.bukkit.plugin.java.JavaPlugin

class JRPGDamageIndicator : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        server.logger.info("This is made by Kotlin")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
