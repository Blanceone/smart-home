package com.smarthome.presentation.info

import org.junit.Assert.*
import org.junit.Test

class InfoCollectionUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        val state = InfoCollectionUiState()
        
        assertEquals(1, state.currentStep)
        assertEquals(7, state.totalSteps)
        assertFalse(state.isLoading)
        assertFalse(state.isCompleted)
        assertNull(state.error)
        assertNull(state.basicInfo)
        assertNull(state.lifestyle)
        assertNull(state.deviceExperience)
        assertNull(state.aestheticPreference)
        assertNull(state.brandPreference)
        assertNull(state.houseLayout)
        assertNull(state.budget)
    }

    @Test
    fun `state copy should update currentStep`() {
        val original = InfoCollectionUiState(currentStep = 1)
        val updated = original.copy(currentStep = 3)
        
        assertEquals(1, original.currentStep)
        assertEquals(3, updated.currentStep)
    }

    @Test
    fun `state should track loading status`() {
        val loadingState = InfoCollectionUiState(isLoading = true)
        val notLoadingState = InfoCollectionUiState(isLoading = false)
        
        assertTrue(loadingState.isLoading)
        assertFalse(notLoadingState.isLoading)
    }

    @Test
    fun `state should track error message`() {
        val errorState = InfoCollectionUiState(error = "网络错误")
        
        assertEquals("网络错误", errorState.error)
    }

    @Test
    fun `state should track completion status`() {
        val completedState = InfoCollectionUiState(isCompleted = true)
        
        assertTrue(completedState.isCompleted)
    }
}

class BasicInfoDataTest {

    @Test
    fun `default basic info should have empty values`() {
        val basicInfo = BasicInfoData()
        
        assertNull(basicInfo.age)
        assertNull(basicInfo.occupation)
        assertTrue(basicInfo.familyMembers.isEmpty())
        assertEquals("", basicInfo.city)
    }

    @Test
    fun `basic info should store all fields`() {
        val basicInfo = BasicInfoData(
            age = "26-30",
            occupation = "上班族",
            familyMembers = setOf("情侣"),
            city = "北京"
        )
        
        assertEquals("26-30", basicInfo.age)
        assertEquals("上班族", basicInfo.occupation)
        assertTrue(basicInfo.familyMembers.contains("情侣"))
        assertEquals("北京", basicInfo.city)
    }

    @Test
    fun `family members should support multiple selections`() {
        val basicInfo = BasicInfoData(
            familyMembers = setOf("夫妻+孩子", "与父母同住")
        )
        
        assertEquals(2, basicInfo.familyMembers.size)
        assertTrue(basicInfo.familyMembers.contains("夫妻+孩子"))
        assertTrue(basicInfo.familyMembers.contains("与父母同住"))
    }
}

class LifestyleDataTest {

    @Test
    fun `lifestyle data should store all fields`() {
        val lifestyle = LifestyleData(
            sleepPattern = "晚睡晚起",
            homeActivities = setOf("休闲娱乐", "健身运动"),
            entertainmentHabits = setOf("看电影追剧", "听音乐")
        )
        
        assertEquals("晚睡晚起", lifestyle.sleepPattern)
        assertEquals(2, lifestyle.homeActivities.size)
        assertEquals(2, lifestyle.entertainmentHabits.size)
    }
}

class DeviceExperienceDataTest {

    @Test
    fun `device experience data should store knowledge level`() {
        val experience = DeviceExperienceData(
            knowledgeLevel = "用过一些",
            usedDevices = setOf("智能音箱", "智能灯具")
        )
        
        assertEquals("用过一些", experience.knowledgeLevel)
        assertEquals(2, experience.usedDevices.size)
    }
}

class AestheticPreferenceDataTest {

    @Test
    fun `aesthetic preference should store style and colors`() {
        val preference = AestheticPreferenceData(
            decorStyle = "现代简约",
            colorPreferences = setOf("白色系", "原木色")
        )
        
        assertEquals("现代简约", preference.decorStyle)
        assertEquals(2, preference.colorPreferences.size)
    }
}

class BrandPreferenceDataTest {

    @Test
    fun `brand preference should store selected brands`() {
        val preference = BrandPreferenceData(
            preferredBrands = setOf("小米/米家", "华为")
        )
        
        assertEquals(2, preference.preferredBrands.size)
        assertTrue(preference.preferredBrands.contains("小米/米家"))
    }
}

class HouseLayoutDataTest {

    @Test
    fun `house layout should store house type and area`() {
        val layout = HouseLayoutData(
            houseType = "两居室",
            totalArea = 85.0,
            rooms = listOf(
                RoomData(name = "客厅", area = 25.0),
                RoomData(name = "主卧", area = 18.0)
            )
        )
        
        assertEquals("两居室", layout.houseType)
        assertEquals(85.0, layout.totalArea, 0.0)
        assertEquals(2, layout.rooms.size)
    }

    @Test
    fun `rooms should support special needs`() {
        val room = RoomData(
            name = "客厅",
            area = 25.0,
            specialNeeds = "需要智能灯光控制"
        )
        
        assertEquals("客厅", room.name)
        assertEquals(25.0, room.area, 0.0)
        assertEquals("需要智能灯光控制", room.specialNeeds)
    }
}

class RoomDataTest {

    @Test
    fun `room data should have default values`() {
        val room = RoomData()
        
        assertEquals("", room.id)
        assertEquals("", room.name)
        assertEquals(0.0, room.area, 0.0)
        assertNull(room.specialNeeds)
    }

    @Test
    fun `room data should store all fields`() {
        val room = RoomData(
            id = "room-123",
            name = "书房",
            area = 15.0,
            specialNeeds = "需要智能照明"
        )
        
        assertEquals("room-123", room.id)
        assertEquals("书房", room.name)
        assertEquals(15.0, room.area, 0.0)
        assertEquals("需要智能照明", room.specialNeeds)
    }
}
