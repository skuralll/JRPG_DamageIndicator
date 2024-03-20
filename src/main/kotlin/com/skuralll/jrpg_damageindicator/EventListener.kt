package com.skuralll.jrpg_damageindicator

import com.skuralll.jrpg_damageindicator.indicator.DamageType
import com.skuralll.jrpg_damageindicator.indicator.IndicatorController
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import java.util.*

class EventListener(
    private val plugin: JRPGDamageIndicator,
    private val indicatorController: IndicatorController
) : Listener {

    // NamespacedKeys
    private val burnedByKey = NamespacedKey(plugin, "burned_by")
    private val poisonedByKey = NamespacedKey(plugin, "poisoned_by")
    private val harmedByKey = NamespacedKey(plugin, "harmed_by")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        // check if the entity is a living entity
        val entity = event.entity
        if (entity !is LivingEntity) return
        val container = entity.persistentDataContainer
        // slip damage
        if (container.has(poisonedByKey) && event.cause == EntityDamageEvent.DamageCause.POISON) {
            // poison damage
            val key = entity.persistentDataContainer.get(poisonedByKey, PersistentDataType.STRING)
            if (key != null) {
                val damager = plugin.server.getPlayer(UUID.fromString(key))
                if (damager != null) {
                    indicatorController.spawn(
                        damager,
                        entity,
                        DamageType.POISON_TICK,
                        event.finalDamage
                    )
                }
            }
        } else if (container.has(burnedByKey) && event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            // fire damage
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

        if (event.damager is AreaEffectCloud) {
            // by instance damage by area-effect-cloud
            val areaEffectCloud = event.damager as AreaEffectCloud
            if (isHarming(areaEffectCloud.basePotionType)) {
                if (areaEffectCloud.persistentDataContainer.has(harmedByKey)) {
                    damager = plugin.server.getPlayer(
                        UUID.fromString(
                            areaEffectCloud.persistentDataContainer.get(
                                harmedByKey,
                                PersistentDataType.STRING
                            )
                        )
                    )
                    type = DamageType.HARMED_DAMAGE
                }
            }
        } else if (event.damager is Projectile) {
            // by projectile
            val projectile = event.damager as Projectile
            if (projectile.shooter !is Player) return
            damager = projectile.shooter as Player
            if (projectile is AbstractArrow) {
                if (projectile is Trident) {
                    type = DamageType.TRIDENT_NORMAL
                } else {
                    type = DamageType.ARROW_NORMAL
                    if (event.isCritical) { // critical
                        type = DamageType.ARROW_CRITICAL
                    }
                    if (projectile.fireTicks != 0) { // fire
                        setEntityMarked(entity, damager, 100, burnedByKey)
                    }
                    if (projectile is Arrow) { // effects
                        if (isPoison(projectile.basePotionType)) {
                            setEntityMarked(entity, damager, getEffectDuration(projectile.basePotionType, PotionEffectType.POISON), poisonedByKey)
                        }
                    }
                }
            } else if (projectile is ThrownPotion) {
                val potion = projectile as ThrownPotion
                potion.effects.find { it.type == PotionEffectType.HARM }?.let {
                    type = DamageType.HARMED_DAMAGE
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPotionSplash(event: PotionSplashEvent) {
        val shooter = event.potion.shooter
        if (shooter !is Player) return

        event.potion.effects.find { it.type == org.bukkit.potion.PotionEffectType.POISON }?.let {
            event.affectedEntities.forEach { entity ->
                if (entity is LivingEntity) {
                    setEntityMarked(entity, shooter, it.duration, poisonedByKey)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onLingeringPotionSplash(event: LingeringPotionSplashEvent) {
        val shooter = event.entity.shooter
        if (shooter !is Player) return
        val potionType = event.areaEffectCloud.basePotionType; // todo check all potions effects (not base-effect only)
        if (isPoison(potionType)) {
            event.areaEffectCloud.persistentDataContainer.set(
                poisonedByKey,
                PersistentDataType.STRING,
                shooter.uniqueId.toString()
            )
        } else if (isHarming(potionType)) {
            event.areaEffectCloud.persistentDataContainer.set(
                harmedByKey,
                PersistentDataType.STRING,
                shooter.uniqueId.toString()
            )
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onAreaEffectCloudApply(event: AreaEffectCloudApplyEvent) {
        val container = event.entity.persistentDataContainer
        if (!container.has(poisonedByKey)) return
        val damager = plugin.server.getPlayer(
            UUID.fromString(
                container.get(
                    poisonedByKey,
                    PersistentDataType.STRING
                )
            )
        ) ?: return
        event.entity.basePotionType.potionEffects.find { it.type == org.bukkit.potion.PotionEffectType.POISON }
            ?.let {
                event.affectedEntities.forEach { entity ->
                    if (entity is LivingEntity) {
                        setEntityMarked(
                            entity,
                            damager,
                            it.duration,
                            poisonedByKey
                        )
                    }
                }
            }
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

    // Effect utilities

    private fun isPoison(type: PotionType): Boolean {
        return type == PotionType.POISON || type == PotionType.LONG_POISON || type == PotionType.STRONG_POISON
    }

    private fun isHarming(type: PotionType): Boolean {
        return type == PotionType.INSTANT_DAMAGE || type == PotionType.STRONG_HARMING
    }

    private fun getEffectDuration(
        potionType: PotionType,
        potionEffectType: PotionEffectType
    ): Int {
        potionType.potionEffects.find { it.type == potionEffectType }
            ?.let {
                return it.duration
            }
        return 0
    }
}