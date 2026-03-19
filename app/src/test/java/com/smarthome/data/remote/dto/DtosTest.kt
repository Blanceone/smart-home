package com.smarthome.data.remote.dto

import org.junit.Assert.*
import org.junit.Test

class DtosTest {

    @Test
    fun `UserDto should have correct properties`() {
        val user = UserDto(
            id = "user-123",
            deviceId = "device-abc",
            nickname = "测试用户",
            avatar = "https://example.com/avatar.jpg",
            createdAt = "2026-03-17T10:00:00Z"
        )
        
        assertEquals("user-123", user.id)
        assertEquals("device-abc", user.deviceId)
        assertEquals("测试用户", user.nickname)
        assertEquals("https://example.com/avatar.jpg", user.avatar)
    }

    @Test
    fun `UserDto should handle optional fields`() {
        val user = UserDto(
            id = "user-456",
            deviceId = "device-def",
            nickname = "匿名用户",
            avatar = null,
            createdAt = null
        )
        
        assertEquals("user-456", user.id)
        assertNull(user.avatar)
        assertNull(user.createdAt)
    }

    @Test
    fun `DeviceDto should handle optional fields`() {
        val deviceWithAllFields = DeviceDto(
            id = "device-1",
            name = "智能灯",
            brand = "小米",
            category = "照明",
            price = 199.0,
            originalPrice = 249.0,
            description = "智能LED灯",
            features = listOf("WiFi", "语音控制"),
            specifications = mapOf("功率" to "10W"),
            applicableScenes = listOf("客厅", "卧室"),
            imageUrl = "https://example.com/light.jpg",
            images = listOf("https://example.com/light1.jpg"),
            taobaoUrl = "https://item.taobao.com/123",
            priceUpdatedAt = "2026-03-17T10:00:00Z"
        )
        
        val deviceWithMinimalFields = DeviceDto(
            id = "device-2",
            name = "智能插座",
            brand = null,
            category = "插座",
            price = 59.0,
            originalPrice = null,
            description = null,
            features = null,
            specifications = null,
            applicableScenes = null,
            imageUrl = null,
            images = null,
            taobaoUrl = null,
            priceUpdatedAt = null
        )
        
        assertNotNull(deviceWithAllFields.features)
        assertNull(deviceWithMinimalFields.features)
        assertEquals(2, deviceWithAllFields.features?.size)
        assertEquals(249.0, deviceWithAllFields.originalPrice, 0.0)
    }

    @Test
    fun `SchemeDto should calculate device count correctly`() {
        val scheme = SchemeDto(
            id = "scheme-1",
            name = "测试方案",
            budget = 10000.0,
            totalPrice = 9500.0,
            status = "completed",
            decorationGuide = null,
            devices = listOf(
                SchemeDeviceDto(
                    id = "d1", name = "设备1", brand = "品牌1", category = "分类1",
                    price = 100.0, quantity = 2, description = null, recommendReason = null,
                    imageUrl = null, taobaoUrl = null
                ),
                SchemeDeviceDto(
                    id = "d2", name = "设备2", brand = "品牌2", category = "分类2",
                    price = 200.0, quantity = 1, description = null, recommendReason = null,
                    imageUrl = null, taobaoUrl = null
                )
            )
        )
        
        assertEquals(2, scheme.devices.size)
        assertEquals(10000.0, scheme.budget, 0.0)
        assertEquals(9500.0, scheme.totalPrice, 0.0)
        assertEquals("completed", scheme.status)
    }

    @Test
    fun `SchemeDto should handle null decoration guide`() {
        val scheme = SchemeDto(
            id = "scheme-2",
            name = "简单方案",
            budget = 5000.0,
            totalPrice = 4800.0,
            status = "completed",
            decorationGuide = null,
            devices = emptyList()
        )
        
        assertNull(scheme.decorationGuide)
        assertTrue(scheme.devices.isEmpty())
    }

    @Test
    fun `DecorationGuideDto should contain all sections`() {
        val guide = DecorationGuideDto(
            summary = "智能家居整体方案",
            professionalAdvice = "建议先安装基础设备",
            rooms = listOf(
                RoomGuideDto(name = "客厅", advice = "安装智能灯具和窗帘"),
                RoomGuideDto(name = "卧室", advice = "安装智能窗帘和传感器")
            )
        )
        
        assertEquals("智能家居整体方案", guide.summary)
        assertEquals("建议先安装基础设备", guide.professionalAdvice)
        assertEquals(2, guide.rooms?.size)
    }

    @Test
    fun `RoomGuideDto should store room advice`() {
        val room = RoomGuideDto(
            name = "厨房",
            advice = "建议安装燃气传感器"
        )
        
        assertEquals("厨房", room.name)
        assertEquals("建议安装燃气传感器", room.advice)
    }

