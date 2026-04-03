/**
 * mainUI.js - 數據監控中心主邏輯
 * 整合功能：
 * 1. 從 global.js 引用 CONFIG.API_BASE 實現全域 IP 管理
 * 2. 自動將即時數據同步至 localStorage (供 history.js 使用)
 * 3. 支援兩組溫濕度、環境品質及電力數據監測
 */

document.addEventListener("DOMContentLoaded", () => {
    
    // --- 1. 配置與 API 端點 ---
    const ENDPOINTS = {
        ALL: `${CONFIG.API_BASE}/allData/allSenosrData`,
        TH: `${CONFIG.API_BASE}/temperatureAndHumidityData/`,
        CIRCUIT: `${CONFIG.API_BASE}/circuitData`,
        CO2_DATA: `${CONFIG.API_BASE}/airQualityData/`,      
        PM25_DATA: `${CONFIG.API_BASE}/airParticulatesData/` 
    };

    let gaugeConfigs = {};

    // --- 2. 核心數據處理 ---

    // 數據抓取函式
    async function fetchJson(url) {
        try {
            const res = await fetch(url, { 
                method: 'GET', 
                headers: { 'Accept': 'application/json' },
                signal: AbortSignal.timeout(2000) 
            });
            if (res.ok) return await res.json();
        } catch (e) {
            console.warn(`請求失敗: ${url}`);
        }
        return null;
    }

    /**
     * 將即時數據儲存至 localStorage 給歷史圖表使用
     * 確保 Key 值與 history.js 的 configs 完全對應
     */
    function saveToHistory(data) {
        // 取得現有紀錄，若無則建立空陣列
        let history = JSON.parse(localStorage.getItem('sensorHistory') || '[]');
        
        const newEntry = {
            // 產生目前時間 (格式 14:30:05)
            time: new Date().toLocaleTimeString('zh-TW', { 
                hour12: false, 
                hour: '2-digit', 
                minute: '2-digit', 
                second: '2-digit' 
            }),
            temp:  data.temp1,  // 對應 history.js 的 key: 'temp'
            humi:  data.humi1,  // 對應 history.js 的 key: 'humi'
            temp2: data.temp2, // 對應 history.js 的 key: 'temp2'
            humi2: data.humi2, // 對應 history.js 的 key: 'humi2'
            co2:   data.co2,    // 對應 history.js 的 key: 'co2'
            pm:    data.pm,     // 對應 history.js 的 key: 'pm'
            power: data.power   // 對應 history.js 的 key: 'power'
        };

        history.push(newEntry);

        // 限制儲存長度 (例如保留最新 30 筆)，避免瀏覽器儲存空間爆滿
        if (history.length > 30) history.shift();

        localStorage.setItem('sensorHistory', JSON.stringify(history));
    }

    // --- 3. UI 工具函式 ---

    function updateLampStatus(lampId, isOnline) {
        const el = document.getElementById(lampId);
        if (!el) return;
        if (isOnline) {
            el.classList.remove('lamp-red');
            el.classList.add('lamp-green');
        } else {
            el.classList.remove('lamp-green');
            el.classList.add('lamp-red');
        }
    }

    function setupGauge(id) {
        const ring = document.getElementById(id);
        if (!ring) return null;
        const length = ring.getTotalLength();
        ring.style.strokeDasharray = length;
        ring.style.strokeDashoffset = length; 
        ring.style.transition = "stroke-dashoffset 1.5s cubic-bezier(0.4, 0, 0.2, 1)";
        return length;
    }

    function updateGauge(id, value, max, length) {
        const ring = document.getElementById(id);
        if (!ring || !length) return;
        const ratio = Math.max(0, Math.min(value / max, 1));
        const offset = length * (1 - ratio);
        ring.style.strokeDashoffset = offset;
    }

    function updateTextBySelector(selector, val, unit) {
        const displayEl = document.querySelector(selector);
        if (displayEl) {
            const formattedVal = typeof val === 'number' ? val.toFixed(1) : "0.0";
            displayEl.innerHTML = `${formattedVal}${unit}`;
        }
    }

    function updateStatusUI(isConnected) {
        const connEl = document.getElementById("conn-status");
        const runEl = document.getElementById("run-status");
        const statusText = isConnected ? "yes" : "no";
        const statusClass = isConnected ? "v-green" : "v-red";

        [connEl, runEl].forEach(el => {
            if (el) {
                el.innerText = statusText;
                el.className = statusClass;
            }
        });
    }

    // --- 4. 初始化與主循環 ---
    async function initDashboard() {
        // 配置儀表板對應的圓環 ID 與 CSS 選擇器
        gaugeConfigs = {
            temp1: { id: "temp-fill-1", len: setupGauge("temp-fill-1"), max: 50,   unit: "°C", selector: ".area-lt .gauge-box:nth-of-type(1) .gauge-value" },
            humi1: { id: "humi-fill-1", len: setupGauge("humi-fill-1"), max: 100,  unit: "%",  selector: ".area-lt .gauge-box:nth-of-type(2) .gauge-value" },
            temp2: { id: "temp-fill-2", len: setupGauge("temp-fill-2"), max: 50,   unit: "°C", selector: ".area-lb .gauge-box:nth-of-type(1) .gauge-value" },
            humi2: { id: "humi-fill-2", len: setupGauge("humi-fill-2"), max: 100,  unit: "%",  selector: ".area-lb .gauge-box:nth-of-type(2) .gauge-value" },
            co2:   { id: "co2-fill",    len: setupGauge("co2-fill"),    max: 1000, unit: "<small>ppm</small>", selector: ".area-mid-b .gauge-box:nth-of-type(1) .gauge-value" },
            pm:    { id: "pm-fill",     len: setupGauge("pm-fill"),     max: 100,  unit: "<small>μg</small>",  selector: ".area-mid-b .gauge-box:nth-of-type(2) .gauge-value" }, 
            power: { id: "power-fill",  len: setupGauge("power-fill"),  max: 500,  unit: "<small>kW</small>",  selector: ".area-power .gauge-value" }
        };

        const refreshData = async () => {
            const [dAll, dTH, dCircuit, dCO2, dPM25] = await Promise.all([
                fetchJson(ENDPOINTS.ALL), fetchJson(ENDPOINTS.TH),
                fetchJson(ENDPOINTS.CIRCUIT), fetchJson(ENDPOINTS.CO2_DATA),
                fetchJson(ENDPOINTS.PM25_DATA)
            ]);

            const isAnyOk = !!(dAll || dTH || dCircuit || dCO2 || dPM25);
            updateStatusUI(isAnyOk);

            // 資料格式正規化 (處理 Array 或單一 Object)
            const thArr = Array.isArray(dTH) ? dTH : [dTH];
            const cir = Array.isArray(dCircuit) ? dCircuit[0] : dCircuit;
            const co2Obj = Array.isArray(dCO2) ? dCO2[0] : dCO2;
            const pmObj = Array.isArray(dPM25) ? dPM25[0] : dPM25;

            // 更新燈號
            updateLampStatus("lamp-1", !!(thArr[0] || dAll));
            updateLampStatus("lamp-2", !!(thArr[1]));
            updateLampStatus("lamp-co2", !!(co2Obj || dAll));
            updateLampStatus("lamp-pm", !!(pmObj || dAll));
            updateLampStatus("lamp-power", !!(cir || dAll));

            if (isAnyOk) {
                // 整理統一的數據物件
                const finalData = {
                    temp1: thArr[0]?.temperature ?? dAll?.temperature ?? 0,
                    humi1: thArr[0]?.humidity ?? dAll?.humidity ?? 0,
                    temp2: thArr[1]?.temperature ?? 0, 
                    humi2: thArr[1]?.humidity ?? 0,
                    power: cir?.power ?? dAll?.power ?? 0,
                    co2:   co2Obj?.airPollution ?? dAll?.co2Value ?? 0,
                    pm:    pmObj?.pm2_5 ?? dAll?.pm25 ?? 0
                };

                // 1. 同步存入 localStorage 供歷史圖表讀取
                saveToHistory(finalData);

                // 2. 更新畫面上的所有儀表與文字
                Object.keys(gaugeConfigs).forEach(key => {
                    const cfg = gaugeConfigs[key];
                    updateTextBySelector(cfg.selector, finalData[key], cfg.unit);
                    if (cfg.len) {
                        updateGauge(cfg.id, finalData[key], cfg.max, cfg.len);
                    }
                });
            }
        };

        // 啟動循環重新整理
        setTimeout(refreshData, 300);
        setInterval(refreshData, 3000); 
    }

    // 輸送帶狀態燈動畫邏輯
    let currentStep = 0;
    setInterval(() => {
        const deviceItems = document.querySelectorAll('.device-item');
        deviceItems.forEach((item, index) => {
            const lamp = item.querySelector('.lamp');
            if (lamp) {
                const isActive = (index === currentStep);
                lamp.innerText = isActive ? "○" : "×";
                lamp.className = isActive ? "lamp lamp-ok" : "lamp lamp-fail";
            }
        });
        currentStep = (currentStep + 1) % (deviceItems.length || 1);
    }, 2000);

    initDashboard();
});