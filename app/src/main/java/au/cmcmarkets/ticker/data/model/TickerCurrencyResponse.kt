package au.cmcmarkets.ticker.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TickerCurrencyResponse(
    @SerializedName("buy")
    val buyPrice: BigDecimal,
    @SerializedName("last")
    val lastPrice: BigDecimal,
    @SerializedName("15m")
    val fifteenMinPrice: BigDecimal,
    @SerializedName("sell")
    val sellPrice: BigDecimal,
    @SerializedName("symbol")
    val symbol: String
)