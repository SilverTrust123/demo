document.addEventListener('DOMContentLoaded', () => {
    const btnToday = document.getElementById('btn-today');
    const btnWeek = document.getElementById('btn-week');
    const btnMonth = document.getElementById('btn-month');
    const btnCustom = document.getElementById('btn-custom');
    
    const startPicker = document.getElementById('custom-start-picker');
    const endPicker = document.getElementById('custom-end-picker');
    
    const dateGroup = document.getElementById('date-display-group');
    const startDisplay = document.getElementById('display-date-start');
    const endDisplay = document.getElementById('display-date-end');

    // 格式化函數：將 2026-03-07 轉為 2026/03/07
    const formatDate = (dateObj) => {
        const y = dateObj.getFullYear();
        const m = String(dateObj.getMonth() + 1).padStart(2, '0');
        const d = String(dateObj.getDate()).padStart(2, '0');
        return `${y}/${m}/${d}`;
    };

    const setRange = (startStr, endStr) => {
        startDisplay.value = startStr;
        endDisplay.value = endStr;
        dateGroup.classList.remove('hidden');
    };

    // 本日：3/7 ~ 3/7
    btnToday.addEventListener('click', () => {
        const today = new Date();
        const dateStr = formatDate(today);
        setRange(dateStr, dateStr);
    });

    // 本周：週一 ~ 今天
    btnWeek.addEventListener('click', () => {
        const now = new Date();
        const day = now.getDay() || 7; // 週日設為 7
        const monday = new Date(now);
        monday.setDate(now.getDate() - day + 1);
        setRange(formatDate(monday), formatDate(now));
    });

    btnMonth.addEventListener('click', () => {
        const now = new Date();
        // 強制設定日期為 1 號，避免偏移到上個月
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
        setRange(formatDate(firstDay), formatDate(now));
    });

    // 自訂：連續選取邏輯
    btnCustom.addEventListener('click', () => {
        startPicker.showPicker(); 
    });

    startPicker.addEventListener('change', () => {
        if (startPicker.value) {
            // 選完開始日期，自動彈出結束日期選擇
            endPicker.showPicker();
        }
    });

    endPicker.addEventListener('change', () => {
        if (startPicker.value && endPicker.value) {
            const s = new Date(startPicker.value);
            const e = new Date(endPicker.value);
            
            if (s > e) {
                alert("開始日期不能晚於結束日期");
                return;
            }
            // 將 input date 的 YYYY-MM-DD 轉為 YYYY/MM/DD
            setRange(startPicker.value.replace(/-/g, '/'), endPicker.value.replace(/-/g, '/'));
        }
    });
});