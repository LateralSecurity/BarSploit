package com.lateralsecurity.barsploit

import android.app.Activity
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class Print(private val activity: Activity) {
    private var mWebView: WebView? = null

    fun printHtml(html: String) {
        val webView = WebView(activity)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false

            override fun onPageFinished(view: WebView, url: String) {
                createWebPrintJob(view)
                mWebView = null
            }
        }

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        mWebView = webView
    }

    fun createWebPrintJob(webView: WebView) {
        (activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

            val jobName = "Barcode List"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            ).also { printJob ->
            }
        }
    }

}