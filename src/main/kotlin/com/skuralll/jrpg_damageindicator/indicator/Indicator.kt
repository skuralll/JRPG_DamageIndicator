package com.skuralll.jrpg_damageindicator.indicator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class Indicator(private val protocolManager: ProtocolManager, private val player: Player, private val vector: Vector) : Vector(vector.x, vector.y, vector.z) {

    // tick count after spawning
    private var tick = AtomicInteger(0)

    // Entity ID
    companion object {
        val ENTITY_COUNTER  = AtomicInteger(-Random().nextInt())
    }
    val entityId = ENTITY_COUNTER.getAndDecrement()

    // whether to continue updating
    private var _alive = true
    val alive: Boolean
        get() = _alive

    fun show(){
        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.write(0, entityId)
        packet.uuiDs.write(0, UUID.randomUUID())
        packet.entityTypeModifier.write(0, EntityType.FIREBALL)
        packet.doubles
            .write(0, x)
            .write(1, y)
            .write(2, z);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
            player.sendMessage("show: $entityId")
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun update() {
        if(!player.isOnline){
            _alive = false
            return;
        }
        val now = tick.incrementAndGet()
        if (now > 20) {
            _alive = false
            return
        }
    }

    fun hide() {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        packet.intLists.write(0, listOf(entityId))
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
            player.sendMessage("hide: $entityId")
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

}