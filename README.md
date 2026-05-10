# Industrial Control & Monitoring System (ICMS) 
[![GitHub Repo](https://img.shields.io/badge/GitHub-Repository-181717?logo=github)](https://github.com/SilverTrust123)
[![GitHub Repo releses](https://img.shields.io/badge/GitHubRepo-LeatestReleases-6f42c1?logo=semantic-release&logoColor=white)](https://github.com/SilverTrust123/demo/archive/refs/tags/V3.1.2.zip)
![Java](https://img.shields.io/badge/Java-Language-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-Backend-6DB33F?logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white)
![IoT](https://img.shields.io/badge/IoT-PLC_Control-blue?logo=arduino&logoColor=white)


## 簡介:
Industrial Control & Monitoring System (ICMS) 是一套基於 Spring Boot 所打造的工業級後端整合平台，專注於整合 PLC 控制、多元感測器資料收集、影像辨識與即時監控，提供穩定、高效且可擴展的中控系統解決方案。本系統採用 分層式系統架構（Controller → Service → Device Layer），將系統功能模組化，實現高內聚、低耦合的設計原則，提升系統可維護性與擴展彈性。系統可即時收集溫濕度、空氣品質、粉塵濃度與電路狀態等多項感測資料，並透過 PLC 模組進行設備控制與狀態回饋，同時整合攝影機物件辨識模組，實現全方位設備監控與環境感知。透過集中化管理平台，使用者可即時監控設備運作狀態、查詢歷史資料、進行設備控制與系統設定，適用於 智慧工廠、工業自動化、環境監測與內網型監控系統 等應用場景，為工業系統提供穩定可靠的後端核心架構。


> 最新版本 version 3.1.0 (關於本本資訊請點)[這裡](https://github.com/SilverTrust123/demo/releases/tag/V3.1.0)

> URL街口相關資訊請參考 [這裡](https://github.com/SilverTrust123/demo/blob/main/doc/url_explain.md)

> URL地圖請直接造訪[這裡](https://htmlpreview.github.io/?https://github.com/SilverTrust123/demoOnly/blob/main/Swagger%20UI.html)

## introduction:

Industrial Control & Monitoring System (ICMS) is an industrial-grade backend platform built with Spring Boot, designed to integrate PLC control, multi-sensor data acquisition, camera-based object detection, and real-time monitoring into a unified management system.The system adopts a layered architecture (Controller → Service → Device Layer) to ensure high modularity, maintainability, and scalability. It collects real-time data from various sensors including temperature, humidity, air quality, particulate matter, and circuit monitoring, while enabling industrial device control through PLC communication modules. In addition, camera-based object detection is integrated to provide enhanced situational awareness and intelligent monitoring capabilities.By providing centralized data processing, device management, and real-time monitoring, ICMS serves as a robust backend solution for smart factories, industrial automation systems, environmental monitoring platforms, and intranet-based industrial control systems, delivering a reliable and scalable core infrastructure for industrial IoT applications.

> latest version 3.1.0 (for version detail plc check here)[here](https://github.com/SilverTrust123/demo/releases/tag/V3.1.0)

> URL detail plz check [here](https://github.com/SilverTrust123/demo/blob/main/doc/url_explain.md)

> for all URL track plz visit [here](https://htmlpreview.github.io/?https://github.com/SilverTrust123/demoOnly/blob/main/Swagger%20UI.html)

   

## 系統特點:

- 系統技術特點（Highlights）

- 採用分層式架構設計（Controller → Service → Device Layer），確保系統高內聚、低耦合，提升可維護性與可擴展性。

- 以模組化方式整合 PLC 控制、多元感測器資料與影像辨識模組，使系統具備高度擴充彈性與設備整合能力。

- 支援即時資料收集與處理機制，能高效處理多設備併發請求並即時回饋監控結果。

- 透過統一資料模型（DTO / SensorData / PLC Mapping）實現異質設備資料的標準化管理與整合。

- 具備集中式設備控制與監控設計，能統一管理設備狀態、系統設定與歷史資料。

- 系統架構設計符合工業內網與 IoT 應用場景，可穩定支援長時間運作與高可靠度需求。

- 歷史資料查詢功能，方便追蹤錯誤

- 全域log

## system heighlight

- Implements layered architecture to achieve clean separation of concerns and high system maintainability.

- Modular integration of PLC, sensor, and vision systems enables scalable industrial device management.

- Designed for real-time data processing and concurrent request handling.

- Standardized data abstraction layer unifies heterogeneous device data.

- Centralized monitoring and control architecture simplifies industrial system management.

- history search function 

- log for every function

### 開發者筆記:

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

### 白話文

我試著用描述的說了一下做了甚麼[請點這裡](https://github.com/SilverTrust123/demo/blob/main/doc/talk/0.md)

-----------------------------------------------

### 如果上面地址失靈請點這裡的URL -> 
https://github.com/SilverTrust123/demo/releases/tag/V2.0.0      
https://github.com/SilverTrust123/demo/blob/main/doc/url_explain.md      
https://htmlpreview.github.io/?https://github.com/SilverTrust123/demoOnly/blob/main/Swagger%20UI.html

-----------------------------------------------

### anything went woring plz try the fellowing URL instead ->
https://github.com/SilverTrust123/demo/releases/tag/V2.0.0     
https://github.com/SilverTrust123/demo/blob/main/doc/url_explain.md     
https://htmlpreview.github.io/?https://github.com/SilverTrust123/demoOnly/blob/main/Swagger%20UI.html  

--------------------------------------------------
## 圖例說明:

### 循環圖

![執行循序圖](<doc/數位雙生控制與 Modbus TCP 執行循序圖.png>)

### 後端分層圖

![後端分層圖](<doc/messageImage_1771861743691.jpg>)
