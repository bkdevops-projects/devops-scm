package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType
import java.io.File

abstract class AbstractBkCodeWebhookParserTest {

    val webhookParser = BkCodeWebhookParser()

    fun readFile(path: String): String {
        val filePath = this::class.java.classLoader.getResource(path)?.file
            ?: throw IllegalStateException("资源文件未找到")
        return File(filePath).readText(Charsets.UTF_8)
    }

    private fun headers(bkCodeEventType: BkCodeEventType) = mapOf(
        "X-Event" to bkCodeEventType.name,
        "x-bkcode-event" to bkCodeEventType.value,
        "X-Source-Type" to "Project"
    )

    fun hookRequest(
        mockFilePath: String,
        bkCodeEventType: BkCodeEventType
    ) = HookRequest(
        headers = headers(bkCodeEventType),
        body = readFile(mockFilePath)
    )

    fun <T> parseWebhook(mockFilePath: String, bkCodeEventType: BkCodeEventType, clazz: Class<T>): T {
        return webhookParser.parse(
            hookRequest(
                mockFilePath = mockFilePath,
                bkCodeEventType = bkCodeEventType
            )
        ) as T
    }
}
