package com.skuralll.jrpg_damageindicator.indicator

import com.skuralll.jrpg_damageindicator.packet.metadata.ITextDisplayMetadata
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
        const val POINTER_UPDATE_TICK = 1

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
        // set entity properties
        entity.let {
            it.isPersistent = false
            it.isVisibleByDefault = false
            it.interpolationDelay = POINTER_UPDATE_TICK
            it.interpolationDuration = POINTER_UPDATE_TICK
            it.teleportDuration = POINTER_UPDATE_TICK
            it.brightness = Display.Brightness(15, 15)
            it.billboard = Display.Billboard.CENTER
            it.text(damageType.toTextComponent(damage))
            it.backgroundColor = Color.fromARGB(0, 0, 0, 0)
            it.textOpacity = alphaToByte(255)
            // attributes = 0x02.toByte()
        }
    }

    // previous position
    private var prevPos: Vector = clone()

    // tick count after spawning
    private var tick = AtomicInteger(0)

    // Entity ID
    val entityId = ENTITY_COUNTER.getAndDecrement()

    // whether to continue updating
    private var _alive = true
    val alive: Boolean
        get() = _alive

    // Entity metadata
    private var prevMetadata = ITextDisplayMetadata()
    var metadata = ITextDisplayMetadata(
        posInterpolation = 1,
        brightness = Display.Brightness(15, 15),
        billboard = Display.Billboard.CENTER,
        textComponent = damageType.toTextComponent(damage),
        backgroundColor = Color.fromARGB(0, 0, 0, 0),
        textOpacity = alphaToByte(255),
        attributes = 0x02.toByte()
    )

    // show(spawn) entity on client side
    fun show() {
//        packetHandler.sendPacket(
//            player,
//            IPacketSpawnEntity(entityId, UUID.randomUUID(), EntityType.TEXT_DISPLAY, this).build()
//        )
//        updateMetadata()
        player.showEntity(plugin, entity)
    }

    fun update() {
        // kill if player is offline
        if (!player.isOnline) {
            _alive = false
            return;
        }
        val now = tick.incrementAndGet()
        // update process (1~3:fade in, 20: set move up, 21~23: fade out, 25: remove entity
        when (now) {
            in 1..3 -> {
                y += 0.015
                metadata.textOpacity = alphaToByte(now * 85)
                metadata.backgroundColor = Color.fromARGB(20 * now, 0, 0, 0)
            }

            20 -> {
                y += 1
                metadata.posInterpolation = 3
            }

            in 21..23 -> {
                metadata.textOpacity = alphaToByte(255 - (now - 20) * 85)
                metadata.backgroundColor = Color.fromARGB(60 - (now - 20) * 20, 0, 0, 0)
            }

            25 -> _alive = false
        }
        updateMetadata()
        updatePosition()
    }

    // hide(remove) entity on client side
    fun hide() {
//        packetHandler.sendPacket(player, IPacketDestroyEntity(listOf(entityId)).build())
        entity.remove()
    }

    // update metadata and send packet to client
    private fun updateMetadata() {
//        if (prevMetadata != metadata) {
//            packetHandler.sendPacket(
//                player,
//                IPacketSetEntityMetadata(entityId, metadata.build()).build()
//            )
//            prevMetadata = metadata.copy()
//        }
    }

    // update position and send packet to client
    private fun updatePosition() {
//        val currentVector = this.clone()
//        if (prevPos != currentVector) {
//            packetHandler.sendPacket(
//                player,
//                IPacketUpdateEntityPosition(entityId, prevPos, this, false).build()
//            )
//            prevPos = currentVector
//        }
    }

}