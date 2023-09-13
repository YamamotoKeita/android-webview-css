package com.example.webviewcss

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewcss.ui.theme.TestData
import com.example.webviewcss.ui.theme.WebViewCssTheme

class MainActivity : ComponentActivity() {

    var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewCssTheme {
                // A surface container using the 'background' color from the theme
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
                webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                WebView.setWebContentsDebuggingEnabled(true)

                load()
            }
        )
    }

    fun load() {
        val htmlPath = "file:///android_asset/pr-offer-quest.html"
        webView?.loadUrl(htmlPath)
    }

    fun appendStyleSheet(css: String) {
        val escapedCss = escapeForStringLiteral(css)
        var script = "var style = document.createElement('style');"
        script += "style.type = 'text/css';"
        script += "var content = document.createTextNode('${escapedCss}');"
        script += "style.appendChild(content);"
        script += "document.body.appendChild(style);"
        webView?.evaluateJavascript(script, null)
    }

    fun appendBody() {
        val escaped = escapeForStringLiteral(TestData.prOfferBody)
        val script = "document.body.innerHTML = '$escaped';"
        webView?.evaluateJavascript(script, null)
    }

    fun escapeForStringLiteral(src: String): String {
        val stringLiteralConversion = mapOf(
            "\r" to "",
            "\n" to "\\n",
            "\"" to "\\\"",
            "\'" to "\\'",
            "\t" to "\\t"
        )
        var result = src
        stringLiteralConversion.forEach { (key, value) ->
            result = result.replace(key, value)
        }
        return result
    }

    private fun readResourceString(context: Context, fileName: String): String? {
        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            val css = readResourceString(context = this@MainActivity,"pr-offer-quest.css")
            if (css != null) {
                appendStyleSheet(css)
            }
            appendBody()
        }
    }
}
