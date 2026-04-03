document.addEventListener('DOMContentLoaded', () => {
    const boardContent = document.querySelector('.board-content');
    const controlRows = document.querySelectorAll('.control-row');
    
    const WRITE_URL = "http://192.168.3.110:9090/plc/writeDPoint";
    const READ_URL = "http://192.168.3.110:9090/plc/AllDPointData";

    // 動態建立確認按鈕列
    const confirmBar = document.createElement('div');
    confirmBar.className = 'floating-confirm-bar';
    confirmBar.innerHTML = `
        <span class="confirm-text">⚠️ 數值已變更</span>
        <button class="confirm-btn">確認儲存 (SET)</button>
    `;
    boardContent.appendChild(confirmBar);
    const confirmBtn = confirmBar.querySelector('.confirm-btn');

    const controllers = [];
    // 依序對應 D 點，請確保順序與 HTML 一致
    const paramNames = ["T14", "T0"]; 

    controlRows.forEach((row, index) => {
        const timeDisplay = row.querySelector('.time-box');
        const minusBtn = row.querySelector('.control-adjust button:first-child');
        const plusBtn = row.querySelector('.control-adjust button:last-child');
        
        // ✨ 修正 1：直接從 HTML 讀取預設秒數作為起始值，防止變空白 ✨
        let rawText = timeDisplay.textContent.trim().replace('sec', '');
        let initialVal = parseFloat(rawText) || 0.0; 

        // 格式化初始顯示
        timeDisplay.textContent = `${initialVal.toFixed(1)} sec`;

        const ctrl = {
            param: paramNames[index],
            original: initialVal, // 基準值
            current: initialVal,  // 當前值
            display: timeDisplay,
            update: (val) => {
                ctrl.current = val;
                timeDisplay.textContent = `${ctrl.current.toFixed(1)} sec`;
                checkChanges();
            },
            commit: () => {
                ctrl.original = ctrl.current;
            }
        };

        minusBtn.addEventListener('click', () => {
            // 確保每次加減都是基於當前顯示的值
            let nextVal = Math.round((ctrl.current - 0.5) * 10) / 10;
            if (nextVal >= 0) ctrl.update(nextVal);
        });

        plusBtn.addEventListener('click', () => {
            let nextVal = Math.round((ctrl.current + 0.5) * 10) / 10;
            if (nextVal <= 60) ctrl.update(nextVal); // 假設上限 60 秒
        });

        controllers.push(ctrl);
    });

    function checkChanges() {
        const hasChanged = controllers.some(c => c.current !== c.original);
        if (hasChanged) {
            confirmBar.classList.add('show');
            controllers.forEach(c => {
                c.display.style.color = (c.current !== c.original) ? "#ffa500" : "#ffffff";
            });
        } else {
            confirmBar.classList.remove('show');
        }
    }

    // ✨ 修正 2：異步讀取 API，僅在成功獲取資料時才更新畫面上數值 ✨
    async function fetchLatestData() {
        try {
            const response = await fetch(READ_URL);
            if (!response.ok) return;
            const data = await response.json();
            
            controllers.forEach(c => {
                if (data[c.param] !== undefined) {
                    const valFromPLC = data[c.param] / 10; 
                    c.original = valFromPLC;
                    c.current = valFromPLC;
                    c.display.textContent = `${valFromPLC.toFixed(1)} sec`;
                    c.display.style.color = "#ffffff";
                }
            });
        } catch (err) {
            console.log("使用預設 HTML 數值 (PLC 未連線)");
        }
    }

    confirmBtn.addEventListener('click', async () => {
        const changedItems = controllers.filter(c => c.current !== c.original);
        try {
            for (let item of changedItems) {
                const payload = {
                    param: item.param,
                    value: Math.round(item.current * 10)
                };
                await fetch(WRITE_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }
            alert("PLC 更新成功！");
            changedItems.forEach(c => c.commit());
            checkChanges();
        } catch (err) {
            alert("更新失敗");
        }
    });

    fetchLatestData();
});