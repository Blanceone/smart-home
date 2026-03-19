package com.smarthome.data.repository

import com.smarthome.data.remote.dto.*
import org.junit.Assert.*
import org.junit.Test

class RequestsTest {

    @Test
    fun `SaveUserInfoRequest should contain all sections`() {
        val request = SaveUserInfoRequest(
            basicInfo = BasicInfoRequest(
                age = "26-30",
                occupation = "上班族",
                familyMembers = listOf("情侣"),
                city = "北京"
            ),
            lifestyle = LifestyleRequest(
                sleepPattern = "晚睡晚起",
                homeActivities = listOf("休闲娱乐"),
                entertainmentHabits = listOf("看电影追剧")
            ),
            deviceExperience = DeviceExperienceRequest(
                knowledgeLevel = "用过一些",
                usedDevices = listOf("智能音箱")
            ),
            aestheticPreference = AestheticPreferenceRequest(
                decorStyle = "现代简约",
                colorPreferences = listOf("白色系")
            ),
            brandPreference = BrandPreferenceRequest(
                preferredBrands = listOf("小米/米家")
            )
        )
        
        assertNotNull(request.basicInfo)
        assertNotNull(request.lifestyle)
        assertNotNull(request.deviceExperience)
        assertNotNull(request.aestheticPreference)
        assertNotNull(request.brandPreference)
    }

    @Test
    fun `BasicInfoRequest should have required fields`() {
        val request = BasicInfoRequest(
            age = "18-25",
            occupation = "学生",
            familyMembers = listOf("独居"),
            city = "上海"
        )
        
        assertEquals("18-25", request.age)
        assertEquals("学生", request.occupation)
        assertEquals(1, request.familyMembers.size)
        assertEquals("上海", request.city)
    }

    @Test
    fun `LifestyleRequest should have optional fields`() {
        val request = LifestyleRequest(
            sleepPattern = "早睡早起",
            homeActivities = listOf("工作学习", "健身运动"),
            entertainmentHabits = listOf("阅读")
        )
        
        assertEquals("早睡早起", request.sleepPattern)
        assertEquals(2, request.homeActivities.size)
        assertEquals(1, request.entertainmentHabits.size)
    }

    @Test
    fun `DeviceExperienceRequest should store knowledge and devices`() {
        val request = DeviceExperienceRequest(
            knowledgeLevel = "非常熟悉",
            usedDevices = listOf("智能音箱", "智能灯具", "智能门锁", "扫地机器人")
        )
        
        assertEquals("非常熟悉", request.knowledgeLevel)
        assertEquals(4, request.usedDevices.size)
    }

    @Test
    fun `AestheticPreferenceRequest should store style and colors`() {
        val request = AestheticPreferenceRequest(
            decorStyle = "北欧风",
            colorPreferences = listOf("灰色系", "原木色")
        )
        
        assertEquals("北欧风", request.decorStyle)
        assertEquals(2, request.colorPreferences.size)
    }

    @Test
    fun `BrandPreferenceRequest should store brands`() {
        val request = BrandPreferenceRequest(
            preferredBrands = listOf("小米/米家", "华为", "Apple HomeKit")
        )
        
        assertEquals(3, request.preferredBrands.size)
    }

    @Test
    fun `SaveHouseLayoutRequest should contain house info`() {
        val request = SaveHouseLayoutRequest(
            houseType = "三居室",
            totalArea = 120.0,
            rooms = listOf(
                RoomRequest(name = "客厅", area = 35.0, specialNeeds = null),
                RoomRequest(name = "主卧", area = 20.0, specialNeeds = "需要智能窗帘")
            )
        )
        
        assertEquals("三居室", request.houseType)
        assertEquals(120.0, request.totalArea, 0.0)
        assertEquals(2, request.rooms.size)
    }

    @Test
    fun `RoomRequest should store room details`() {
        val request = RoomRequest(
            name = "书房",
            area = 15.0,
            specialNeeds = "需要智能照明"
        )
        
        assertEquals("书房", request.name)
        assertEquals(15.0, request.area, 0.0)
        assertEquals("需要智能照明", request.specialNeeds)
    }

    @Test
    fun `RegisterRequest should contain device id`() {
        val request = RegisterRequest(
            deviceId = "a1b2c3d4e5f6",
            nickname = "测试用户",
            avatar = null
        )
        
        assertEquals("a1b2c3d4e5f6", request.deviceId)
        assertEquals("测试用户", request.nickname)
        assertNull(request.avatar)
    }

    @Test
    fun `GenerateSchemeRequest should contain budget`() {
        val request = GenerateSchemeRequest(
            budget = 10000.0,
            regenerate = false
        )
        
        assertEquals(10000.0, request.budget, 0.0)
        assertFalse(request.regenerate)
    }

    @Test
    fun `GenerateSchemeRequest should support regenerate flag`() {
        val request = GenerateSchemeRequest(
            budget = 15000.0,
            regenerate = true
        )
        
        assertTrue(request.regenerate)
    }

    @Test
    fun `SubmitRatingRequest should contain rating and content`() {
        val request = SubmitRatingRequest(
            schemeId = "scheme-123",
            rating = 5,
            content = "方案很专业"
        )
        
        assertEquals("scheme-123", request.schemeId)
        assertEquals(5, request.rating)
        assertEquals("方案很专业", request.content)
    }

    @Test
    fun `SubmitSuggestionRequest should contain type and content`() {
        val request = SubmitSuggestionRequest(
            type = "功能建议",
            content = "希望能增加设备对比功能",
            contact = "user@example.com"
        )
        
        assertEquals("功能建议", request.type)
        assertEquals("希望能增加设备对比功能", request.content)
        assertEquals("user@example.com", request.contact)
    }

    @Test
    fun `SubmitDataCorrectionRequest should contain device and error info`() {
        val request = SubmitDataCorrectionRequest(
            deviceId = "device-123",
            errorType = "价格错误",
            correctInfo = "实际价格应该是199元"
        )
        
        assertEquals("device-123", request.deviceId)
        assertEquals("价格错误", request.errorType)
        assertEquals("实际价格应该是199元", request.correctInfo)
    }

    @Test
    fun `SearchDevicesRequest should have pagination parameters`() {
        val request = SearchDevicesRequest(
            keyword = "智能灯",
            category = "照明",
            brand = "小米",
            minPrice = 100.0,
            maxPrice = 500.0,
            page = 1,
            pageSize = 20
        )
        
        assertEquals("智能灯", request.keyword)
        assertEquals("照明", request.category)
        assertEquals("小米", request.brand)
        assertEquals(100.0, request.minPrice!!, 0.0)
        assertEquals(500.0, request.maxPrice!!, 0.0)
        assertEquals(1, request.page)
        assertEquals(20, request.pageSize)
    }
}
