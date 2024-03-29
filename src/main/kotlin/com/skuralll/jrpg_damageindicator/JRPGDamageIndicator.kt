package com.skuralll.jrpg_damageindicator

import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import com.skuralll.jrpg_damageindicator.packet.PacketHandler
import org.bukkit.plugin.java.JavaPlugin

class JRPGDamageIndicator : JavaPlugin() {

    // Packet Handler
    private val packetHandler: PacketHandler by lazy {
        PacketHandler()
    }

    // IndicatorController
    private val indicatorController: IndicatorController by lazy {
        IndicatorController(this, packetHandler)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(this, indicatorController), this)
    }

    override fun onDisable() {

    }
}
