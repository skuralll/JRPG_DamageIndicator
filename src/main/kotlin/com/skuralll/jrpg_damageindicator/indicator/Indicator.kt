package com.skuralll.jrpg_damageindicator.indicator

import com.skuralll.jrpg_damageindicator.packet.metadata.ITextDisplayMetadata.Companion.alphaToByte
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class Indicator(
    private val plugin: Plugin,
    private val player: Player,
    vector: Vector,
    damageType: DamageType,
    private val damage: Double,
) :
    Vector(vector.x, vector.y, vector.z) {

    companion object {
        // Entity ID counter
        val ENTITY_COUNTER = AtomicInteger(-Random().nextInt())
    }

    // EntityDisplay
    private val entity: TextDisplay =
        player.world.spawnEntity(
            Location(player.world, x, y, z),
            EntityType.TEXT_DISPLAY
        ) as TextDisplay

    init {
        // set entity initial properties
        entity.let {
            it.isPersistent = false
            it.isVisibleByDefault = false
            it.interpolationDelay = 0
            it.interpolationDuration = 1
            it.teleportDuration = 1
            it.brightness = Display.Brightness(15, 15)
            it.billboard = Display.Billboard.CENTER
            it.text(damageType.toTextComponent(damage))
            it.backgroundColor = Color.fromARGB(0, 0, 0, 0)
            it.textOpacity = alphaToByte(0)
            it.isSeeThrough = true
        }
    }

    // tick count after spawning
    private var tick = AtomicInteger(0)

    // Entity ID
    val entityId = ENTITY_COUNTER.getAndDecrement()

    // spawn entity on client side
    fun spawn() {
        player.showEntity(plugin, entity)
    }

    fun update() {
        // kill if player is offline
        if (!player.isOnline) {
            despawn()
            return;
        }
        val now = tick.incrementAndGet()
        // update process (1~3:fade in, 20: set move up, 21~23: fade out, 25: remove entity
        // TODO : make it configurable
        when (now) {
            in 1..3 -> {
                entity.teleport(entity.location.clone().apply { y += 0.015 })
                entity.textOpacity = alphaToByte(now * 85)
                entity.backgroundColor = Color.fromARGB(20 * now, 0, 0, 0)
            }

            19 -> {
                entity.teleportDuration = 3
            }

            20 -> {
                entity.teleport(entity.location.clone().apply { y += 1 })
            }

            in 21..23 -> {
                entity.textOpacity = alphaToByte(255 - (now - 20) * 85)
                entity.backgroundColor = Color.fromARGB(60 - (now - 20) * 20, 0, 0, 0)
            }

            25 -> despawn()
        }
    }

    // despawn entity on client side
    fun despawn() {
        entity.remove()
    }

    // check if entity is alive
    fun isAlive(): Boolean {
        return !entity.isDead
    }

}