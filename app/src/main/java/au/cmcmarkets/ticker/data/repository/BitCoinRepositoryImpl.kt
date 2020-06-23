package au.cmcmarkets.ticker.data.repository

import au.cmcmarkets.ticker.data.api.BitcoinApi
import au.cmcmarkets.ticker.data.model.TickerResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class BitCoinRepositoryImpl @Inject constructor(private val bitcoinService: BitcoinApi) : IBitCoinRepository {

    override fun getTickerPrices(): Observable<TickerResponse> = bitcoinService.getTickerPrices()
            .subscribeOn(Schedulers.io())
            .toObservable()

}