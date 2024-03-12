package com.skuralll.jrpg_damageindicator.indicator

import com.comphenix.protocol.ProtocolManager
import org.bukkit.entity.Player

class IndicatorController(private val protocolManager: ProtocolManager) {

    // spawn indicator
    fun spawn(player: Player){
        player.sendMessage("spawn indicator")
    }

}