/**
 * ProductLine 頁面控制邏輯
 * 功能：
 * 1. 定義金屬與非金屬路徑
 * 2. 模擬機台運作步驟，並更新對應燈號
 * 3. 右側看板顯示目前步驟，並將已跑過的步驟維持顯示「○」
 */

const states = {
    "S1": { light: 0, text: "S1 等待物料" },
    "S10": { light: 1, text: "S10 龍門向右預位" },
    "S11": { light: 1, text: "S11 龍門向下預位" },
    "S12": { light: 1, text: "S12 夾持工作, 龍門向上" },
    "S13": { light: 1, text: "S13 龍門向左" },
    "S14": { light: 1, text: "S14 龍門向下" },
    "S15": { light: 1, text: "S15 放開夾爪" },
    "S16": { light: 1, text: "S16 龍門向上" },
    "S17": { light: 2, text: "S17 輸送帶2" },
    "S18": { light: 3, text: "S18 轉臂向下" },
    "S19": { light: 3, text: "S19 吸附工件" },
    "S20": { light: 3, text: "S20 吸盤向上" },
    "S21": { light: 3, text: "S21 吸盤旋轉向下" },
    "S22": { light: 3, text: "S22 解真空" },
    "S23": { light: 3, text: "S23 吸盤向上" },
    "S24": { light: 3, text: "S24 吸盤歸位" },
    "S30": { light: 4, text: "S30 滑台向左" },
    "S31": { light: 4, text: "S31 滑台向下" },
    "S32": { light: 4, text: "S32 夾持工件" },
    "S33": { light: 4, text: "S33 滑台向上" },
    "S34": { light: 4, text: "S34 滑台向右" },
    "S35": { light: 4, text: "S35 滑台向下" },
    "S36": { light: 4, text: "S36 放開工件" },
    "S37": { light: 4, text: "S37 滑台向上" }
};

// 路徑定義
const metalPath = ["S1", "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24"];
const nonMetalPath = ["S1", "S30", "S31", "S32", "S33", "S34", "S35", "S36", "S37"];

let currentStep = 0;
let isMetal = true; 
let currentPath = metalPath;

// 用於記錄目前工件「已經跑過」的步驟 key
let completedSteps = new Set(); 

const lights = document.querySelectorAll('.indicator-light');
const boardContent = document.querySelector('.board-content');

/**
 * 更新右側看板
 * @param {string} currentStateKey 目前所在的步驟 Key (例如: "S10")
 */
function updateBoard(currentStateKey) {
    const currentLightIndex = states[currentStateKey].light;
    
    // 將當前步驟加入已完成集合
    completedSteps.add(currentStateKey);

    // 篩選出屬於當前設備（燈號）的所有步驟
    let htmlLines = Object.keys(states)
        .filter(key => states[key].light === currentLightIndex)
        .map(key => {
            const isActive = (key === currentStateKey);
            
            // 判斷邏輯：如果是目前步驟，或曾經在 completedSteps 裡，則顯示圈圈
            const isDone = completedSteps.has(key);
            const icon = isDone ? "○" : "✖";
            
            const activeClass = isActive ? "active-text" : "";
            return `<p class="${activeClass}">${icon} ${states[key].text}</p>`;
        }).join("");

    // 重新渲染看板內容
    boardContent.innerHTML = `
        <div class="status-container">
            <h2>STEP</h2>
            <div class="steps-list">
                ${htmlLines}
            </div>
        </div>
    `;
}

/**
 * 主流程運行邏輯
 */
function runProcess() {
    const stateKey = currentPath[currentStep];
    const stateData = states[stateKey];

    // 1. 更新機台燈號 (清除所有亮燈，並點亮當前對應燈號)
    lights.forEach(light => light.classList.remove('active'));
    if (lights[stateData.light]) {
        lights[stateData.light].classList.add('active');
    }

    // 2. 更新右側狀態看板
    updateBoard(stateKey);

    // 3. 進入下一步
    currentStep++;

    // 4. 路徑結束判定：更換工件並隨機決定下一條路徑
    if (currentStep >= currentPath.length) {
        currentStep = 0; 
        
        // 重要：當一個工件製程結束，清空記錄讓下一個工件從叉叉開始
        completedSteps.clear(); 
        
        // 隨機切換金屬或非金屬路徑
        isMetal = Math.random() > 0.5; 
        currentPath = isMetal ? metalPath : nonMetalPath;
    }
}

// 設定循環執行 (每 1.5 秒更換一步)
setInterval(runProcess, 1500);

// 初始化第一次執行
runProcess();