package com.aghajari.content

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Trace
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.aghajari.xmlbypass.XmlByPass
import com.aghajari.xmlbypass.XmlLayout

@XmlByPass(
    layouts = [XmlLayout(layout = "*")]
)
class BenchmarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Trace.beginSection("LayoutInflation")
        val type = intent.getStringExtra(BENCHMARK_KEY)?.let {
            BenchmarkType.valueOf(it)
        }

        when (type) {
            BenchmarkType.COMPOSE -> {
                setContent {
                    Box(modifier = Modifier.fillMaxSize()) { }
                }
            }
            BenchmarkType.XML -> {
                setContentView(R.layout.hello_world)
            }
            BenchmarkType.XML_BY_PASS -> {
                setContentView(hello_world(this))
            }
            BenchmarkType.NOTHING -> {
                setContentView(View(this))
            }
            null -> {
                /* Microbenchmark */
            }
        }
        Trace.endSection()
        reportFullyDrawn()
    }
}

@SuppressLint("StaticFieldLeak")
object ViewCache {
    private var view: View? = null

    fun getView(context: Context): View {
        if (view == null) {
            view = hello_world(context.applicationContext)
        } else {
            (view?.parent as? ViewGroup)?.removeView(view)
        }
        return view!!
    }
}

/*@Composable
fun ComposeHelloWorld() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello World",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}*/