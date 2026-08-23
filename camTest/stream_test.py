import cv2
import numpy as np
import time
import requests
from datetime import datetime
from ultralytics import YOLO
import sys
import os

from flask import Flask, Response

DEVICE_ID = "CAM"

BACKEND_URL = "http://localhost:9090/camData/"

POLL_INTERVAL = 3

STREAM_PORT = 5000


app = Flask(__name__)



DANGER_POLY = np.array([
    [100, 100],
    [1200, 100],
    [1200, 1200],
    [100, 1200]
])

model = YOLO("yolov8n.pt")

cap = cv2.VideoCapture(0)

last_send_time = 0

latest_frame = None


if not cap.isOpened():
    print("攝影機開啟失敗")
    sys.exit()



def point_in_polygon(point, poly):
    return cv2.pointPolygonTest(poly, point, False) >= 0


def send_to_backend(payload):
    try:
        r = requests.post(
            BACKEND_URL,
            json=payload,
            timeout=1
        )

        print(
            f"[SEND] "
            f"{payload['deviceId']} "
            f"danger={payload['danger']}"
        )

    except Exception as e:
        print("[ERROR] 後端連線失敗:", e)

def generate_frames():
    global latest_frame

    while True:

        if latest_frame is None:
            continue

        # OpenCV frame → JPEG
        ret, buffer = cv2.imencode(
            '.jpg',
            latest_frame,
            [cv2.IMWRITE_JPEG_QUALITY, 80]
        )

        if not ret:
            continue

        frame_bytes = buffer.tobytes()

        # MJPEG
        yield (
            b'--frame\r\n'
            b'Content-Type: image/jpeg\r\n\r\n'
            + frame_bytes
            + b'\r\n'
        )


@app.route('/video_feed')
def video_feed():

    return Response(
        generate_frames(),
        mimetype='multipart/x-mixed-replace; boundary=frame'
    )
def camera_loop():

    global latest_frame
    global last_send_time

    while True:

        ret, frame = cap.read()

        if not ret:
            print("攝影機讀取失敗")
            break

        detected_objects = []

        person_count = 0

        danger_now = False

        results = model(
            frame,
            conf=0.4,
            verbose=False
        )


        for r in results:

            for box in r.boxes:
                if int(box.cls[0]) == 0:

                    person_count += 1

                    x1, y1, x2, y2 = map(
                        int,
                        box.xyxy[0]
                    )

                    footX = (x1 + x2) // 2
                    footY = y2


                    in_danger = point_in_polygon(
                        (footX, footY),
                        DANGER_POLY
                    )


                    if in_danger:

                        danger_now = True

                        color = (
                            0,
                            0,
                            255
                        )

                    else:

                        color = (
                            0,
                            255,
                            0
                        )

                    detected_objects.append({

                        "className": "person",

                        "x1": x1,
                        "y1": y1,

                        "x2": x2,
                        "y2": y2,

                        "footX": footX,
                        "footY": footY

                    })
                    cv2.rectangle(
                        frame,
                        (x1, y1),
                        (x2, y2),
                        color,
                        2
                    )


                    cv2.circle(
                        frame,
                        (footX, footY),
                        5,
                        (255, 0, 0),
                        -1
                    )
        cv2.polylines(
            frame,
            [DANGER_POLY],
            True,
            (0, 0, 255),
            2
        )

        cv2.putText(

            frame,

            f"CAM={DEVICE_ID}  "
            f"COUNT={person_count}  "
            f"DANGER={danger_now}",

            (20, 40),

            cv2.FONT_HERSHEY_SIMPLEX,

            1,

            (0, 0, 255)
            if danger_now
            else
            (0, 255, 0),

            2
        )
        latest_frame = frame.copy()

        now = time.time()

        if now - last_send_time >= POLL_INTERVAL:

            payload = {

                "deviceId": DEVICE_ID,

                "timestamp": 000,

                "danger": danger_now,

                "personCount": person_count,

                "dangerZone":
                    DANGER_POLY.tolist(),

                "objects":
                    detected_objects

            }

            send_to_backend(payload)

            last_send_time = now

        cv2.imshow(
            "Cam Sensor (Live)",
            frame
        )


        if cv2.waitKey(1) & 0xFF == 27:

            break


    cap.release()

    cv2.destroyAllWindows()

if __name__ == "__main__":

    import threading

    # Camera / YOLO 放到背景執行緒
    camera_thread = threading.Thread(
        target=camera_loop,
        daemon=True
    )

    camera_thread.start()


    print("================================")
    print("Camera 啟動")
    print("Video Stream:")
    print(
        f"http://localhost:{STREAM_PORT}/video_feed"
    )
    print("================================")
    app.run(
        host="0.0.0.0",
        port=STREAM_PORT,
        threaded=True
    )