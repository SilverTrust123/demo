import cv2
import numpy as np
import os
import time
import requests
from datetime import datetime
from ultralytics import YOLO

DEVICE_ID = "CAM"
IMAGE_FOLDER = "./images_20260508"
BACKEND_URL = "http://192.168.3.253:9090/CamData"

POLL_INTERVAL = 1  
DANGER_POLY = np.array([
    [0, 1200],
    [1200, 1200],
    [1200, 600],
    [0, 600]
])

model = YOLO("yolov8n.pt")
last_send_time = 0

def point_in_polygon(point, poly):
    return cv2.pointPolygonTest(poly, point, False) >= 0


def send_to_backend(payload):
    try:
        r = requests.post(BACKEND_URL, json=payload, timeout=1)
        print(f"[SEND] device={payload['deviceId']} status={r.status_code}")
    except Exception as e:
        print("[ERROR] 後端連線失敗:", e)

image_files = [
    f for f in os.listdir(IMAGE_FOLDER)
    if f.lower().endswith((".jpg", ".png"))
]

if not image_files:
    print("images 資料夾內沒有圖片")
    exit()

for filename in image_files:
    frame = cv2.imread(os.path.join(IMAGE_FOLDER, filename))
    if frame is None:
        continue

    detected_objects = []
    person_count = 0
    danger_now = False
    results = model(frame, conf=0.4)

    for r in results:
        for box in r.boxes:
            if int(box.cls[0]) == 0:
                person_count += 1

                x1, y1, x2, y2 = map(int, box.xyxy[0])
                footX = (x1 + x2) // 2
                footY = y2

                in_danger = point_in_polygon((footX, footY), DANGER_POLY)
                if in_danger:
                    danger_now = True
                    color = (0, 0, 255)
                else:
                    color = (0, 255, 0)

                detected_objects.append({
                    "className": "person",
                    "x1": x1,
                    "y1": y1,
                    "x2": x2,
                    "y2": y2,
                    "footX": footX,
                    "footY": footY
                })
                cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)
                cv2.circle(frame, (footX, footY), 5, (255, 0, 0), -1)

    cv2.polylines(frame, [DANGER_POLY], True, (255, 0, 255), 2)

    cv2.putText(
        frame,
        f"DEVICE={DEVICE_ID}  COUNT={person_count}  DANGER={danger_now}",
        (20, 40),
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (0, 0, 255) if danger_now else (0, 255, 0),
        2
    )
    now = time.time()
    if now - last_send_time >= POLL_INTERVAL:
        payload = {
            "deviceId": DEVICE_ID,
            "timestamp": datetime.now().isoformat(timespec="seconds"),
            "danger": danger_now,
            "personCount": person_count,
            "dangerZone": DANGER_POLY.tolist(),
            "objects": detected_objects
        }
        send_to_backend(payload)
        last_send_time = now

    cv2.imshow("Cam Sensor Test", frame)
    cv2.waitKey(0)

cv2.destroyAllWindows()
