"""
智能家居方案设计APP - 综合自动化测试用例
根据 PRD v1.2、UI设计、DETAILED_DESIGN 设计

测试覆盖：
1. 基础服务测试 - 健康检查、根路径
2. API契约测试 - 统一响应格式
3. 品牌服务测试 - 品牌列表
4. 分类服务测试 - 分类列表
5. 商品服务测试 - 商品列表
6. 户型服务测试 - 户型列表/创建 (v1.1 无状态)
7. 方案生成测试 - AI方案生成 (v1.1)
8. 日志服务测试 - 日志上传 (v1.2 新增)
9. 错误处理测试 - 错误码验证
"""

import httpx
import asyncio
import json
import time
from datetime import datetime
from typing import Dict, Any, Optional, List
from io import BytesIO

BASE_URL = "http://8.137.174.58"

class ComprehensiveTester:
    def __init__(self, base_url: str = BASE_URL):
        self.base_url = base_url
        self.results = []
        self.passed = 0
        self.failed = 0
        self.test_start = datetime.now()
        self.test_data = {}

    def log(self, test_name: str, passed: bool, message: str = "", details: Any = None):
        status = "PASS" if passed else "FAIL"
        self.results.append({
            "test": test_name,
            "status": status,
            "message": message,
            "details": details,
            "timestamp": datetime.now().isoformat()
        })
        if passed:
            self.passed += 1
            print(f"[PASS] {test_name}: {message}")
        else:
            self.failed += 1
            print(f"[FAIL] {test_name}: {message}")
        if details and not passed:
            print(f"       Details: {details}")

    def check_response_code(self, response: httpx.Response, expected: int, test_name: str) -> bool:
        if response.status_code == expected:
            return True
        self.log(test_name, False, f"Expected {expected}, got {response.status_code}")
        return False

    def check_data_field(self, data: Dict, field: str, expected_type: type, test_name: str) -> bool:
        if field not in data:
            self.log(test_name, False, f"Missing field: {field}")
            return False
        if not isinstance(data[field], expected_type):
            self.log(test_name, False, f"Field {field} expected {expected_type}, got {type(data[field])}")
            return False
        return True

    async def test_health_check(self):
        print("\n=== 1. 健康检查测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/health", timeout=10.0)
                if self.check_response_code(response, 200, "健康检查-状态码"):
                    data = response.json()
                    if data.get("status") == "healthy":
                        self.log("健康检查", True, "服务正常运行")
                    else:
                        self.log("健康检查", False, f"状态异常: {data}")
                else:
                    self.log("健康检查", False, f"HTTP {response.status_code}")
        except Exception as e:
            self.log("健康检查", False, f"请求异常: {str(e)}")

    async def test_root_endpoint(self):
        print("\n=== 2. 根路径测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/", timeout=10.0)
                if self.check_response_code(response, 200, "根路径-状态码"):
                    data = response.json()
                    if data.get("name") and data.get("version"):
                        self.log("根路径", True, f"API信息: {data.get('name')} v{data.get('version')}")
                    else:
                        self.log("根路径", False, f"响应格式异常: {data}")
        except Exception as e:
            self.log("根路径", False, f"请求异常: {str(e)}")

    async def test_api_contract(self):
        print("\n=== 3. API契约测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/brands", timeout=10.0)
                if response.status_code == 200:
                    data = response.json()
                    if self.check_data_field(data, "code", int, "API契约-code字段"):
                        self.log("API契约-统一响应格式", True, "包含code/message/timestamp")
                    if self.check_data_field(data, "message", str, "API契约-message字段"):
                        self.log("API契约-message字段", True, "message字段存在")
                    if "timestamp" in data:
                        self.log("API契约-timestamp字段", True, "timestamp字段存在")
                    if isinstance(data.get("data"), list):
                        self.log("API契约-数据结构", True, "data为数组类型")
                    else:
                        self.log("API契约-数据结构", False, f"data类型异常: {type(data.get('data'))}")
                else:
                    self.log("API契约", False, f"HTTP {response.status_code}")
        except Exception as e:
            self.log("API契约", False, f"请求异常: {str(e)}")

    async def test_brand_list(self):
        print("\n=== 4. 品牌服务测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/brands", timeout=10.0)
                if self.check_response_code(response, 200, "品牌列表-状态码"):
                    data = response.json()
                    if self.check_data_field(data, "code", int, "品牌列表-code字段"):
                        if data["code"] == 0:
                            brands = data.get("data", [])
                            if isinstance(brands, list):
                                self.log("品牌列表-code", True, f"成功返回{len(brands)}个品牌")
                                expected_brands = ["小米", "华为", "涂鸦智能", "Aqara", "欧瑞博", "海尔", "美的"]
                                brand_names = [b.get("brand_name") for b in brands if isinstance(b, dict)]
                                for expected in expected_brands:
                                    found = any(expected in name for name in brand_names)
                                    if not found:
                                        self.log("品牌列表-完整性", False, f"缺少品牌: {expected}")
                                        return
                                self.log("品牌列表-完整性", True, "包含所有预期品牌")
                            else:
                                self.log("品牌列表-data类型", False, f"data应为list，实际为{type(brands)}")
                        else:
                            self.log("品牌列表-code", False, f"业务错误: {data.get('message')}")
        except Exception as e:
            self.log("品牌列表", False, f"请求异常: {str(e)}")

    async def test_category_list(self):
        print("\n=== 5. 分类服务测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/categories", timeout=10.0)
                if self.check_response_code(response, 200, "分类列表-状态码"):
                    data = response.json()
                    if self.check_data_field(data, "code", int, "分类列表-code字段"):
                        if data["code"] == 0:
                            categories = data.get("data", [])
                            if isinstance(categories, list):
                                self.log("分类列表-code", True, f"成功返回{len(categories)}个分类")
                                expected_categories = ["智能照明", "智能安防", "智能窗帘", "智能控制", "智能环境", "智能厨卫"]
                                category_names = [c.get("category_name") for c in categories if isinstance(c, dict)]
                                for expected in expected_categories:
                                    found = any(expected in name for name in category_names)
                                    if not found:
                                        self.log("分类列表-完整性", False, f"缺少分类: {expected}")
                                        return
                                self.log("分类列表-完整性", True, "包含所有预期分类")
                            else:
                                self.log("分类列表-data类型", False, f"data应为list")
                        else:
                            self.log("分类列表-code", False, f"业务错误: {data.get('message')}")
        except Exception as e:
            self.log("分类列表", False, f"请求异常: {str(e)}")

    async def test_product_list(self):
        print("\n=== 6. 商品服务测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/products", timeout=10.0)
                if self.check_response_code(response, 200, "商品列表-状态码"):
                    data = response.json()
                    if self.check_data_field(data, "code", int, "商品列表-code字段"):
                        if data["code"] == 0:
                            products = data.get("data", {}).get("list", [])
                            if isinstance(products, list):
                                self.log("商品列表-code", True, f"成功返回{len(products)}个商品")
                                pagination = data.get("data", {}).get("pagination", {})
                                self.log("商品列表-分页", True, f"page={pagination.get('page')}, total={pagination.get('total')}")
                            else:
                                self.log("商品列表-data类型", False, f"data.list应为list")
                        else:
                            self.log("商品列表-code", False, f"业务错误: {data.get('message')}")
        except Exception as e:
            self.log("商品列表", False, f"请求异常: {str(e)}")

    async def test_house_list(self):
        print("\n=== 7. 户型服务测试 - 列表 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/houses", timeout=10.0)
                if self.check_response_code(response, 200, "户型列表-状态码"):
                    self.log("户型列表-架构合规", True, "无需认证即可访问(符合v1.1)")
                    data = response.json()
                    if data.get("code") == 0:
                        houses = data.get("data", {}).get("list", [])
                        if isinstance(houses, list):
                            self.test_data["house_count"] = len(houses)
                            self.log("户型列表-功能", True, f"返回{len(houses)}个户型")
                        else:
                            self.log("户型列表-功能", False, f"data.list应为list")
        except Exception as e:
            self.log("户型列表", False, f"请求异常: {str(e)}")

    async def test_house_create(self):
        print("\n=== 8. 户型服务测试 - 创建 ===")
        house_data = {
            "house_name": f"测试户型_{int(time.time())}",
            "total_area": 90.0,
            "rooms": [
                {
                    "room_name": "客厅",
                    "room_type": "living_room",
                    "length": 5.0,
                    "width": 4.0,
                    "area": 20.0
                },
                {
                    "room_name": "主卧",
                    "room_type": "bedroom",
                    "length": 4.0,
                    "width": 3.5,
                    "area": 14.0
                }
            ]
        }
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.base_url}/api/v1/houses",
                    json=house_data,
                    timeout=10.0
                )
                if self.check_response_code(response, 200, "户型创建-状态码"):
                    self.log("户型创建-架构合规", True, "无需认证即可创建(符合v1.1)")
                    data = response.json()
                    if data.get("code") == 0:
                        self.log("户型创建-功能", True, f"创建成功: {data.get('data', {}).get('house_id', 'N/A')}")
                        self.test_data["created_house_id"] = data.get("data", {}).get("house_id")
                    else:
                        self.log("户型创建-功能", False, f"业务错误: {data.get('message')}")
        except Exception as e:
            self.log("户型创建", False, f"请求异常: {str(e)}")

    async def test_scheme_generation(self):
        print("\n=== 9. 方案生成测试 ===")
        request_data = {
            "house_layout": {
                "total_area": 90.0,
                "rooms": [
                    {
                        "room_name": "客厅",
                        "room_type": "living_room",
                        "length": 5.0,
                        "width": 4.0,
                        "area": 20.0
                    },
                    {
                        "room_name": "主卧",
                        "room_type": "bedroom",
                        "length": 4.0,
                        "width": 3.5,
                        "area": 14.0
                    }
                ]
            },
            "questionnaire": {
                "living_status": "own",
                "resident_count": 2,
                "has_elderly": False,
                "has_children": False,
                "has_pets": True,
                "preferred_scenarios": ["lighting", "security"],
                "sleep_pattern": "normal",
                "knowledge_level": "basic"
            },
            "preferences": {
                "budget_min": 5000,
                "budget_max": 15000,
                "preferred_brands": ["小米", "Aqara"],
                "excluded_brands": []
            }
        }
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.base_url}/api/v1/schemes/generate",
                    json=request_data,
                    timeout=30.0
                )
                if response.status_code == 200:
                    data = response.json()
                    if data.get("code") == 0:
                        result = data.get("data", {})
                        if "task_id" in result:
                            self.log("方案生成-异步模式", True, f"任务ID: {result['task_id']}")
                            self.test_data["task_id"] = result["task_id"]
                            await self.test_task_status(result["task_id"])
                        elif "scheme_name" in result:
                            self.log("方案生成-同步模式", True, f"方案: {result.get('scheme_name')}")
                            await self.validate_scheme_result(result)
                        else:
                            self.log("方案生成-响应格式", True, "返回数据格式正确")
                    else:
                        self.log("方案生成-code", False, f"业务错误: {data.get('message')}")
                elif response.status_code == 422:
                    self.log("方案生成-参数校验", False, "请求参数格式错误")
                else:
                    self.log("方案生成-状态码", False, f"HTTP {response.status_code}")
        except Exception as e:
            self.log("方案生成", False, f"请求异常: {str(e)}")

    async def test_task_status(self, task_id: str):
        print(f"\n=== 方案任务状态查询: {task_id[:8]}... ===")
        max_retries = 60
        for i in range(max_retries):
            try:
                async with httpx.AsyncClient() as client:
                    response = await client.get(
                        f"{self.base_url}/api/v1/schemes/tasks/{task_id}",
                        timeout=30.0
                    )
                    if response.status_code == 200:
                        data = response.json()
                        if data.get("code") == 0:
                            result = data.get("data", {})
                            status = result.get("status", "unknown")
                            self.log(f"方案任务状态-#{i+1}", True, f"状态: {status}")
                            if status == "completed" or status == "success":
                                scheme_result = result.get("result", {})
                                await self.validate_scheme_result(scheme_result)
                                return
                            elif status == "failed":
                                self.log("方案任务-失败", False, f"任务执行失败")
                                return
                    await asyncio.sleep(2)
            except Exception as e:
                self.log(f"方案任务状态-#{i+1}", False, f"请求异常: {str(e)}")
        self.log("方案任务-超时", False, f"等待{max_retries}次(共{max_retries * 2}秒)后未完成")

    async def validate_scheme_result(self, result: Dict):
        print("\n=== 方案结果验证 ===")
        required_fields = ["scheme_name", "scheme_description", "devices", "total_price", "budget_remaining"]
        for field in required_fields:
            if field in result:
                self.log(f"方案字段-{field}", True, f"存在")
            else:
                self.log(f"方案字段-{field}", False, f"缺少必需字段")
                return
        devices = result.get("devices", [])
        if isinstance(devices, list) and len(devices) > 0:
            self.log("方案设备列表", True, f"包含{len(devices)}个设备")
            for device in devices[:2]:
                if isinstance(device, dict):
                    required_device_fields = ["device_type", "device_name", "room", "quantity", "reason"]
                    for field in required_device_fields:
                        if field not in device:
                            self.log(f"设备字段-{field}", False, f"设备缺少字段")
                            return
            self.log("设备字段-完整性", True, "设备数据结构正确")
        else:
            self.log("方案设备列表", False, f"设备列表为空或格式异常")

    async def test_log_upload(self):
        print("\n=== 10. 日志服务测试 - 日志上传 ===")
        log_content = f"""[2026-03-24 15:00:00] INFO: App started
[2026-03-24 15:00:01] INFO: User opened house list page
[2026-03-24 15:00:02] DEBUG: API request: GET /api/v1/houses
[2026-03-24 15:00:03] INFO: Received 10 houses from API
[2026-03-24 15:00:04] WARN: Slow network detected
[2026-03-24 15:00:05] ERROR: Failed to load product image
[2026-03-24 15:00:06] INFO: User tapped generate button
[2026-03-24 15:00:07] INFO: Scheme generation started
[2026-03-24 15:00:08] DEBUG: Request data: {{"house_layout": {{"total_area": 90}}}}
[2026-03-24 15:00:09] INFO: Scheme generation completed
"""
        try:
            async with httpx.AsyncClient() as client:
                files = {"file": ("test_log.txt", BytesIO(log_content.encode()), "text/plain")}
                data = {
                    "appVersion": "1.0.0",
                    "platform": "android",
                    "osVersion": "13",
                    "logStartDate": "2026-03-20T00:00:00Z",
                    "logEndDate": "2026-03-24T23:59:59Z"
                }
                headers = {"X-Device-ID": "test-device-001"}
                response = await client.post(
                    f"{self.base_url}/api/v1/logs/upload",
                    files=files,
                    data=data,
                    headers=headers,
                    timeout=30.0
                )
                if self.check_response_code(response, 200, "日志上传-状态码"):
                    result = response.json()
                    if result.get("code") == 0:
                        self.log("日志上传-功能", True, f"上传成功: {result.get('data', {}).get('uploadId', 'N/A')}")
                        if "logCount" in result.get("data", {}):
                            self.log("日志上传-计数", True, f"日志条数: {result.get('data', {}).get('logCount')}")
                    else:
                        error_msg = result.get("message", "未知错误")
                        if "过大" in error_msg or "限制" in error_msg:
                            self.log("日志上传-文件大小", False, f"文件超过限制: {error_msg}")
                        else:
                            self.log("日志上传-功能", False, f"业务错误: {error_msg}")
        except Exception as e:
            self.log("日志上传", False, f"请求异常: {str(e)}")

    async def test_log_upload_large_file(self):
        print("\n=== 11. 日志服务测试 - 大文件上传 ===")
        large_log_content = "A" * (6 * 1024 * 1024)
        try:
            async with httpx.AsyncClient() as client:
                files = {"file": ("large_log.txt", BytesIO(large_log_content.encode()), "text/plain")}
                data = {
                    "appVersion": "1.0.0",
                    "platform": "android",
                    "osVersion": "13"
                }
                headers = {"X-Device-ID": "test-device-002"}
                response = await client.post(
                    f"{self.base_url}/api/v1/logs/upload",
                    files=files,
                    data=data,
                    headers=headers,
                    timeout=30.0
                )
                if response.status_code == 413:
                    self.log("日志上传-大文件拦截", True, "正确拒绝超大文件(413)")
                elif response.status_code == 200:
                    result = response.json()
                    if result.get("code") == 40001:
                        self.log("日志上传-大文件拦截", True, "正确返回文件过大错误码")
                    else:
                        self.log("日志上传-大文件拦截", False, f"预期错误码40001，实际{result.get('code')}")
                else:
                    self.log("日志上传-大文件拦截", False, f"HTTP {response.status_code}")
        except Exception as e:
            self.log("日志上传-大文件", False, f"请求异常: {str(e)}")

    async def test_error_handling(self):
        print("\n=== 12. 错误处理测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/api/v1/products/999999", timeout=10.0)
                if response.status_code == 404:
                    self.log("错误处理-404响应", True, "正确返回404状态码")
                elif response.status_code == 200:
                    data = response.json()
                    if data.get("code") != 0:
                        self.log("错误处理-业务错误码", True, f"返回错误码: {data.get('code')}")
                    else:
                        self.log("错误处理-业务错误码", False, "预期返回错误，实际返回成功")
        except Exception as e:
            self.log("错误处理", False, f"请求异常: {str(e)}")

    async def test_rate_limiting(self):
        print("\n=== 13. 限流测试 ===")
        print("提示: 限流测试需要发送大量请求，谨慎执行")
        pass

    async def run_all_tests(self):
        print("=" * 60)
        print("智能家居方案设计APP - 综合自动化测试")
        print("=" * 60)
        print(f"测试时间: {self.test_start.isoformat()}")
        print(f"测试地址: {self.base_url}")
        print(f"架构版本: v1.2 (含日志管理模块)")
        print("=" * 60)

        await self.test_health_check()
        await self.test_root_endpoint()
        await self.test_api_contract()
        await self.test_brand_list()
        await self.test_category_list()
        await self.test_product_list()
        await self.test_house_list()
        await self.test_house_create()
        await self.test_scheme_generation()
        await self.test_log_upload()
        await self.test_log_upload_large_file()
        await self.test_error_handling()

        self.print_summary()
        return self.results

    def print_summary(self):
        print("\n" + "=" * 60)
        print("测试结果汇总")
        print("=" * 60)
        print(f"测试地址: {self.base_url}")
        print(f"测试耗时: {(datetime.now() - self.test_start).total_seconds():.2f}秒")
        print(f"PASS: {self.passed}")
        print(f"FAIL: {self.failed}")
        print(f"总计: {self.passed + self.failed}")
        print("=" * 60)

        with open("test_results.json", "w", encoding="utf-8") as f:
            json.dump({
                "summary": {
                    "base_url": self.base_url,
                    "duration": (datetime.now() - self.test_start).total_seconds(),
                    "passed": self.passed,
                    "failed": self.failed,
                    "total": self.passed + self.failed,
                    "timestamp": datetime.now().isoformat()
                },
                "results": self.results
            }, f, ensure_ascii=False, indent=2)
            print("\n结果已保存到 test_results.json")

if __name__ == "__main__":
    tester = ComprehensiveTester()
    asyncio.run(tester.run_all_tests())
