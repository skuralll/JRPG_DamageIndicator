package com.skuralll.jrpg_damageindicator

import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import org.bukkit.plugin.java.JavaPlugin

class JRPGDamageIndicator : JavaPlugin() {

    // IndicatorController
    private val indicatorController: IndicatorController by lazy {
        IndicatorController(this)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(this, indicatorController), this)
    }

    override fun onDisable() {

    }
}
