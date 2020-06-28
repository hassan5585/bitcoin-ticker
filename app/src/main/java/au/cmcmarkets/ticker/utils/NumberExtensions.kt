package au.cmcmarkets.ticker.utils

fun Double?.isGreaterThanZero(): Boolean {
    if (this == null) {
        return false
    }
    return this > 0
}