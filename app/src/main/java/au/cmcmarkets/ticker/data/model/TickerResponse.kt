package au.cmcmarkets.ticker.data.model

class TickerResponse : HashMap<String, TickerCurrencyResponse>() {
    fun getGBPValue(): TickerCurrencyResponse? = get("GBP")
}