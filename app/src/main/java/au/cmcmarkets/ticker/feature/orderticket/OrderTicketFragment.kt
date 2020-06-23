package au.cmcmarkets.ticker.feature.orderticket

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import au.cmcmarkets.ticker.R
import au.cmcmarkets.ticker.core.di.viewmodel.ViewModelFactory
import au.cmcmarkets.ticker.utils.SimpleTextChangeListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_order_ticket.*
import javax.inject.Inject

class OrderTicketFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OrderTicketViewModel by viewModels { viewModelFactory }

    private val amountTextWatcher = object : SimpleTextChangeListener {
        override fun afterTextChanged(s: Editable?) {
            s?.toString()?.let {
                viewModel.setAmount(it)
            }
        }
    }

    private val unitsTextWatcher = object : SimpleTextChangeListener {
        override fun afterTextChanged(s: Editable?) {
            s?.toString()?.let {
                viewModel.setUnits(it)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order_ticket, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        super.onDestroy()
    }

    private fun setListeners() {
        viewModel.isPriceReadyLiveData.observe(viewLifecycleOwner, Observer { isPriceReady ->
            amountEditText.isEnabled = isPriceReady
            unitsEditText.isEnabled = isPriceReady
        })
        viewModel.unitsLiveData.observe(viewLifecycleOwner, Observer {
            // Distinguish between text added by user and text coming from VM we remove the textwatcher temporarily
            unitsEditText.removeTextChangedListener(unitsTextWatcher)
            unitsEditText.setText(it)
            unitsEditText.addTextChangedListener(unitsTextWatcher)
        })
        viewModel.amountLiveData.observe(viewLifecycleOwner, Observer {
            // Distinguish between text added by user and text coming from VM we remove the textwatcher temporarily
            amountEditText.removeTextChangedListener(amountTextWatcher)
            amountEditText.setText(it)
            amountEditText.addTextChangedListener(amountTextWatcher)
        })
        amountEditText.addTextChangedListener(amountTextWatcher)
        unitsEditText.addTextChangedListener(unitsTextWatcher)
    }

}
