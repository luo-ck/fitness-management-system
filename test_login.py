import requests
import json

# 教练登录接口
url = "http://localhost:8080/api/auth/coach/login"

# 登录数据
login_data = {
    "username": "老牛",
    "passwordHash": "12345678"
}

print(f"发送登录请求到: {url}")
print(f"请求数据: {json.dumps(login_data, ensure_ascii=False)}")

# 发送POST请求，明确指定UTF-8编码
response = requests.post(
    url,
    json=login_data,
    headers={"Content-Type": "application/json; charset=utf-8"},
    timeout=10
)

print(f"\n响应状态码: {response.status_code}")
print(f"响应头: {dict(response.headers)}")
print(f"响应内容: {response.text}")

# 检查响应状态
if response.status_code == 200:
    print("\n✅ 登录成功！")
    # 解析响应内容
    response_data = response.json()
    print(f"Token: {response_data.get('token')}")
    print(f"Coach info: {json.dumps(response_data.get('coach'), ensure_ascii=False)}")
else:
    print(f"\n❌ 登录失败，状态码: {response.status_code}")