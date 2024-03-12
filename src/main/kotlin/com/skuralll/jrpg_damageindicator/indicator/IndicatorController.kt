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
import org.bukkit.util.Vector

class IndicatorController(private val plugin: JRPGDamageIndicator, private val protocolManager: ProtocolManager) {

    private var indicators = mutableMapOf<Int, Indicator>() // use hash map. because to avoid ConcurrentModificationException

    init{
        // update indicators
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val despawns = mutableListOf<Int>()
            indicators.forEach  { (_, indicator) ->
                indicator.update()
                if(!indicator.alive){
                    despawns.add(indicator.entityId)
                }
            }
            despawns.forEach { despawn(it) }
        }, 0, 1)
    }

    // spawn indicator
    fun spawn(damager: Player, target: Entity){
        val indicator = Indicator(protocolManager, damager, target.location.toVector().add(Vector(0.0, 2.0, 0.0)))
        indicators[indicator.entityId] = indicator
        indicator.show()
    }

    // despawn indicator
    private fun despawn(entityId: Int){
        if (indicators.containsKey(entityId)){
            indicators[entityId]?.hide()
            indicators.remove(entityId)
        }
    }

    private fun despawn(indicator: Indicator){
        despawn(indicator.entityId)
    }

}