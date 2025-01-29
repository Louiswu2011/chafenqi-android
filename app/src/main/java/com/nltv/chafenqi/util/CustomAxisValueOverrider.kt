package com.nltv.chafenqi.util

import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.common.data.ExtraStore

class MaimaiAxisValueOverrider: CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        return if (minY - 1.0 < 0) 0.0 else minY - 1.0
    }

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        return if (maxY + 1.0 > 101) 101.0 else maxY + 1.0
    }
}

class ChunithmAxisValueOverrider: CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        return if (minY - 5000.0 < 0) 0.0 else minY - 5000.0
    }

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        return if (maxY + 5000.0 > 1010000) 1010000.0 else maxY + 5000.0
    }
}