const API_URL = 'http://192.168.3.253:9090/CamData';

async function fetchPersonCount() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('連線失敗');
        
        const data = await response.json();
        console.log("偵錯用 - 完整資料內容:", data); 
        const count = data.personCount ?? data.person_count ?? data.count ?? data.PersonCount ?? 0;
        
        // 更新 UI
        document.getElementById('personCount').innerText = count;

        // 更新狀態
        const statusEl = document.getElementById('systemStatus');
        const isDanger = data.danger ?? data.is_danger ?? false;

        if (isDanger) {
            statusEl.innerText = "狀態：有人入侵";
            statusEl.className = "status-text danger-alert";
        } else {
            statusEl.innerText = "狀態：正常監控中";
            statusEl.className = "status-text";
        }

        // 更新更新時間
        const timeStr = data.timestamp ? data.timestamp.split('T')[1] : "N/A";
        document.getElementById('updateTime').innerText = `最後更新時間：${timeStr}`;

    } catch (error) {
        console.error("API Error:", error);
        document.getElementById('systemStatus').innerText = "伺服器連線異常";
        document.getElementById('personCount').innerText = "--";
    }

    setTimeout(fetchPersonCount, 1000);
}

fetchPersonCount();