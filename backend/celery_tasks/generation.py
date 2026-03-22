import json
import httpx
from decimal import Decimal
from typing import List, Dict, Any
from app.celery_app import celery_app
from app.core.config import settings


@celery_app.task(bind=True, max_retries=2)
def generate_scheme_task(self, params: Dict[str, Any]):
    try:
        rooms = params.get("rooms", [])
        total_area = params.get("total_area", 0)

        prompt = build_prompt(params)
        ai_result = call_deepseek_api(prompt)

        if "error" in ai_result:
            return {"error": ai_result["error"]}

        scheme_data = ai_result.get("data", {})
        devices = scheme_data.get("devices", [])

        matched_devices = match_products_api(devices, params)

        total_price = sum(d.get("subtotal", 0) for d in matched_devices)

        return {
            "scheme_name": scheme_data.get("scheme_name", "智能家居方案"),
            "scheme_description": scheme_data.get("scheme_description", ""),
            "devices": matched_devices,
            "total_price": total_price,
            "budget_remaining": params.get("budget_max", 100000) - total_price
        }

    except Exception as e:
        if self.request.retries < self.max_retries:
            raise self.retry(exc=e, countdown=60)
        return {"error": str(e)}


def build_prompt(params: Dict[str, Any]) -> str:
    rooms = params.get("rooms", [])
    room_str = ", ".join([f"{r['room_name']}({r['area']}平米)" for r in rooms])

    scenarios = params.get("preferred_scenarios", [])
    scenario_map = {
        "lighting": "智能照明",
        "security": "智能安防",
        "curtain": "智能窗帘",
        "appliance": "智能家电",
        "environment": "智能环境",
        "audio": "智能影音"
    }
    scenario_names = [scenario_map.get(s, s) for s in scenarios]
    scenario_str = "、".join(scenario_names) if scenario_names else "基础智能设备"

    living_status_map = {"own": "自有住房", "rent": "租房"}
    living_status = living_status_map.get(params.get("living_status", "own"), "自有住房")

    sleep_pattern_map = {
        "early": "早睡早起",
        "late": "晚睡晚起",
        "irregular": "作息不规律"
    }
    sleep_pattern = sleep_pattern_map.get(params.get("sleep_pattern", "normal"), "正常作息")

    knowledge_map = {
        "none": "完全不了解",
        "basic": "了解一些基础知识",
        "familiar": "比较熟悉智能家居"
    }
    knowledge = knowledge_map.get(params.get("knowledge_level", "basic"), "了解一些基础知识")

    budget_min = params.get("budget_min", 0)
    budget_max = params.get("budget_max", 100000)

    return f"""你是一位专业的智能家居方案设计师。请根据以下信息为用户设计一套智能家居方案。

用户住宅信息：
- 房型：{room_str}
- 总面积：{total_area}平米

居住情况：
- 居住状态：{living_status}
- 居住人数：{params.get("resident_count", 1)}人
- 是否有老人：{"是" if params.get("has_elderly") else "否"}
- 是否有儿童：{"是" if params.get("has_children") else "否"}
- 是否有宠物：{"是" if params.get("has_pet") else "否"}

用户偏好：
- 偏好场景：{scenario_str}
- 睡眠模式：{sleep_pattern}
- 智能家居了解程度：{knowledge}

预算范围：{budget_min} - {budget_max}元

请设计一套智能家居方案，包括：
1. 方案名称和描述
2. 每个房间需要的智能设备列表
3. 每个设备的选型理由
4. 预估价格

请以JSON格式返回，格式如下：
{{
    "scheme_name": "方案名称",
    "scheme_description": "方案描述",
    "devices": [
        {{
            "device_type": "设备类型如light/curtain/speaker/sensor等",
            "device_name": "具体设备名称",
            "room": "房间名称",
            "quantity": 数量,
            "reason": "选型理由",
            "subtotal": 单价
        }}
    ],
    "total_price": 总价
}}

注意：设备数量要合理，单价要在预算范围内。"""


def call_deepseek_api(prompt: str) -> Dict[str, Any]:
    if not settings.DEEPSEEK_API_KEY:
        return generate_mock_scheme()

    try:
        with httpx.Client(timeout=settings.AI_GENERATION_TIMEOUT) as client:
            response = client.post(
                f"{settings.DEEPSEEK_API_URL}/chat/completions",
                headers={
                    "Authorization": f"Bearer {settings.DEEPSEEK_API_KEY}",
                    "Content-Type": "application/json"
                },
                json={
                    "model": settings.DEEPSEEK_MODEL,
                    "messages": [
                        {"role": "system", "content": "你是一位专业的智能家居方案设计师。"},
                        {"role": "user", "content": prompt}
                    ],
                    "temperature": 0.7
                }
            )

            if response.status_code != 200:
                return {"error": f"API调用失败: {response.status_code}"}

            result = response.json()
            content = result["choices"][0]["message"]["content"]

            if content.startswith("```json"):
                content = content[7:]
            if content.endswith("```"):
                content = content[:-3]

            return {"data": json.loads(content)}

    except httpx.TimeoutException:
        return {"error": "AI服务响应超时，请重试"}
    except Exception as e:
        return {"error": f"AI服务调用失败: {str(e)}"}