    @Test
    fun `ApiResponse should handle success and error`() {
        val successResponse = ApiResponse(
            code = 0,
            message = "成功",
            data = "test data"
        )
        
        val errorResponse = ApiResponse<String>(
            code = 1001,
            message = "错误",
            data = null
        )
        
        assertTrue(successResponse.isSuccess)
        assertFalse(errorResponse.isSuccess)
        assertEquals("test data", successResponse.data)
        assertNull(errorResponse.data)
    }

    @Test
    fun `FeedbackResponse should contain feedback info`() {
        val response = FeedbackResponse(
            id = "feedback-1",
            type = "suggestion",
            content = "建议增加更多品牌",
            contact = "test@example.com",
            createdAt = "2026-03-17T10:00:00Z"
        )
        
        assertEquals("feedback-1", response.id)
        assertEquals("suggestion", response.type)
        assertEquals("建议增加更多品牌", response.content)
        assertEquals("test@example.com", response.contact)
    }

    @Test
    fun `DeviceItemDto should have correct structure`() {
        val item = DeviceItemDto(
            id = "device-item-1",
            name = "智能摄像头",
            brand = "海康威视",
            category = "安防",
            price = 299.0,
            imageUrl = "https://example.com/cam.jpg"
        )
        
        assertEquals("device-item-1", item.id)
        assertEquals("智能摄像头", item.name)
        assertEquals(299.0, item.price, 0.0)
    }

    @Test
    fun `PaginationDto should handle pagination metadata`() {
        val pagination = PaginationDto(
            page = 2,
            pageSize = 20,
            total = 100,
            totalPages = 5
        )
        
        assertEquals(2, pagination.page)
        assertEquals(20, pagination.pageSize)
        assertEquals(100, pagination.total)
        assertEquals(5, pagination.totalPages)
    }

    @Test
    fun `ExportDto should contain export information`() {
        val export = ExportDto(
            pdfUrl = "https://api.example.com/export/scheme-123.pdf",
            fileName = "智能家居方案_20260317.pdf",
            expiresAt = "2026-03-18T10:00:00Z"
        )
        
        assertEquals("https://api.example.com/export/scheme-123.pdf", export.pdfUrl)
        assertEquals("智能家居方案_20260317.pdf", export.fileName)
        assertEquals("2026-03-18T10:00:00Z", export.expiresAt)
    }

    @Test
    fun `PurchaseUrlDto should contain purchase info`() {
        val purchase = PurchaseUrlDto(
            purchaseUrl = "https://s.click.taobao.com/abc123",
            price = 199.0,
            originalPrice = 249.0,
            coupon = null
        )
        
        assertEquals("https://s.click.taobao.com/abc123", purchase.purchaseUrl)
        assertEquals(199.0, purchase.price, 0.0)
        assertEquals(249.0, purchase.originalPrice, 0.0)
        assertNull(purchase.coupon)
    }

    @Test
    fun `CouponDto should handle coupon details`() {
        val coupon = CouponDto(
            amount = 20.0,
            condition = "满100可用",
           有效期 = "2026-03-31"
        )
        
        assertEquals(20.0, coupon.amount, 0.0)
        assertEquals("满100可用", coupon.condition)
        assertEquals("2026-03-31", coupon.有效期)
    }

    @Test
    fun `SchemeItemDto should contain basic scheme info`() {
        val item = SchemeItemDto(
            id = "scheme-item-1",
            name = "我的智能方案",
            budget = 10000.0,
            totalPrice = 9500.0,
            deviceCount = 8,
            createdAt = "2026-03-15T10:30:00Z"
        )
        
        assertEquals("scheme-item-1", item.id)
        assertEquals("我的智能方案", item.name)
        assertEquals(10000.0, item.budget, 0.0)
        assertEquals(8, item.deviceCount)
    }

    @Test
    fun `SchemeListDto should contain scheme list`() {
        val list = SchemeListDto(
            schemes = listOf(
                SchemeItemDto(
                    id = "1", name = "方案1", budget = 10000.0,
                    totalPrice = 9500.0, deviceCount = 8,
                    createdAt = "2026-03-15T10:00:00Z"
                ),
                SchemeItemDto(
                    id = "2", name = "方案2", budget = 15000.0,
                    totalPrice = 14000.0, deviceCount = 12,
                    createdAt = "2026-03-14T10:00:00Z"
                )
            ),
            total = 2
        )
        
        assertEquals(2, list.schemes.size)
        assertEquals(2, list.total)
        assertEquals("方案1", list.schemes[0].name)
    }
}
