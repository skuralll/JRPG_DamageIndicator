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
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class EventListener(
    private val plugin: JRPGDamageIndicator,
    private val indicatorController: IndicatorController
) : Listener {

    // NamespacedKeys
    private val burnedByKey = NamespacedKey(plugin, "burned_by")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        // check if the entity is a living entity
        val entity = event.entity
        if (entity !is LivingEntity) return
        val container = entity.persistentDataContainer

        // fire damage
        if (container.has(burnedByKey) && event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            val key = entity.persistentDataContainer.get(burnedByKey, PersistentDataType.STRING)
            if (key != null) {
                val damager = plugin.server.getPlayer(UUID.fromString(key))
                if (damager != null) {
                    indicatorController.spawn(
                        damager,
                        entity,
                        DamageType.FIRED_TICK,
                        event.finalDamage
                    )
                }
            }
        }
    }

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
                    if (projectile.fireTicks != 0) {
                        setEntityMarked(entity, damager, 100, burnedByKey)
                    }
                }
            }
        } else if (event.damager is Player) {
            // by player direct attack
            damager = event.damager as Player
            if (damager.inventory.itemInMainHand.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
                setEntityMarked(entity, damager, 80, burnedByKey)
            }
            if (event.isCritical) {
                type = DamageType.DIRECT_CRITICAL
            }
        }

        // show indicator
        if (damager == null) return
        indicatorController.spawn(damager, entity, type, event.finalDamage)
    }

    // set entity marked by damager (for fired, potion, etc.)
    private fun setEntityMarked(
        entity: LivingEntity,
        damager: Player,
        duration: Int,
        key: NamespacedKey
    ) {
        entity.persistentDataContainer.set(
            key,
            PersistentDataType.STRING,
            damager.uniqueId.toString()
        )
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            entity.persistentDataContainer.remove(key)
        }, duration.toLong())
    }

}