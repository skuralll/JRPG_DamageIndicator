package com.skuralll.jrpg_damageindicator.indicator

import com.skuralll.jrpg_damageindicator.JRPGDamageIndicator
import com.skuralll.jrpg_damageindicator.packet.PacketHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class IndicatorController(
    private val plugin: JRPGDamageIndicator,
    private val packetHandler: PacketHandler
) {

    companion object {
        const val SPAWN_RANGE_XZ = 1.5
        const val SPAWN_RANGE_Y = 0.5
    }

    private var indicators =
        mutableMapOf<Int, Indicator>() // use hash map. because to avoid ConcurrentModificationException

    init {
        // update indicators
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            indicators.keys.toList().forEach { entityId ->
                println(entityId)
                indicators[entityId]?.update()
                if (!indicators[entityId]!!.isAlive()) {
                    despawn(entityId)
                }
            }
        }, 0, 1)
    }

    // spawn indicator
    fun spawn(damager: Player, target: Entity, type: DamageType, damage: Double) {
        val indicator =
            Indicator(
                plugin,
                damager,
                getSpawnVector(target),
                type,
                damage
            )
        indicators[indicator.entityId] = indicator
        indicator.spawn()
    }

    // despawn indicator
    private fun despawn(entityId: Int) {
        if (indicators.containsKey(entityId)) {
            indicators[entityId]?.despawn()
            indicators.remove(entityId)
        }
    }

    private fun despawn(indicator: Indicator) {
        despawn(indicator.entityId)
    }

    // get spawn vector
    private fun getSpawnVector(entity: Entity): Vector {
        val pos = entity.location.toVector()
        val x = pos.x + SPAWN_RANGE_XZ * (Math.random() - 0.5)
        val y = entity.boundingBox.centerY + SPAWN_RANGE_Y * (Math.random() - 0.5)
        val z = pos.z + SPAWN_RANGE_XZ * (Math.random() - 0.5)
        return Vector(x, y, z)
    }

}