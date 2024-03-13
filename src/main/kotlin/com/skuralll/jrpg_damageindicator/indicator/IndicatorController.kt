package com.skuralll.jrpg_damageindicator.indicator

import com.skuralll.jrpg_damageindicator.JRPGDamageIndicator
import com.skuralll.jrpg_damageindicator.packet.PacketHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class IndicatorController(private val plugin: JRPGDamageIndicator, private val packetHandler: PacketHandler) {

    private var indicators = mutableMapOf<Int, Indicator>() // use hash map. because to avoid ConcurrentModificationException

    init {
        // update indicators
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            indicators.keys.toList().forEach { entityId ->
                indicators[entityId]?.update()
                if (!indicators[entityId]!!.alive) {
                    despawn(entityId)
                }
            }
        }, 0, 1)
    }

    // spawn indicator
    fun spawn(damager: Player, target: Entity) {
        val indicator = Indicator(packetHandler, damager, target.location.toVector().add(Vector(0.0, 1.0, 0.0)))
        indicators[indicator.entityId] = indicator
        indicator.show()
    }

    // despawn indicator
    private fun despawn(entityId: Int) {
        if (indicators.containsKey(entityId)) {
            indicators[entityId]?.hide()
            indicators.remove(entityId)
        }
    }

    private fun despawn(indicator: Indicator) {
        despawn(indicator.entityId)
    }

}