package com.skuralll.jrpg_damageindicator.indicator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import org.bukkit.entity.Player
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.skuralll.jrpg_damageindicator.JRPGDamageIndicator
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity

class IndicatorController(private val plugin: JRPGDamageIndicator, private val protocolManager: ProtocolManager) {

    private val indicators = mutableSetOf<Indicator>()

    init{
        // update indicators
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            indicators.forEach {
                it.update()
                if(!it.alive){
                    despawn(it)
                }
            }
        }, 0, 1)
    }

    // spawn indicator
    fun spawn(damager: Player, target: Entity){
        val indicator = Indicator(protocolManager, damager, target.location.toVector())
        indicators.add(indicator)
        indicator.show()
    }

    // despawn indicator
    private fun despawn(indicator: Indicator){
        indicator.hide()
        indicators.remove(indicator)
    }

}