const API_URL = 'http://192.168.3.253:9090/CamData';

async function fetchPersonCount() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('連線失敗');
        
        const data = await response.json();

        //更新人數文字
        document.getElementById('personCount').innerText = data.personCount;

        //更新警示狀態
        const statusEl = document.getElementById('systemStatus');
        if (data.danger) {
            statusEl.innerText = "狀態：有人入侵";
            statusEl.className = "status-text danger-alert";
        } else {
            statusEl.innerText = "狀態：正常監控中";
            statusEl.className = "status-text";
        }

        //更新時間戳記 (提取 JSON 中的時間)
        const time = data.timestamp.split('T')[1]; 
        document.getElementById('updateTime').innerText = `最後更新時間：${time}`;

    } catch (error) {
        console.error("API Error:", error);
        document.getElementById('systemStatus').innerText = "伺服器連線異常";
    }

    // 每秒更新一次即可 (不需要像畫圖那樣 0.1 秒更新)
    setTimeout(fetchPersonCount, 1000);
}

// 啟動程式
fetchPersonCount();