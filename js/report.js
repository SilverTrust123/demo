// js/report.js

document.addEventListener('DOMContentLoaded', () => {
    const downloadBtn = document.getElementById('download-btn');
    const reportSelect = document.getElementById('report-select');

    downloadBtn.addEventListener('click', () => {
        const selectedValue = reportSelect.value;

        if (!selectedValue) {
            alert('請先選擇一份報表！');
            return;
        }

        // 模擬下載動作
        console.log('準備下載檔案：' + selectedValue);
        
        // 實際下載邏輯：
        // window.location.href = 'download/path/' + selectedValue;
        
        alert('正在為您下載：' + selectedValue);
    });
});