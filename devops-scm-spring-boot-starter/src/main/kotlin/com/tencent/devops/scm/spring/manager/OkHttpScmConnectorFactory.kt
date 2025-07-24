package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.sdk.common.connector.ScmConnector
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector
import com.tencent.devops.scm.spring.properties.HttpClientProperties
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class OkHttpScmConnectorFactory(
    private val metricsEventListener: OkHttpMetricsEventListener?
) : ScmConnectorFactory {

    private val connectorCache = ConcurrentHashMap<String, ScmConnector>()
    private val propertiesCache = ConcurrentHashMap<String, HttpClientProperties>()

    override fun create(properties: HttpClientProperties): ScmConnector {
        val apiUrl = properties.apiUrl ?: throw IllegalArgumentException("api url is required")

        val cachedProps = propertiesCache[apiUrl]
        if (properties == cachedProps) {
            return connectorCache[apiUrl] ?: throw IllegalStateException("Connector not found for $apiUrl")
        }
        
        val connector = createConnector(properties)
        connectorCache[apiUrl] = connector
        propertiesCache[apiUrl] = properties
        return connector
    }

    private fun createConnector(properties: HttpClientProperties): ScmConnector {
        return OkHttpClient.Builder().apply {
            properties.connectTimeout?.let { 
                connectTimeout(it, TimeUnit.SECONDS)
            }
            properties.readTimeout?.let { 
                readTimeout(it, TimeUnit.SECONDS)
            }
            properties.writeTimeout?.let { 
                writeTimeout(it, TimeUnit.SECONDS)
            }
            metricsEventListener?.let { 
                eventListener(it) 
            }
        }.build().let { OkHttpScmConnector(it) }
    }
}
