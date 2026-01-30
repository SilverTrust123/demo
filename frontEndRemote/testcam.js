const API_URL = 'http://192.168.3.253:9090/ComData';
const canvas = document.getElementById('visionCanvas');
const ctx = canvas.getContext('2d');

//解析度
const RAW_W = 640;
const RAW_H = 480;

async function updateVision() {
    try {
        const res = await fetch(API_URL);
        if (!res.ok) throw new Error('Network response was not ok');
        const data = await res.json();

        // 更新 UI 文字資訊
        document.getElementById('devId').innerText = data.deviceId;
        document.getElementById('pCount').innerText = data.personCount;
        document.getElementById('timestamp').innerText = data.timestamp.split('T')[1]; // 只顯示時間
        
        const alarm = document.getElementById('alarmStatus');
        if (data.danger) {
            alarm.innerText = "有人入侵";
            alarm.className = "data-value status-danger";
        } else {
            alarm.innerText = "正常監看中";
            alarm.className = "data-value status-safe";
        }

        // 確保畫布內部繪圖尺寸與後端座標系一致
        if (canvas.width !== RAW_W) canvas.width = RAW_W;
        if (canvas.height !== RAW_H) canvas.height = RAW_H;

        // 執行繪圖
        drawScene(data);

    } catch (err) {
        console.warn("無法連接到後端 API:", err);
        document.getElementById('alarmStatus').innerText = "連線中斷";
    }
    
    // 使用 setTimeout 控製更新頻率 (每 100ms 一次，約 10 FPS)
    setTimeout(updateVision, 100);
}

function drawScene(data) {
    // 每次繪圖前先清空畫布
    ctx.clearRect(0, 0, RAW_W, RAW_H);

    // --- 繪製危險區域 (Danger Zone) ---
    if (data.dangerZone && data.dangerZone.length > 0) {
        ctx.beginPath();
        ctx.moveTo(data.dangerZone[0][0], data.dangerZone[0][1]);
        data.dangerZone.forEach(pt => ctx.lineTo(pt[0], pt[1]));
        ctx.closePath();
        
        // 樣式設定
        ctx.lineWidth = 4;
        ctx.strokeStyle = data.danger ? 'rgba(255, 77, 77, 1)' : 'rgba(255, 255, 0, 0.6)';
        ctx.fillStyle = data.danger ? 'rgba(255, 77, 77, 0.2)' : 'rgba(255, 255, 0, 0.05)';
        ctx.stroke();
        ctx.fill();
    }

    // --- 繪製偵測物件 (Objects) ---
    if (data.objects) {
        data.objects.forEach(obj => {
            const width = obj.x2 - obj.x1;
            const height = obj.y2 - obj.y1;

            // 繪製綠色辨識框
            ctx.strokeStyle = "#00ff88";
            ctx.lineWidth = 2;
            ctx.strokeRect(obj.x1, obj.y1, width, height);

            // 繪製腳部中心點 (footX, footY)
            ctx.fillStyle = "#ff00ff";
            ctx.beginPath();
            ctx.arc(obj.footX, obj.footY, 5, 0, Math.PI * 2);
            ctx.fill();

            // 繪製標籤文字與背景
            ctx.font = "bold 14px Arial";
            const label = obj.className.toUpperCase();
            const textWidth = ctx.measureText(label).width;

            ctx.fillStyle = "#00ff88";
            ctx.fillRect(obj.x1, obj.y1 - 20, textWidth + 10, 20); // 標籤背景

            ctx.fillStyle = "#000";
            ctx.fillText(label, obj.x1 + 5, obj.y1 - 5); // 標籤文字
        });
    }
}

// 啟動監控
updateVision();