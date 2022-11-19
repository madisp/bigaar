package pink.madis.bigaar.sampleapp

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.activity.ComponentActivity
import pink.madis.bigaar.sample.SomeFile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            layoutParams = MarginLayoutParams(
                MarginLayoutParams.MATCH_PARENT,
                MarginLayoutParams.MATCH_PARENT
            )
            text = SomeFile.foobar()
        })
    }
}