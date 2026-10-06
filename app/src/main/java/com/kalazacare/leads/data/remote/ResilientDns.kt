package com.kalazacare.leads.data.remote

import android.util.Log
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.net.UnknownHostException

private const val TAG = "ResilientDns"

/**
 * Finds the Supabase server even when the phone's mobile network won't.
 *
 * Indian carriers have DNS-blocked `*.supabase.co` before (Jio/Airtel/ACT, Feb 2026), and on some
 * phones the app still showed "Can't reach the server" while it worked on others. A blocked
 * lookup can fail outright or return a wrong ("sinkhole") address, so for Supabase hosts this asks
 * an encrypted DNS service first (DNS-over-HTTPS to Cloudflare, then Google -- both reached by
 * fixed IPs, so the carrier's DNS is never involved) and only falls back to the phone's own DNS
 * if both are unreachable. Every other host uses the phone's DNS as normal.
 */
object ResilientDns : Dns {

    private val bootstrapClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    private val providers: List<DnsOverHttps> by lazy {
        listOf(
            DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(InetAddress.getByName("1.1.1.1"), InetAddress.getByName("1.0.0.1"))
                .build(),
            DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url("https://dns.google/dns-query".toHttpUrl())
                .bootstrapDnsHosts(InetAddress.getByName("8.8.8.8"), InetAddress.getByName("8.8.4.4"))
                .build(),
        )
    }

    override fun lookup(hostname: String): List<InetAddress> {
        if (!hostname.endsWith(".supabase.co")) return Dns.SYSTEM.lookup(hostname)

        for (provider in providers) {
            try {
                val addresses = provider.lookup(hostname)
                if (addresses.isNotEmpty()) return addresses
            } catch (e: Exception) {
                Log.w(TAG, "DNS-over-HTTPS lookup failed for $hostname, trying next", e)
            }
        }
        Log.w(TAG, "Encrypted DNS unreachable for $hostname, using the phone's DNS")
        return try {
            Dns.SYSTEM.lookup(hostname)
        } catch (e: UnknownHostException) {
            throw UnknownHostException(
                "Could not look up $hostname (encrypted DNS and the phone's DNS both failed)",
            ).apply { initCause(e) }
        }
    }
}
