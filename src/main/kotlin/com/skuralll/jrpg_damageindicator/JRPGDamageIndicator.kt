package com.skuralll.jrpg_damageindicator

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import org.bukkit.plugin.java.JavaPlugin

class JRPGDamageIndicator : JavaPlugin() {

    // ProtocolLib
    private val protocolManager: ProtocolManager by lazy{
        ProtocolLibrary.getProtocolManager()
    }
    // IndicatorController
    private val indicatorController: IndicatorController by lazy{
        IndicatorController(protocolManager)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(indicatorController), this)
    }

    override fun onDisable() {

    }
}
