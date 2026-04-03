import { useState, useEffect } from 'react'; // 第四步加入的：引入 React 的「大腦」工具
import './App.css'; // 第三步加入的：引入你原本的 mainUI.css

function App() {
  // --- 邏輯區 (原本 mainUI.js 的內容搬過來這裡) ---
  
  // 設定狀態 (State)：這就是 React 的「記憶體」，數據變了，畫面就會自動變
  const [sensors, setSensors] = useState({
    temp: 23.8,
    humi: 46.9,
    co2: 530,
    pm: 0
  });

  const [currentStep, setCurrentStep] = useState(0);

  // 設定定時器 (Effect)：模擬原本的 setInterval 更新數據
  useEffect(() => {
    const timer = setInterval(() => {
      // 更新感測器數值
      setSensors({
        temp: 22 + Math.random() * 4,
        humi: 40 + Math.random() * 20,
        co2: 450 + Math.random() * 300,
        pm: Math.random() * 15
      });
      
      // 更新機器輪巡狀態
      setCurrentStep((prev) => (prev + 1) % 5);
    }, 5000);

    return () => clearInterval(timer); // 良好的習慣：離開網頁時把計時器關掉
  }, []);

  // --- 畫面區 (原本 index.html 搬過來這裡) ---
  return (
    <div className="dashboard-wrapper">
      <header className="top-section">
        <h1 className="brand-logo">機台監控</h1>
        <nav className="nav-menu">
          <a href="#" className="nav-btn active-tab">HomePage</a>
          {/* 其他導覽按鈕... */}
        </nav>
        
        <div className="status-summary-bar">
          <div className="summary-item">
            <p className="summary-label">運轉狀態</p>
            <p className="summary-val">
              {/* 這裡不再用 id="run-status"，而是直接用判斷式 */}
              <span className={currentStep !== null ? "v-green" : "v-red"}>
                {currentStep !== null ? "yes" : "no"}
              </span>
            </p>
          </div>
        </div>
      </header>

      <main className="middle-section">
        <div className="gauges-flex-container">
          {/* 溫度儀表板 */}
          <div className="gauge-card">
            <h2 className="gauge-title g-temp-text">temperature</h2>
            <div className="gauge-svg-wrapper">
              <svg viewBox="0 0 100 60">
                <path className="gauge-base-ring" d="M10,50 A40,40 0 1,1 90,50" />
                {/* 關鍵：原本 JS 在改的 path，現在由 React 直接透過數值控制 */}
                <path className="gauge-active-ring g-temp-stroke" d="M10,50 A40,40 0 1,1 90,50" />
              </svg>
              {/* 這裡直接顯示 state 裡的數字，toFixed(1) 是取小數點第一位 */}
              <div className="gauge-value g-temp-text">{sensors.temp.toFixed(1)}°C</div>
            </div>
          </div>
          
          {/* 濕度、CO2 儀表板可以依此類推... */}
        </div>
      </main>

      <footer className="bottom-section">
        {/* 原本的設備清單... */}
        <p>目前執行步驟：{currentStep + 1}</p>
      </footer>
    </div>
  );
}

export default App;