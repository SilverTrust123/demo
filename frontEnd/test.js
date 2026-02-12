/**
 * 測試用的傳送函式
 * @param {boolean} isOn - 傳入 true 或 false
 */
async function sendTestSignal(isOn) {
    const API_URL = 'http://192.168.0.110:9090/plc/writeMPoint'; 

    const payload = {
        device:"TEST",
        value: isOn 
    };

    // 發送請求
    fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (response.ok) {
            console.log("✅ 指令已送達後端 IP 192.168.3.220");
            document.getElementById('status').innerText = "發送成功";
        } else {
            console.error("❌ 後端收到指令但報錯，代碼：", response.status);
            document.getElementById('status').innerText = "伺服器報錯";
        }
    })
    .catch(error => {
        // 電腦連不到 192.168.3.220
        console.error("網路連線失敗:", error);
        document.getElementById('status').innerText = "連線失敗，請檢查網路線";
    });
}