package com.skuralll.jrpg_damageindicator

import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EventListener(private val indicatorController: IndicatorController) : Listener {

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        // check if the entity is a living entity
        val entity = event.entity
        if (entity !is LivingEntity) return
        if (entity is ArmorStand) return
        var damager: Player? = null
        // by player direct attack
        if (event.damager is Player) {
            damager = event.damager as Player
        }

        // show indicator
        if (damager == null) return
        indicatorController.spawn(damager, entity, event.damage)
    }

}