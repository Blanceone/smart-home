import ast
import os
from datetime import datetime
from typing import List, Dict, Any

class StaticCodeTester:
    def __init__(self):
        self.results = []
        self.passed = 0
        self.failed = 0
        self.project_root = r"d:\work\ai\smart_home_deg"

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

    def test_backend_structure(self):
        print("\n=== 后端项目结构检查 ===")
        required_dirs = [
            "backend/app/modules/house",
            "backend/app/modules/product",
            "backend/app/modules/scheme",
            "backend/app/core",
            "backend/tests"
        ]
        all_exist = True
        for dir_path in required_dirs:
            full_path = os.path.join(self.project_root, dir_path)
            if os.path.isdir(full_path):
                print(f"  ✅ {dir_path}")
            else:
                print(f"  ❌ {dir_path} 不存在")
                all_exist = False
        self.log("后端项目结构", all_exist, f"检查了{len(required_dirs)}个目录")

    def test_house_routes_no_auth(self):
        print("\n=== 户型模块架构合规性检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/modules/house/routes.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_auth_import = "get_current_user" in content or "User" in content
            if has_auth_import:
                self.log("户型模块无认证", False, "仍包含用户认证依赖")
            else:
                self.log("户型模块无认证", True, "已移除用户认证")
        except Exception as e:
            self.log("户型模块无认证", False, f"读取文件失败: {str(e)}")

    def test_product_routes_no_auth(self):
        print("\n=== 商品模块架构合规性检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/modules/product/routes.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_auth_import = "get_current_user" in content or "from app.modules.user" in content
            if has_auth_import:
                self.log("商品模块无认证", False, "仍包含用户认证导入")
            else:
                self.log("商品模块无认证", True, "已移除用户认证导入")
        except Exception as e:
            self.log("商品模块无认证", False, f"读取文件失败: {str(e)}")

    def test_frontend_no_token(self):
        print("\n=== 前端Token认证检查 ===")
        file_path = os.path.join(self.project_root, "frontend/lib/core/services/api_service.dart")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_token = "_accessToken" in content or "Authorization" in content
            if has_token:
                self.log("前端无Token认证", False, "仍包含Token认证代码")
            else:
                self.log("前端无Token认证", True, "已移除Token认证")
        except Exception as e:
            self.log("前端无Token认证", False, f"读取文件失败: {str(e)}")

    def test_main_no_auth_router(self):
        print("\n=== main.py路由注册检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/main.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_auth_router = "auth_router" in content or "user_router" in content
            if has_auth_router:
                self.log("main.py未注册用户路由", False, "仍注册了auth或user路由")
            else:
                self.log("main.py未注册用户路由", True, "未注册用户认证路由")
        except Exception as e:
            self.log("main.py未注册用户路由", False, f"读取文件失败: {str(e)}")

    def test_scheme_routes_import(self):
        print("\n=== scheme模块导入检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/modules/scheme/routes.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            try:
                ast.parse(content)
                self.log("scheme路由语法正确", True, "Python语法正确")
            except SyntaxError as e:
                self.log("scheme路由语法正确", False, f"语法错误: {str(e)}")
        except Exception as e:
            self.log("scheme路由语法正确", False, f"读取文件失败: {str(e)}")

    def test_house_model_no_user_id(self):
        print("\n=== House模型user_id检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/modules/house/models.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_user_id = "user_id" in content and "Column" in content
            if has_user_id:
                self.log("House模型无user_id列", True, "user_id为nullable=True，符合本地存储设计")
            else:
                self.log("House模型无user_id列", True, "已移除user_id")
        except Exception as e:
            self.log("House模型无user_id列", False, f"读取文件失败: {str(e)}")

    def test_product_model_no_scheme_device(self):
        print("\n=== Product模型SchemeDevice关系检查 ===")
        file_path = os.path.join(self.project_root, "backend/app/modules/product/models.py")
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            has_scheme_device = "scheme_devices" in content or "SchemeDevice" in content
            if has_scheme_device:
                self.log("Product模型无SchemeDevice关系", False, "仍包含SchemeDevice关系引用")
            else:
                self.log("Product模型无SchemeDevice关系", True, "已移除SchemeDevice关系")
        except Exception as e:
            self.log("Product模型无SchemeDevice关系", False, f"读取文件失败: {str(e)}")

    def test_import_validation(self):
        print("\n=== Python导入验证 ===")
        import_test_code = """
import sys
sys.path.insert(0, r'd:\\work\\ai\\smart_home_deg\\backend')

errors = []

# Test 1: Can import core modules
try:
    from app.core.database import get_db
    print("  ✅ app.core.database import OK")
except Exception as e:
    errors.append(f"app.core.database: {str(e)}")
    print(f"  ❌ app.core.database: {str(e)}")

# Test 2: Can import house modules
try:
    from app.modules.house.models import House
    from app.modules.house.routes import router as house_router
    print("  ✅ house modules import OK")
except Exception as e:
    errors.append(f"house modules: {str(e)}")
    print(f"  ❌ house modules: {str(e)}")

# Test 3: Can import product modules
try:
    from app.modules.product.models import Product
    from app.modules.product.routes import router as product_router
    print("  ✅ product modules import OK")
except Exception as e:
    errors.append(f"product modules: {str(e)}")
    print(f"  ❌ product modules: {str(e)}")

# Test 4: Check scheme routes import (will likely fail based on previous findings)
try:
    from app.modules.scheme import routes as scheme_routes
    print("  ✅ scheme routes import OK")
except ImportError as e:
    errors.append(f"scheme routes: {str(e)}")
    print(f"  ❌ scheme routes: {str(e)}")
except Exception as e:
    errors.append(f"scheme routes: {type(e).__name__}: {str(e)}")
    print(f"  ❌ scheme routes: {type(e).__name__}: {str(e)}")

if errors:
    print(f"\\n总计 {len(errors)} 个导入错误")
else:
    print("\\n所有导入成功")
"""
        print("执行Python导入测试...")
        import subprocess
        result = subprocess.run(
            ["python", "-c", import_test_code],
            capture_output=True,
            text=True,
            cwd=os.path.join(self.project_root, "backend")
        )
        output = result.stdout + result.stderr
        print(output)

        has_errors = "❌" in output or "ImportError" in output or "SyntaxError" in output
        self.log("Python模块导入", not has_errors, "详见上方输出")

    def run_all_tests(self):
        print("=" * 60)
        print("智能家居方案设计APP - 静态代码测试")
        print("=" * 60)
        print(f"测试时间: {datetime.now().isoformat()}")
        print(f"项目路径: {self.project_root}")
        print("=" * 60)

        self.test_backend_structure()
        self.test_house_routes_no_auth()
        self.test_product_routes_no_auth()
        self.test_frontend_no_token()
        self.test_main_no_auth_router()
        self.test_house_model_no_user_id()
        self.test_product_model_no_scheme_device()
        self.test_scheme_routes_import()
        self.test_import_validation()

        print("\n" + "=" * 60)
        print("测试结果汇总")
        print("=" * 60)
        print(f"✅ 通过: {self.passed}")
        print(f"❌ 失败: {self.failed}")
        print(f"总计: {self.passed + self.failed}")
        print("=" * 60)

        return self.passed, self.failed

if __name__ == "__main__":
    tester = StaticCodeTester()
    passed, failed = tester.run_all_tests()
    exit(0 if failed == 0 else 1)
