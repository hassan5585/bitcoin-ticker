package au.cmcmarkets.ticker.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Override methods you are interested in. The rest will have empty implementations
 */
interface SimpleTextChangeListener: TextWatcher {
    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}