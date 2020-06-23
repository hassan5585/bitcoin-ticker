package au.cmcmarkets.ticker.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import au.cmcmarkets.ticker.utils.SimpleTextChangeListener

class CMCEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) :
        AppCompatEditText(context, attrs, defStyleAttr) {

    private companion object {
        const val DOT = "."
    }

    init {
        addTextChangedListener(object : SimpleTextChangeListener {
            override fun afterTextChanged(s: Editable?) {
                val splitList = s?.split(DOT)
                if (splitList != null) {
                    // Making sure there is only one "." value and values after "." are only 2
                    val dotCount = s.toString().filter { it.toString() == DOT }.count()
                    if ((dotCount > 1) || (splitList.size == 2 && splitList[1].length > 2)) {
                        s.delete(s.length - 1, s.length)
                    }
                }
            }
        })
    }
}