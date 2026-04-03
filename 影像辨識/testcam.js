const API_URL = 'http://192.168.3.253:9090/CamData';

async function fetchPersonCount() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('連線失敗');
        
        const data = await response.json();
        console.log("偵錯用 - 完整資料內容:", data); 

        // 先檢查是不是陣列，有資料再抓第一筆
        const first = Array.isArray(data) && data.length > 0 ? data[0] : null;

        const count = first?.personCount ?? 0;
        document.getElementById('personCount').innerText = count;

        const statusEl = document.getElementById('systemStatus');
        const isDanger = first?.danger ?? false;

        if (isDanger) {
            statusEl.innerText = "狀態：有人入侵";
            statusEl.className = "status-text danger-alert";
        } else {
            statusEl.innerText = "狀態：正常監控中";
            statusEl.className = "status-text";
        }

        const timeStr = first?.timestamp ? first.timestamp.split('T')[1] : "N/A";
        document.getElementById('updateTime').innerText = `最後更新時間：${timeStr}`;

    } catch (error) {
        console.error("API Error:", error);
        document.getElementById('systemStatus').innerText = "伺服器連線異常";
        document.getElementById('personCount').innerText = "--";
    }

    setTimeout(fetchPersonCount, 1000);
}

fetchPersonCount();