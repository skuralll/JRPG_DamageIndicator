package com.skuralll.jrpg_damageindicator.packet.metadata

import com.comphenix.protocol.wrappers.WrappedDataValue

abstract class IMetadata {
    abstract fun build(): List<WrappedDataValue>
}