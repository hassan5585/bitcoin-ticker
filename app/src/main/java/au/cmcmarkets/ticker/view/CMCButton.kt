package au.cmcmarkets.ticker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import au.cmcmarkets.ticker.R

class CMCButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                          defStyleAttr: Int = android.R.attr.buttonStyle) : AppCompatButton(context, attrs, defStyleAttr) {

    private val overlayDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(context, R.drawable.overlay_button)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isActivated) {
            overlayDrawable?.apply {
                setBounds(0, 0, width, height)
            }?.draw(canvas!!)
        }
    }
}