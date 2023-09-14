package com.example.webviewcss

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewcss.ui.theme.WebViewCssTheme

class MainActivity : ComponentActivity() {

    var webView: WebView? = null
    private var isHTMLReady = false
    private var afterReady: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewCssTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyWebView()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun MyWebView() {
        AndroidView(
            factory = ::WebView,
            update = { webView ->
                this.webView = webView
                webView.webViewClient = webViewClient
                webView.settings.javaScriptEnabled = true
                loadBaseHTML()
            }
        )
    }

    fun loadBaseHTML() {
        webView?.loadUrl("file:///android_asset/pr-offer-quest.html")
    }

    fun reserveBody(html: String) {
        if (isHTMLReady) {
            setBody(html)
        } else {
            afterReady = {
                setBody(html)
            }
        }
    }
    fun setBody(html: String) {
        val escaped = escapeForStringLiteral(html)
        val script = "document.body.innerHTML = '$escaped';"
        webView?.evaluateJavascript(script, null)
    }

    fun escapeForStringLiteral(src: String): String {
        val conversion = mapOf(
            "\r" to "",
            "\n" to "\\n",
            "\"" to "\\\"",
            "\'" to "\\'",
            "\t" to "\\t"
        )
        var result = src
        conversion.forEach { (key, value) ->
            result = result.replace(key, value)
        }
        return result
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            isHTMLReady = true
            afterReady?.invoke()
            afterReady = null

            reserveBody(TestData.data[0])
        }
    }
}
