import cv2
import numpy as np
import os
from ultralytics import YOLO

model = YOLO("yolov8n.pt")

DANGER_POLY = np.array([
    [100, 100],
    [1200, 100],
    [1200, 1200],
    [100, 1200]
])

def point_in_polygon(point, poly):
    return cv2.pointPolygonTest(poly, point, False) >= 0

image_folder = "images"
image_files = [f for f in os.listdir(image_folder) if f.endswith((".jpg", ".png"))]

for img_file in image_files:
    img_path = os.path.join(image_folder, img_file)
    frame = cv2.imread(img_path)

    danger_now = False

    results = model(frame, conf=0.5)
    for r in results:
        for box in r.boxes:
            if int(box.cls[0]) == 0:  # 0 = person
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                foot = ((x1 + x2)//2, y2)

                if point_in_polygon(foot, DANGER_POLY):
                    danger_now = True
                    color = (0,0,255) 
                else:
                    color = (0,255,0) 

                cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)

    cv2.polylines(frame, [DANGER_POLY], True, (0,0,255), 2)

    status = "DANGER" if danger_now else "SAFE"
    cv2.putText(frame, f"Status: {status}", (10,30),
                cv2.FONT_HERSHEY_SIMPLEX, 1, (0,0,255), 2)

    cv2.imshow("Photo Test", frame)
    cv2.waitKey(0)

cv2.destroyAllWindows()
