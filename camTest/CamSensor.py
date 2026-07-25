import cv2
import numpy as np
import time
import requests
from datetime import datetime
from ultralytics import YOLO
import sys
import os

def get_resource_path(relative_path):
    """ 取得打包後臨時資料夾的路徑或是開發時的相對路徑 """
    if hasattr(sys, '_MEIPASS'):
        return os.path.join(sys._MEIPASS, relative_path)
    return os.path.join(os.path.abspath("."), relative_path)
DEVICE_ID = "CAM"
BACKEND_URL = "http://localhost:9090/camData/"
POLL_INTERVAL = 3

DANGER_POLY = np.array([
    [100, 100],
    [1200, 100],
    [1200, 1200],
    [100, 1200]
])

model = YOLO("yolov8n.pt")
cap = cv2.VideoCapture(1)
last_send_time = 0

if not cap.isOpened():
    print("攝影機開啟失敗")
    exit()

def point_in_polygon(point, poly):
    return cv2.pointPolygonTest(poly, point, False) >= 0


def send_to_backend(payload):
    try:
        r = requests.post(BACKEND_URL, json=payload, timeout=1)
        print(f"[SEND] {payload['deviceId']} danger={payload['danger']}")
    except Exception as e:
        print("[ERROR] 後端連線失敗:", e)

while True:
    ret, frame = cap.read()
    if not ret:
        break

    detected_objects = []
    person_count = 0
    danger_now = False

    results = model(frame, conf=0.4, verbose=False)

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

    cv2.polylines(frame, [DANGER_POLY], True, (0, 0, 255), 2)

    cv2.putText(
        frame,
        f"CAM={DEVICE_ID}  COUNT={person_count}  DANGER={danger_now}",
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
            # "timestamp": datetime.now().isoformat(timespec="seconds"),
            "timestamp":000,
            "danger": danger_now,
            "personCount": person_count,
            "dangerZone": DANGER_POLY.tolist(),
            "objects": detected_objects,
        }
        send_to_backend(payload)
        last_send_time = now

    cv2.imshow("Cam Sensor (Live)", frame)
    if cv2.waitKey(1) & 0xFF == 27:
        break
cap.release()
cv2.destroyAllWindows()
