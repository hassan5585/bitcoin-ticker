package au.cmcmarkets.ticker.data.repository

import au.cmcmarkets.ticker.data.model.TickerResponse
import io.reactivex.Observable

interface IBitCoinRepository {
    fun getTickerPrices(): Observable<TickerResponse>
}