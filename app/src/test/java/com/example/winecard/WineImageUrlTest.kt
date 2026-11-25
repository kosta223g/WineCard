package com.example.winecard

import org.junit.Assert.assertEquals
import org.junit.Test

class WineImageUrlTest {
    @Test fun upgradesHttp() = assertEquals("https://images.vivino.com/thumbs/a.jpg", normalizeWineImageUrl(" http://images.vivino.com/thumbs/a.jpg "))
    @Test fun handlesRelative() = assertEquals("https://images.vivino.com/thumbs/a.jpg", normalizeWineImageUrl("/thumbs/a.jpg"))
    @Test fun handlesProtocolRelative() = assertEquals("https://images.vivino.com/thumbs/a.jpg", normalizeWineImageUrl("//images.vivino.com/thumbs/a.jpg"))
    @Test fun emptyAndInvalid() {
        assertEquals("", normalizeWineImageUrl(null))
        assertEquals("", normalizeWineImageUrl("not a URL"))
        assertEquals("", normalizeWineImageUrl("file:///test.jpg"))
    }
}
