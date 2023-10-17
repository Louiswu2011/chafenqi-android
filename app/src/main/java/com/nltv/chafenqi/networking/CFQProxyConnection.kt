package com.nltv.chafenqi.networking

import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.FileChannel
import java.util.concurrent.TimeUnit

object CFQProxyOutboundConnection: Runnable {
    private const val TAG = "CFQProxyConnectionOutboundThread"

    override fun run() {

    }

}