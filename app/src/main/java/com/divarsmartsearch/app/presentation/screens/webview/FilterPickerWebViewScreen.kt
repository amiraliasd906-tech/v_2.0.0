package com.divarsmartsearch.app.presentation.screens.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/** Divar's own search page for the city we start filter-picking from. */
private const val FILTER_PICKER_START_URL = "https://divar.ir/s/mashhad"

/**
 * A plain, fully-interactive WebView pointed at Divar's real search page.
 *
 * Unlike [DivarWebViewScreen] (which drives an auto-scroll + extraction loop
 * to passively scan an already-saved search), this screen does nothing on
 * its own: no auto-scroll, no JS extraction, no pause/resume bot toggle.
 * Every tap goes straight to the page, exactly like a normal browser, so the
 * user can freely tap through Divar's real filter UI (price, area,
 * neighborhood, etc.) themselves.
 *
 * A single button below the page reads whatever URL the WebView is
 * currently on -- which is Divar's own URL, already updated with whatever
 * filters the user picked -- and hands it back to the caller via [onDone].
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPickerWebViewScreen(
    onDone: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(FILTER_PICKER_START_URL) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("انتخاب فیلتر از دیوار") })
        },
        bottomBar = {
            Button(
                onClick = { onDone(webViewRef?.url ?: currentUrl) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(text = "  ثبت این لینک و بازگشت")
            }
        },
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    // No custom bridge, no auto-scroll, no extraction timer:
                    // this WebView just behaves like a normal browser so the
                    // user can tap anywhere on Divar's real filter UI.
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            currentUrl = url ?: currentUrl
                        }
                    }

                    loadUrl(FILTER_PICKER_START_URL)
                    webViewRef = this
                }
            },
        )
    }
}
