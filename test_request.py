import requests

url = "http://127.0.0.1:5000/predict"

data = {
    "TaskMI": 123000,
    "TaskSize": 500,
    "Job_Depth": 3,
    "MIPs": 1000,
    "Bandwidth": 1000,
    "VM_Memory": 8192,
    "PES": 4,
    "TaskMI_per_MIPS": 123.0
}

response = requests.post(url, json=data)

print("Status Code:", response.status_code)
print("Response:", response.json())
