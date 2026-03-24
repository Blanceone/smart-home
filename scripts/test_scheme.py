import requests
import json

data = {
    "house_layout": {"total_area": 80, "rooms": [{"room_name": "LR", "room_type": "living_room", "length": 5, "width": 4}]},
    "questionnaire": {"living_status": "owner", "resident_count": 2, "preferred_scenarios": ["lighting"]},
    "preferences": {"budget_min": 1000, "budget_max": 10000}
}

r = requests.post("http://127.0.0.1:8000/api/v1/schemes/generate", json=data)
print("Status:", r.status_code)
result = r.json()
print("Response:", json.dumps(result, indent=2, ensure_ascii=False))
