package com.skuralll.jrpg_damageindicator.indicator

import com.comphenix.protocol.ProtocolManager
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Indicator(private val protocolManager: ProtocolManager, private val player: Player, private val vector: Vector) : Vector(vector.x, vector.y, vector.z) {

    // whether to continue updating
    private var _alive = true
    val alive: Boolean
        get() = _alive

    fun show(){

    }

    fun update() {
        if(!player.isOnline){
            _alive = false
            return;
        }
    }

    fun hide() {

    }

}