def match_products_api(devices: List[Dict], params: Dict[str, Any]) -> List[Dict]:
    budget_max = params.get("budget_max", 100000)
    preferred_brands = params.get("preferred_brands", [])
    excluded_brands = params.get("excluded_brands", [])

    mock_products = {
        "light": [
            {"name": "米家智能LED灯泡", "brand": "小米", "price": 79},
            {"name": "Philips智能灯泡", "brand": "飞利浦", "price": 129},
            {"name": "Yeelight智能灯带", "brand": "Yeelight", "price": 149},
        ],
        "curtain": [
            {"name": "小米智能窗帘", "brand": "小米", "price": 499},
            {"name": "Aqara智能窗帘电机", "brand": "绿米", "price": 599},
        ],
        "speaker": [
            {"name": "小爱音箱Pro", "brand": "小米", "price": 199},
            {"name": "小度智能音箱", "brand": "百度", "price": 299},
        ],
        "sensor": [
            {"name": "米家温湿度传感器", "brand": "小米", "price": 49},
            {"name": "Aqara人体传感器", "brand": "绿米", "price": 89},
        ],
        "camera": [
            {"name": "小米智能摄像机", "brand": "小米", "price": 199},
            {"name": "萤石智能摄像机", "brand": "海康威视", "price": 299},
        ],
        "lock": [
            {"name": "小米智能门锁", "brand": "小米", "price": 999},
            {"name": "德施曼智能门锁", "brand": "德施曼", "price": 1499},
        ],
        "switch": [
            {"name": "小米智能开关", "brand": "小米", "price": 89},
            {"name": "Aqara智能墙壁开关", "brand": "绿米", "price": 149},
        ],
        "air": [
            {"name": "米家空调伴侣", "brand": "小米", "price": 149},
            {"name": "智能空气净化器", "brand": "小米", "price": 699},
        ]
    }

    matched = []
    for device in devices:
        device_type = device.get("device_type", "light").lower()
        quantity = device.get("quantity", 1)

        products = mock_products.get(device_type, mock_products["light"])
        product = products[0] if products else {"name": device.get("device_name", "智能设备"), "brand": "未知", "price": 99}

        matched.append({
            "device_type": device_type,
            "device_name": device.get("device_name", product["name"]),
            "room": device.get("room", "客厅"),
            "quantity": quantity,
            "reason": device.get("reason", "性价比高"),
            "subtotal": product["price"] * quantity,
            "brand_name": product["brand"],
            "price": product["price"]
        })

    return matched


def generate_mock_scheme() -> Dict[str, Any]:
    return {
        "data": {
            "scheme_name": "温馨两居智能生活方案",
            "scheme_description": "专为90平米两居室设计的智能家居方案，涵盖智能照明、安防、环境调节等场景。",
            "devices": [
                {"device_type": "light", "device_name": "米家智能LED灯泡", "room": "客厅", "quantity": 4, "reason": "支持语音控制和APP远程操控", "subtotal": 316},
                {"device_type": "light", "device_name": "米家智能LED灯泡", "room": "主卧", "quantity": 2, "reason": "暖光设计，适合睡前氛围", "subtotal": 158},
                {"device_type": "curtain", "device_name": "小米智能窗帘", "room": "客厅", "quantity": 1, "reason": "一键开关，支持日出模式", "subtotal": 499},
                {"device_type": "speaker", "device_name": "小爱音箱Pro", "room": "客厅", "quantity": 1, "reason": "全屋语音控制中枢", "subtotal": 199},
                {"device_type": "sensor", "device_name": "米家温湿度传感器", "room": "客厅", "quantity": 1, "reason": "联动空调自动调节温湿度", "subtotal": 49},
                {"device_type": "camera", "device_name": "小米智能摄像机", "room": "玄关", "quantity": 1, "reason": "门口异常提醒", "subtotal": 199},
                {"device_type": "lock", "device_name": "小米智能门锁", "room": "入户门", "quantity": 1, "reason": "指纹密码多重开锁方式", "subtotal": 999},
                {"device_type": "switch", "device_name": "小米智能开关", "room": "厨房", "quantity": 2, "reason": "手湿时也可安全触控", "subtotal": 178},
            ],
            "total_price": 2597
        }
    }
