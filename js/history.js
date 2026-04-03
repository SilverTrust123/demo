/**
 * js/history.js - 歷史數據折線圖邏輯
 * 功能：每 10 秒自動從 localStorage 同步主頁傳來的最新數據
 */

let mainChart = null;
let currentMode = 'temp'; // 預設顯示模式

// --- 1. 圖表配置定義 ---
const configs = {
    temp:  { label: 'Temperature 1', color: '#b24b4b', key: 'temp',  unit: '溫度 1 (°C)' },
    humi:  { label: 'Humidity 1',    color: '#e08e45', key: 'humi',  unit: '濕度 1 (%)' },
    temp2: { label: 'Temperature 2', color: '#ff6b6b', key: 'temp2', unit: '溫度 2 (°C)' },
    humi2: { label: 'Humidity 2',    color: '#c08552', key: 'humi2', unit: '濕度 2 (%)' },
    co2:   { label: 'CO2 Concentration', color: '#d4af37', key: 'co2', unit: 'CO2 (ppm)' },
    pm:    { label: 'PM2.5',         color: '#763dc6', key: 'pm',    unit: '污染 (μg/m³)' },
    power: { label: 'Power Usage',   color: '#2d6a4f', key: 'power', unit: '電流 (kW)' }
};

/**
 * 初始化圖表
 */
function initChart() {
    const canvas = document.getElementById('historyChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    mainChart = new Chart(ctx, {
        type: 'line',
        data: { 
            labels: [], 
            datasets: [{ 
                data: [], 
                borderWidth: 3, 
                tension: 0.3,
                fill: false,           // 僅保留折線
                backgroundColor: 'transparent',
                pointRadius: 4,
                pointBackgroundColor: '#fff'
            }] 
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
                duration: 800 // 設定平滑更新動畫
            },
            plugins: { 
                legend: { display: false },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                x: {
                    title: { display: true, text: '時間', font: { size: 14, weight: 'bold' } }
                },
                y: {
                    title: { display: true, text: '數值', font: { size: 14, weight: 'bold' } },
                    beginAtZero: false
                }
            }
        }
    });

    // 第一次載入
    updateChartData(); 
}

/**
 * 核心功能：從 localStorage 讀取最新數據並更新圖表
 */
function updateChartData() {
    const rawData = localStorage.getItem('sensorHistory');
    if (!rawData || !mainChart) return;

    let history;
    try {
        history = JSON.parse(rawData);
    } catch (e) {
        console.error("解析歷史數據失敗", e);
        return;
    }

    const config = configs[currentMode];
    if (!config) return;

    // 更新標籤 (時間軸) 與 數據點
    mainChart.data.labels = history.map(d => d.time || '');
    mainChart.data.datasets[0].data = history.map(d => d[config.key] ?? 0);
    
    // 更新顏色與標題
    mainChart.data.datasets[0].borderColor = config.color;
    mainChart.data.datasets[0].pointBorderColor = config.color;
    mainChart.options.scales.y.title.text = config.unit;

    // 更新 UI 文字標籤
    const labelEl = document.getElementById('current-chart-label');
    if (labelEl) {
        labelEl.innerText = config.label;
        labelEl.style.color = config.color;
    }

    // 執行更新動畫
    mainChart.update();
    console.log(`[History] 圖表已同步最新數據 (${currentMode})`);
}

/**
 * 綁定按鈕切換事件
 */
function bindTabEvents() {
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            const target = e.currentTarget.getAttribute('data-target');
            if (!configs[target]) return;

            // 切換 Active 狀態
            buttons.forEach(b => {
                const colorClasses = Object.keys(configs).map(key => `active-${key}`);
                b.classList.remove('active', ...colorClasses);
            });
            e.currentTarget.classList.add('active', `active-${target}`);

            // 切換模式並立即更新
            currentMode = target;
            updateChartData();
        });
    });
}

// --- 關鍵設定：每 10 秒執行一次數據同步 ---
setInterval(updateChartData, 10000); 

window.addEventListener('load', () => {
    initChart();
    bindTabEvents();
});