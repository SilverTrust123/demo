document.addEventListener('DOMContentLoaded', () => {
    const ctx = document.getElementById('predictionChart').getContext('2d');

    const predictionChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['每 1hr'], // Y 軸標籤
            datasets: [
                {
                    label: '預測生產量',
                    data: [1000], // 預測數據
                    backgroundColor: '#3498db', // 藍色
                    barThickness: 50
                },
                {
                    label: '實際生產量',
                    data: [700], // 實際數據
                    backgroundColor: '#e67e22', // 橘色
                    barThickness: 50
                }
            ]
        },
        options: {
            indexAxis: 'y', // 改為橫向圖表
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom', // 圖例放下方
                    labels: {
                        font: { size: 16, weight: 'bold' }
                    }
                },
                tooltip: { enabled: true }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    grid: { display: false },
                    ticks: { font: { size: 14, weight: 'bold' } }
                },
                y: {
                    grid: { display: false },
                    ticks: { font: { size: 16, weight: 'bold' } }
                }
            }
        }
    });
});