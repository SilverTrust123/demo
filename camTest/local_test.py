import cv2
import numpy as np
from ultralytics import YOLO

model = YOLO("yolov8n.pt")

cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

DANGER_POLY = np.array([
    [200, 100],
    [450, 100],
    [450, 350],
    [200, 350]
])

def point_in_polygon(point, poly):
    return cv2.pointPolygonTest(poly, point, False) >= 0

while True:
    ret, frame = cap.read()
    danger_now = False
    results = model(frame, conf=0.5)
    for r in results:
        for box in r.boxes:
            if int(box.cls[0]) == 0:
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                foot = ((x1 + x2)//2, y2)
                if point_in_polygon(foot, DANGER_POLY):
                    danger_now = True
                    color = (0,0,255) 
                else:
                    color = (0,255,0) 

                cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)

    cv2.polylines(frame, [DANGER_POLY], True, (0,0,255), 2)

    cv2.imshow("Local Danger Test", frame)

    if cv2.waitKey(1) & 0xFF == 27:
        break

cap.release()
cv2.destroyAllWindows()
