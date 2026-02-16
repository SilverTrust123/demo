### Industrial Control & Monitoring System (ICMS) 

## 簡介:
Industrial Control & Monitoring System (ICMS) 是一套基於 Spring Boot 所打造的工業級後端整合平台，專注於整合 PLC 控制、多元感測器資料收集、影像辨識與即時監控，提供穩定、高效且可擴展的中控系統解決方案。

本系統採用 分層式系統架構（Controller → Service → Device Layer），將系統功能模組化，實現高內聚、低耦合的設計原則，提升系統可維護性與擴展彈性。系統可即時收集溫濕度、空氣品質、粉塵濃度與電路狀態等多項感測資料，並透過 PLC 模組進行設備控制與狀態回饋，同時整合攝影機物件辨識模組，實現全方位設備監控與環境感知。

透過集中化管理平台，使用者可即時監控設備運作狀態、查詢歷史資料、進行設備控制與系統設定，適用於 智慧工廠、工業自動化、環境監測與內網型監控系統 等應用場景，為工業系統提供穩定可靠的後端核心架構。

> version 1.0.0

## introduction:
Industrial Control & Monitoring System (ICMS) is an industrial-grade backend platform built with Spring Boot, designed to integrate PLC control, multi-sensor data acquisition, camera-based object detection, and real-time monitoring into a unified management system.

The system adopts a layered architecture (Controller → Service → Device Layer) to ensure high modularity, maintainability, and scalability. It collects real-time data from various sensors including temperature, humidity, air quality, particulate matter, and circuit monitoring, while enabling industrial device control through PLC communication modules. In addition, camera-based object detection is integrated to provide enhanced situational awareness and intelligent monitoring capabilities.

By providing centralized data processing, device management, and real-time monitoring, ICMS serves as a robust backend solution for smart factories, industrial automation systems, environmental monitoring platforms, and intranet-based industrial control systems, delivering a reliable and scalable core infrastructure for industrial IoT applications.

> version 1.0.0

# 開發者筆記:
> 目前已完成:
- 完成基礎系統架構
- Controller sevice sensor 邏輯獨立拆分
- 加入 logging 與錯誤處理
- 有加了資料過時卻任
- silaing window 平滑數據 就可以消除一些突發的雜訊阿之類的
- 拆分邏輯
- 連線測試
- 邏輯分層
- 可以做溫溼度等等的預測