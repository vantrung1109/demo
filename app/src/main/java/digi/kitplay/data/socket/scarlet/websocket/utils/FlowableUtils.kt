/*
 * © 2018 Match Group, LLC.
 */

@file:JvmName("FlowableUtils")

package digi.kitplay.data.socket.scarlet.websocket.utils

import io.reactivex.rxjava3.core.Flowable

fun <T : Any> Flowable<T>.toStream() = FlowableStream(this)
