document.addEventListener('DOMContentLoaded', () => {
    const machineImg = document.getElementById('machineImg');
    const machineDesc = document.getElementById('machineDesc');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const navIcons = document.querySelectorAll('.nav-icon');

    const machineData = [
        { img: '../picture/realmachine1.png', desc: '輸送帶 1：<br>輸送工件到紅外線感測器前辨識是否為金屬品' },
        { img: '../picture/realmachine2.png', desc: '龍門機械臂：<br>當產品為金屬品時龍門機械臂啟動夾取工件到輸送帶2' },
        { img: '../picture/realmachine4.png', desc: '輸送帶 2：<br>當龍門機械臂放置金屬工件時輸送帶2將金屬工件輸送到旋轉機械臂前' },
        { img: '../picture/realmachine3.png', desc: '旋轉缸機械臂：<br>當輸送帶2將工件輸送至機械臂時向下吸取將工件放置成品區' },
        { img: '../picture/realmachine5.png', desc: '滑台缸機械臂：<br>當判斷工件為非金屬時機械臂啟動夾取非金屬工件到放置區' }
    ];

    let currentIndex = 0;

const updateDisplay = (index) => {
    currentIndex = index;
    // 更新主圖與文字
    machineImg.src = machineData[index].img;
    machineDesc.innerHTML = machineData[index].desc;

    // --- 新增：放大文字並調整樣式 ---
    machineDesc.style.fontSize = "30px";      // 設定字體大小 (可依需求調整，如 28px 或 1.5rem)
    machineDesc.style.fontWeight = "bold";    // 加粗字體讓它更醒目
    machineDesc.style.lineHeight = "1.5";     // 增加行高，避免字數多時太擁擠
    machineDesc.style.color = "var(--text-color, #333)"; // 確保符合你剛設定的主題顏色
    // ----------------------------

    // 更新小圖示透明度 (選中的變亮)
    navIcons.forEach((icon, i) => {
        icon.style.opacity = (i === index) ? "1" : "0.5";
        icon.style.borderBottom = (i === index) ? "2px solid #555" : "none";
    });
};

    // 綁定箭頭事件
    nextBtn.addEventListener('click', () => {
        let index = (currentIndex + 1) % machineData.length;
        updateDisplay(index);
    });

    prevBtn.addEventListener('click', () => {
        let index = (currentIndex - 1 + machineData.length) % machineData.length;
        updateDisplay(index);
    });

    // 綁定小圖示點擊事件
    navIcons.forEach((icon) => {
        icon.addEventListener('click', () => {
            const index = parseInt(icon.getAttribute('data-index'));
            updateDisplay(index);
        });
    });

    // 初始化顯示第一筆
    updateDisplay(0);
});