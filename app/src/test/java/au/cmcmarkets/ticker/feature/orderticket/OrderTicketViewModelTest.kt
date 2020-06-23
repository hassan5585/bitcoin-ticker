package au.cmcmarkets.ticker.feature.orderticket

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import au.cmcmarkets.ticker.data.model.TickerCurrencyResponse
import au.cmcmarkets.ticker.data.model.TickerResponse
import au.cmcmarkets.ticker.data.repository.IBitCoinRepository
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class OrderTicketViewModelTest {

    private companion object {
        const val PING_INTERVAL_IN_SECS = 15L
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val bitcoinRepository: IBitCoinRepository = mock(IBitCoinRepository::class.java)

    private val decimalFormat: NumberFormat by lazy {
        DecimalFormat("###.##")
    }

    private lateinit var viewModel: OrderTicketViewModel

    private val emptyTickerResponse: TickerResponse = TickerResponse()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        viewModel = OrderTicketViewModel(bitcoinRepository)
    }

    @Test
    fun `given the repository when the onResume method is called viewmodel should subscribe to observable`() {
        val observable = Observable.just(emptyTickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        assertThat(viewModel.disposableBag.size(), `is`(1))
    }

    @Test
    fun `given the repository when the onPause method is called viewmodel should unsubscribe to observable`() {
        val observable = Observable.just(emptyTickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onPause()
        assertThat(viewModel.disposableBag.size(), `is`(0))
    }

    @Test
    fun `given no price set bitcoin changing the amount value should do nothing`() {
        val observable = Observable.just(emptyTickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        viewModel.setAmount("10")
        assertThat(viewModel.unitsLiveData.value, `is`(nullValue()))
    }

    @Test
    fun `given no price set bitcoin changing the units value should do nothing`() {
        val observable = Observable.just(emptyTickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        viewModel.setUnits("10")
        assertThat(viewModel.amountLiveData.value, `is`(nullValue()))
    }

    @Test
    fun `given bitcoin price set the livedata should emit true`() {
        val tickerResponse = getTickerWithCorrectCurrency()
        val observable = Observable.just(tickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        Thread.sleep(1000)
        assertThat(viewModel.isPriceReadyLiveData.value, `is`(true))
    }

    @Test
    fun `given the ticker is subscribed to it should get fresh data every 15 seconds`() {
        val amountOfPingsToTest = 3
        val tickerResponse = getTickerWithCorrectCurrency()
        val observable = Observable.just(tickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        val timeToWaitInSecs = (PING_INTERVAL_IN_SECS * amountOfPingsToTest) + 1
        val testObserver = TestObserver<TickerResponse>()
        viewModel.tickerObservable.subscribe(testObserver)
        Thread.sleep(TimeUnit.SECONDS.toMillis(timeToWaitInSecs))
        testObserver.assertValueCount(amountOfPingsToTest + 1) // Since 1st ping is done as soon as it subscribes
    }

    @Test
    fun `given the units are set the livedata should provide the formatted amount`() {
        val amountObserver = Observer<String> {}
        viewModel.amountLiveData.observeForever(amountObserver)
        val tickerResponse = getTickerWithCorrectCurrency()
        val observable = Observable.just(tickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        Thread.sleep(1000)
        viewModel.setUnits("10")
        assertThat(viewModel.amountLiveData.value, `is`(notNullValue()))
        assertThat(viewModel.amountLiveData.value, `is`(decimalFormat.format(BigDecimal(100.00))))
        viewModel.amountLiveData.removeObserver(amountObserver)
    }

    @Test
    fun `given the amount is set the livedata should provide the formatted units`() {
        val amountObserver = Observer<String> {}
        viewModel.unitsLiveData.observeForever(amountObserver)
        val tickerResponse = getTickerWithCorrectCurrency()
        val observable = Observable.just(tickerResponse)
        `when`(bitcoinRepository.getTickerPrices()).thenReturn(observable)
        viewModel.onResume()
        Thread.sleep(1000)
        viewModel.setAmount("10")
        assertThat(viewModel.unitsLiveData.value, `is`(notNullValue()))
        assertThat(viewModel.unitsLiveData.value, `is`(decimalFormat.format(BigDecimal(1))))
        viewModel.unitsLiveData.removeObserver(amountObserver)
    }

    private fun getTickerWithCorrectCurrency(): TickerResponse {
        val tickerCurrencyResponse = TickerCurrencyResponse(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "$")
        return TickerResponse().apply {
            put("GBP", tickerCurrencyResponse)
        }
    }
}