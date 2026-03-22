import httpx
import asyncio
from typing import Dict, Any, Optional
from datetime import datetime

BASE_URL = "http://8.137.174.58"

class APITester:
    def __init__(self, base_url: str = BASE_URL):
        self.base_url = base_url
        self.results = []
        self.passed = 0
        self.failed = 0

    def log(self, test_name: str, passed: bool, message: str = ""):
        status = "✅ PASS" if passed else "❌ FAIL"
        self.results.append({
            "test": test_name,
            "status": status,
            "message": message,
            "timestamp": datetime.now().isoformat()
        })
        if passed:
            self.passed += 1
        else:
            self.failed += 1
        print(f"{status}: {test_name} {message}")

    async def test_health(self):
        print("\n=== 健康检查测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/health", timeout=5.0)
                self.log("健康检查", response.status_code == 200, f"状态码: {response.status_code}")
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
        except Exception as e:
            self.log("健康检查", False, f"错误: {str(e)}")

    async def test_root(self):
        print("\n=== 根路径测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(f"{self.base_url}/", timeout=5.0)
                self.log("根路径", response.status_code == 200, f"状态码: {response.status_code}")
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
        except Exception as e:
            self.log("根路径", False, f"错误: {str(e)}")

    async def test_create_house(self):
        print("\n=== 户型创建测试 ===")
        house_data = {
            "total_area": 90.0,
            "rooms": [
                {
                    "room_name": "客厅",
                    "room_type": "living_room",
                    "length": 5.0,
                    "width": 4.0
                },
                {
                    "room_name": "主卧",
                    "room_type": "bedroom",
                    "length": 4.0,
                    "width": 3.5
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
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        self.log("创建户型", True, f"house_id: {data.get('data', {}).get('house_id')}")
                        return data.get("data", {}).get("house_id")
                    else:
                        self.log("创建户型", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("创建户型", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("创建户型", False, f"错误: {str(e)}")
        return None

    async def test_list_houses(self):
        print("\n=== 户型列表测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.base_url}/api/v1/houses",
                    params={"page": 1, "page_size": 10},
                    timeout=10.0
                )
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        self.log("户型列表", True, f"总数: {data.get('data', {}).get('pagination', {}).get('total')}")
                        return data.get("data", {}).get("list", [])
                    else:
                        self.log("户型列表", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("户型列表", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("户型列表", False, f"错误: {str(e)}")
        return []

    async def test_list_products(self):
        print("\n=== 商品列表测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.base_url}/api/v1/products",
                    params={"page": 1, "page_size": 10},
                    timeout=10.0
                )
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        self.log("商品列表", True, f"总数: {data.get('data', {}).get('pagination', {}).get('total')}")
                        return data.get("data", {}).get("list", [])
                    else:
                        self.log("商品列表", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("商品列表", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("商品列表", False, f"错误: {str(e)}")
        return []

    async def test_list_brands(self):
        print("\n=== 品牌列表测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.base_url}/api/v1/brands",
                    timeout=10.0
                )
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        data_list = data.get('data', [])
                        if isinstance(data_list, list):
                            self.log("品牌列表", True, f"总数: {len(data_list)}")
                        else:
                            self.log("品牌列表", True, f"总数: {len(data_list.get('list', []))}")
                        return True
                    else:
                        self.log("品牌列表", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("品牌列表", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("品牌列表", False, f"错误: {str(e)}")
        return False

    async def test_list_categories(self):
        print("\n=== 分类列表测试 ===")
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.base_url}/api/v1/categories",
                    timeout=10.0
                )
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        data_list = data.get('data', [])
                        if isinstance(data_list, list):
                            self.log("分类列表", True, f"总数: {len(data_list)}")
                        else:
                            self.log("分类列表", True, f"总数: {len(data_list.get('list', []))}")
                        return True
                    else:
                        self.log("分类列表", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("分类列表", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("分类列表", False, f"错误: {str(e)}")
        return False

    async def test_generate_scheme(self):
        print("\n=== 方案生成测试 ===")
        scheme_request = {
            "houseLayout": {
                "totalArea": 90.0,
                "rooms": [
                    {
                        "roomName": "客厅",
                        "roomType": "living_room",
                        "length": 5.0,
                        "width": 4.0,
                        "area": 20.0
                    }
                ]
            },
            "questionnaire": {
                "livingStatus": "own",
                "residentCount": 2,
                "hasElderly": False,
                "hasChildren": False,
                "hasPets": True,
                "preferredScenarios": ["lighting", "security"],
                "sleepPattern": "normal",
                "knowledgeLevel": "basic"
            },
            "preferences": {
                "budgetMin": 5000,
                "budgetMax": 15000,
                "preferredBrands": ["小米", "Aqara"],
                "excludedBrands": []
            }
        }
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.base_url}/api/v1/schemes/generate",
                    json=scheme_request,
                    timeout=60.0
                )
                if response.status_code == 200:
                    data = response.json()
                    print(f"  响应: {data}")
                    if data.get("code") == 0:
                        self.log("方案生成", True, f"scheme_id: {data.get('data', {}).get('scheme_id')}")
                        return True
                    else:
                        self.log("方案生成", False, f"业务错误: {data.get('message')}")
                else:
                    self.log("方案生成", False, f"HTTP错误: {response.status_code}")
        except Exception as e:
            self.log("方案生成", False, f"错误: {str(e)}")
        return False

    async def test_no_auth_required(self):
        print("\n=== 架构合规性测试 ===")
        print("验证API是否无需认证即可访问 (符合v1.1架构)")

        endpoints = [
            ("GET", "/api/v1/houses"),
            ("GET", "/api/v1/products"),
            ("GET", "/api/v1/brands"),
            ("GET", "/api/v1/categories"),
        ]

        all_passed = True
        for method, endpoint in endpoints:
            try:
                async with httpx.AsyncClient() as client:
                    if method == "GET":
                        response = await client.get(f"{self.base_url}{endpoint}", timeout=10.0)
                    else:
                        response = await client.post(f"{self.base_url}{endpoint}", json={}, timeout=10.0)

                    if response.status_code == 200:
                        print(f"  ✅ {method} {endpoint} - 无需认证")
                    else:
                        print(f"  ❌ {method} {endpoint} - 状态码: {response.status_code}")
                        all_passed = False
            except Exception as e:
                print(f"  ❌ {method} {endpoint} - 错误: {str(e)}")
                all_passed = False

        self.log("架构合规性(无需认证)", all_passed, "所有接口无需Token即可访问")

    async def run_all_tests(self):
        print("=" * 60)
        print("智能家居方案设计APP - API自动测试")
        print("=" * 60)
        print(f"测试时间: {datetime.now().isoformat()}")
        print(f"测试地址: {self.base_url}")
        print("=" * 60)

        await self.test_health()
        await self.test_root()
        await self.test_no_auth_required()
        await self.test_list_houses()
        await self.test_create_house()
        await self.test_list_products()
        await self.test_list_brands()
        await self.test_list_categories()
        await self.test_generate_scheme()

        print("\n" + "=" * 60)
        print("测试结果汇总")
        print("=" * 60)
        print(f"✅ 通过: {self.passed}")
        print(f"❌ 失败: {self.failed}")
        print(f"总计: {self.passed + self.failed}")
        print("=" * 60)

        return self.passed, self.failed

if __name__ == "__main__":
    tester = APITester()
    passed, failed = asyncio.run(tester.run_all_tests())
    exit(0 if failed == 0 else 1)
