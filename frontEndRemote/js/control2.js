document.addEventListener('DOMContentLoaded', () => {
    // ===== 1. 數值加減與 Reset 邏輯 =====
    const controlRows = document.querySelectorAll('.control-row');
    const rowControllers = [];

    controlRows.forEach((row) => {
        const minusBtn = row.querySelector('.control-adjust button:first-child');
        const plusBtn = row.querySelector('.control-adjust button:last-child');
        const timeDisplay = row.querySelector('.time-box');
        
        // 取得初始 HTML 設定的數值
        const defaultValue = parseFloat(timeDisplay.textContent);
        let currentValue = defaultValue;

        const updateDisplay = () => {
            timeDisplay.textContent = `${currentValue.toFixed(1)} sec`;
        };

        minusBtn.addEventListener('click', () => {
            if (currentValue > 0.5) {
                currentValue = Math.round((currentValue - 0.5) * 10) / 10;
                updateDisplay();
            }
        });

        plusBtn.addEventListener('click', () => {
            if (currentValue < 10) {
                currentValue = Math.round((currentValue + 0.5) * 10) / 10;
                updateDisplay();
            }
        });

        // 儲存重置函式
        rowControllers.push({
            reset: () => {
                currentValue = defaultValue;
                updateDisplay();
            }
        });
    });

    // ===== 2. 分頁切換邏輯 (藍色箭頭) =====
    const funcGroups = document.querySelectorAll('.func-group');
    const nextPageBtn = document.getElementById('nextPageBtn');
    let currentPageIndex = 0;

    if (nextPageBtn && funcGroups.length > 1) {
        nextPageBtn.addEventListener('click', () => {
            // 隱藏當前頁面
            funcGroups[currentPageIndex].style.display = 'none';
            
            // 計算下一頁索引 (循環)
            currentPageIndex = (currentPageIndex + 1) % funcGroups.length;
            
            // 顯示下一頁
            funcGroups[currentPageIndex].style.display = 'flex';
            
            // 簡單的點擊動畫回饋
            nextPageBtn.style.transform = 'scale(0.8)';
            setTimeout(() => { nextPageBtn.style.transform = 'scale(1)'; }, 100);
        });
    }

    // ===== 3. 全局 Reset 按鈕綁定 =====
    const resetBtn = document.querySelector('.machine-menu .menu-item:last-child');
    if (resetBtn) {
        resetBtn.addEventListener('click', (e) => {
            e.preventDefault(); 
            rowControllers.forEach(controller => controller.reset());
        });
    }

    // ===== 4. 按鈕點擊縮放動畫效果 =====
    const allInteractiveElements = document.querySelectorAll('.control-adjust button, .menu-item, .arrow-btn');
    allInteractiveElements.forEach(el => {
        el.addEventListener('mousedown', () => el.style.transform = 'scale(0.9)');
        el.addEventListener('mouseup', () => el.style.transform = 'scale(1)');
        el.addEventListener('mouseleave', () => el.style.transform = 'scale(1)');
    });
});