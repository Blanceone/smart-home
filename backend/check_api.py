import httpx
import asyncio

async def check_apis():
    async with httpx.AsyncClient() as client:
        print("=== /api/v1/products ===")
        r = await client.get('http://8.137.174.58/api/v1/products', timeout=10)
        print(f"Status: {r.status_code}")
        data = r.json()
        print(f"Keys: {list(data.keys())}")
        print(f"data type: {type(data.get('data'))}")
        if isinstance(data.get('data'), dict):
            print(f"data keys: {list(data.get('data', {}).keys())}")
        print()

        print("=== /api/v1/houses ===")
        r = await client.get('http://8.137.174.58/api/v1/houses', timeout=10)
        print(f"Status: {r.status_code}")
        data = r.json()
        print(f"Keys: {list(data.keys())}")
        print(f"data type: {type(data.get('data'))}")
        if isinstance(data.get('data'), dict):
            print(f"data keys: {list(data.get('data', {}).keys())}")
        print()

        print("=== /api/v1/categories ===")
        r = await client.get('http://8.137.174.58/api/v1/categories', timeout=10)
        print(f"Status: {r.status_code}")
        data = r.json()
        print(f"Categories: {[c.get('category_name') for c in data.get('data', [])]}")

asyncio.run(check_apis())
