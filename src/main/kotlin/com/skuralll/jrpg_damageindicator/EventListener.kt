package com.skuralll.jrpg_damageindicator

import com.skuralll.jrpg_damageindicator.indicator.DamageType
import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EventListener(private val indicatorController: IndicatorController) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        // check if the entity is a living entity
        val entity = event.entity
        if (entity !is LivingEntity) return
        if (entity is ArmorStand) return
        var damager: Player? = null
        var type: DamageType = DamageType.DIRECT_NORMAL


        if (event.damager is Projectile) {
            // by projectile
            val projectile = event.damager as Projectile
            if (projectile.shooter !is Player) return
            damager = projectile.shooter as Player
            if (projectile is AbstractArrow) {
                if (projectile is Trident) {
                    type = DamageType.TRIDENT_NORMAL
                } else {
                    type = DamageType.ARROW_NORMAL
                    if (event.isCritical) {
                        type = DamageType.ARROW_CRITICAL
                    }
                }
            }
        } else if (event.damager is Player) {
            // by player direct attack
            damager = event.damager as Player
            if (event.isCritical) {
                type = DamageType.DIRECT_CRITICAL
            }
        }

        // show indicator
        if (damager == null) return
        indicatorController.spawn(damager, entity, type, event.damage)
    }

}