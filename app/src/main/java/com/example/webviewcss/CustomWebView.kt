package com.example.webviewcss

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.webviewcss.ui.theme.TestData

class CustomWebView(val context: Context, val webView: WebView?) {

    fun load() {
        val htmlPath = "file:///android_asset/pr-offer-quest.html"
        webView?.loadUrl(htmlPath)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView(webView: WebView) {
        webView.webViewClient = webViewClient
        webView.settings.javaScriptEnabled = true
        // webViewのコンテンツがhttps、headタグに埋め込むcssがhttpであるため設定
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        WebView.setWebContentsDebuggingEnabled(true)
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

    private fun readResourceString(fileName: String): String? {
        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            val css = readResourceString("pr-offer-quest.css")
            if (css != null) {
                appendStyleSheet(css)
            }
            appendBody()
        }
    }
}