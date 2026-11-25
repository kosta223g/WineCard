package com.example.winecard

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.SocketTimeoutException

class WineLoadErrorTest {
    @Test fun noNetwork() = assertEquals(WineLoadError.NoNetwork, classifyWineError(SocketTimeoutException(), false))
    @Test fun serverUnavailable() = assertEquals(WineLoadError.Server, classifyWineError(SocketTimeoutException(), true))
    @Test fun invalidFormat() = assertEquals(WineLoadError.Format, classifyWineError(SerializationException("invalid"), true))
    @Test(expected = SerializationException::class)
    fun missingCategoryIsNotEmpty() { WineDatasetDto().category("reds") }
    @Test fun emptyCategoryIsValid() { assertEquals(emptyList<WineDto>(), WineDatasetDto(reds = emptyList()).category("reds")) }
}
