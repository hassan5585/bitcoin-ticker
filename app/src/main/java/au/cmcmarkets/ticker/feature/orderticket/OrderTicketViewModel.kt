package au.cmcmarkets.ticker.feature.orderticket

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import au.cmcmarkets.ticker.data.repository.IBitCoinRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class OrderTicketViewModel @Inject constructor(private val bitcoinRepository: IBitCoinRepository) : ViewModel(), LifecycleObserver {

    private companion object {
        const val PING_INTERVAL_IN_SECS = 15L
        const val EMPTY_STRING = ""
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val disposableBag by lazy {
        CompositeDisposable()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tickerObservable = Observable.interval(0, PING_INTERVAL_IN_SECS, TimeUnit.SECONDS)
            .flatMap {
                bitcoinRepository.getTickerPrices()
            }

    private val decimalFormat: NumberFormat by lazy {
        DecimalFormat("###.##")
    }

    private val internalAmountLiveData = MutableLiveData<String>()

    private val internalUnitsLiveData = MutableLiveData<String>()

    private val internalIsPriceReadyLiveData = MutableLiveData<Boolean>()

    private val internalBitCoinPriceLiveData = MutableLiveData<BigDecimal>()

    val amountLiveData: LiveData<String> = Transformations.map(internalUnitsLiveData) { newUnitsValueString ->
        if (newUnitsValueString.isBlank()) {
            EMPTY_STRING
        } else {
            internalBitCoinPriceLiveData.value?.let { oldBitCoinValue ->
                val newUnitsValue = BigDecimal(newUnitsValueString)
                val newAmountValue = oldBitCoinValue.multiply(newUnitsValue)
                getFormattedAmountValue(newAmountValue)
            } ?: EMPTY_STRING
        }
    }

    val unitsLiveData: LiveData<String> = Transformations.map(internalAmountLiveData) { newAmountValueString ->
        if (newAmountValueString.isBlank()) {
            EMPTY_STRING
        } else {
            internalBitCoinPriceLiveData.value?.let { oldBitCoinValue ->
                val newAmountValue = BigDecimal(newAmountValueString)
                val newUnitsValue = newAmountValue.divide(oldBitCoinValue, 2, RoundingMode.HALF_UP)
                getFormattedUnitsValue(newUnitsValue)
            } ?: EMPTY_STRING
        }
    }

    val isPriceReadyLiveData: LiveData<Boolean>
        get() = internalIsPriceReadyLiveData

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        disposableBag.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        addDisposable(tickerObservable.subscribe {
            it.getGBPValue()?.buyPrice?.let {
                internalBitCoinPriceLiveData.postValue(it)
                internalIsPriceReadyLiveData.postValue(true)
            } ?: internalIsPriceReadyLiveData.postValue(false)
        })
    }

    fun setUnits(units: String) {
        if (internalIsPriceReadyLiveData.value == true && units != internalUnitsLiveData.value) {
            internalUnitsLiveData.value = units
        }
    }

    fun setAmount(amount: String) {
        if (internalIsPriceReadyLiveData.value == true && amount != internalAmountLiveData.value) {
            internalAmountLiveData.value = amount
        }
    }

    private fun addDisposable(disposable: Disposable) {
        disposableBag.add(disposable)
    }

    private fun getFormattedUnitsValue(units: BigDecimal): String {
        return decimalFormat.format(units)
    }

    private fun getFormattedAmountValue(amount: BigDecimal): String {
        return decimalFormat.format(amount)
    }
}