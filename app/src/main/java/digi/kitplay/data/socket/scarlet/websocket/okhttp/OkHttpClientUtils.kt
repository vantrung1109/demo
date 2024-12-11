/*
 * © 2018 Match Group, LLC.
 */

@file:JvmName("OkHttpClientUtils")

package digi.kitplay.data.socket.scarlet.websocket.okhttp

import com.tinder.scarlet.WebSocket
import digi.kitplay.data.socket.scarlet.websocket.okhttp.request.RequestFactory
import digi.kitplay.data.socket.scarlet.websocket.okhttp.request.StaticUrlRequestFactory
import okhttp3.OkHttpClient

fun OkHttpClient.newWebSocketFactory(requestFactory: RequestFactory): WebSocket.Factory {
    return OkHttpWebSocket.Factory(OkHttpClientWebSocketConnectionEstablisher(this, requestFactory))
}

fun OkHttpClient.newWebSocketFactory(url: String): WebSocket.Factory {
    return newWebSocketFactory(StaticUrlRequestFactory(url))
}