package com.example.winecard

import org.junit.Assert.assertEquals
import org.junit.Test

class WineThemeModeTest {
    @Test fun restoresModes() {
        WineThemeMode.entries.forEach { assertEquals(it, WineThemeMode.fromStored(it.name)) }
    }
    @Test fun defaultsToSystem() {
        assertEquals(WineThemeMode.SYSTEM, WineThemeMode.fromStored(null))
        assertEquals(WineThemeMode.SYSTEM, WineThemeMode.fromStored("unknown"))
    }
}
