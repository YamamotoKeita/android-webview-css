package com.example.webviewcss

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    Box(contentAlignment = Alignment.BottomCenter) {
                        MyWebView()
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = { setBody(TestData[0]) }) {
                                Text("本文1")
                            }
                            Button(onClick = { setBody(TestData[1]) }) {
                                Text("本文2")
                            }
                            Button(onClick = { setBody(TestData[2]) }) {
                                Text("本文3")
                            }
                        }
                    }
                }
            }
        }
        
        reserveBody(TestData[0])
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun MyWebView() {
        AndroidView(
            modifier = Modifier.fillMaxHeight(),
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
        }
    }
}
