/**
 * global.js - 全域功能配置 (完整功能還原版 + 深色模式新增)
 */

document.addEventListener('DOMContentLoaded', () => {
    initTheme();           // 1. 初始化主題 (新增)
    initErrorModal();      
    initLoginModal();      
    initTimeoutModal();    
    initSettingsSidebar(); 
    checkLoginStatus();    
    
    // 啟動全域背景監控 (整合 Webcam 與 感測器)
    startGlobalMonitor();

    // 每分鐘自動檢查一次登入是否過期
    setInterval(checkLoginStatus, 60000); 
});

// ... 前面的 CONFIG 定義 ...
const CONFIG = {
    API_BASE: "http://192.168.3.110:9090",
    AUTH_KEY: "admin_token", // 確保這裡與你儲存 Token 的 Key 一致
    TIME_KEY: "login_timestamp",
    EXPIRE_TIME: 24 * 60 * 60 * 1000,
    ALERT_COOLDOWN: 10000 
};

// 在 global.js 的 CONFIG 之後加入這段
const originalFetch = window.fetch;
window.fetch = async (...args) => {
    let [resource, config] = args;
    
    // 如果是呼叫後端 API，且目前有 Token，就自動補上
    const token = localStorage.getItem(CONFIG.AUTH_KEY);
    if (token && resource.includes(CONFIG.API_BASE)) {
        config = config || {};
        config.headers = {
            ...config.headers,
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }
    return originalFetch(resource, config);
};
/**
 * ✨ 新增：全域認證 Fetch 函式 ✨
 * 自動從 localStorage 抓取 Token 並放入 Header
 */
window.fetchWithAuth = async function(url, options = {}) {
    // 從 localStorage 取得 token
    const token = localStorage.getItem(CONFIG.AUTH_KEY);
    
    // 準備 Headers，如果 token 存在就加上 Authorization
    const authHeaders = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        // 依照你的後端需求調整，通常是 Bearer ${token}
        authHeaders['Authorization'] = `Bearer ${token}`;
    }

    // 合併新的 options
    const newOptions = {
        ...options,
        headers: authHeaders
    };

    return fetch(url, newOptions);
};

// ... 後面的主題、監控、Modal 初始化邏輯 ...

// --- 全域狀態變數 ---
let isAlertActive = false;    // 目前是否正在顯示警報視窗
let lastAlertTime = 0;        // 上一次警報關閉的時間點

/**
 * 深色模式邏輯 (新增功能)
 */
function initTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
}

function toggleDarkMode() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const targetTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', targetTheme);
    localStorage.setItem('theme', targetTheme);
    console.log(`主題已切換至: ${targetTheme}`);
}

/**
 * 全域監控主程式：整合所有報警邏輯
 */
async function startGlobalMonitor() {
    const CAM_ENDPOINT = `${CONFIG.API_BASE}/camData/`;
    const SENSOR_ENDPOINT = `${CONFIG.API_BASE}/allData/allSenosrData`;
    
    const performChecks = async () => {
        if (isAlertActive) return;
        const now = Date.now();
        if (now - lastAlertTime < CONFIG.ALERT_COOLDOWN) return;

        try {
            const [camRes, sensorRes] = await Promise.all([
                fetch(CAM_ENDPOINT).then(r => r.ok ? r.json() : null).catch(() => null),
                fetch(SENSOR_ENDPOINT).then(r => r.ok ? r.json() : null).catch(() => null)
            ]);

            const camInfo = Array.isArray(camRes) ? camRes[0] : camRes;
            if (camInfo && camInfo.personCount > 0) {
                window.showError(`警告：生產線偵測到人員闖入！(目前：${camInfo.personCount} 人)`);
                return;
            }

            const temp = sensorRes.temperature ?? 0;
            if (temp > 40.0) {
                window.showError(`設備過熱警告！目前溫度：${temp.toFixed(1)}°C`);
            } else if (temp > 0 && temp < 10.0) {
                window.showError(`環境低溫異常！目前溫度：${temp.toFixed(1)}°C`);
            }

        } catch (error) {
            console.warn("全域監控發生錯誤:", error);
        }
    };

    setInterval(performChecks, 3000);
}

/**
 * 顯示警報視窗
 */
window.showError = (msg) => {
    const overlay = document.getElementById('error-overlay');
    const message = document.getElementById('error-message');
    
    if (overlay && message) { 
        isAlertActive = true; 
        message.innerText = msg; 
        overlay.style.display = 'flex'; 
    }
};

/**
 * 關閉警報視窗
 */
window.hideError = () => {
    const overlay = document.getElementById('error-overlay');
    if (overlay) {
        overlay.style.display = 'none';
        isAlertActive = false;      
        lastAlertTime = Date.now(); 
        console.log("警報已解除，系統進入 10 秒防干擾冷卻期...");
    }
};

/**
 * 1. 初始化登入視窗
 */
function initLoginModal() {
    if (document.getElementById('login-modal-overlay')) return;

    const modalHTML = `
    <div id="login-modal-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; z-index:9999; background:rgba(0,0,0,0.5); backdrop-filter: blur(5px);">
        <div class="login-card" style="background: rgba(255, 255, 255, 0.85); width: 350px; padding: 40px 30px; border-radius: 30px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); box-shadow: 0 15px 35px rgba(0,0,0,0.2); text-align: center; border: 1px solid rgba(255,255,255,0.3);">
            <div style="margin-bottom: 10px;"><img src="picture/login_icon.png" style="width: 200px; opacity: 0.8;" alt="LoginIcon"></div>
            <h2 style="margin: 0; color: #333; font-size: 24px; letter-spacing: 2px;">帳號密碼登入</h2>
            <p style="margin: 5px 0 25px; color: #333; font-size: 28px; font-weight: bold;">LOG IN</p>
            <div class="input-container" style="position: relative; margin-bottom: 15px;">
                <span style="position: absolute; left: 15px; top: 50%; transform: translateY(-50%); color: #1a5a7a; font-size: 20px;">👤</span>
                <input type="text" id="input-username" placeholder="帳號/Account" style="width: 100%; padding: 12px 12px 12px 45px; border-radius: 25px; border: 2px solid #5a9fb3; background: #e6f1f4; box-sizing: border-box; font-size: 16px;">
            </div>
            <div class="input-container" style="position: relative; margin-bottom: 10px;">
                <span style="position: absolute; left: 15px; top: 50%; transform: translateY(-50%); color: #1a5a7a; font-size: 20px;">🔒</span>
                <input type="password" id="input-password" placeholder="密碼/Password" style="width: 100%; padding: 12px 12px 12px 45px; border-radius: 25px; border: 2px solid #5a9fb3; background: #e6f1f4; box-sizing: border-box; font-size: 16px;">
            </div>
            <div id="login-modal-msg" style="font-size:12px; color:#e74c3c; margin-bottom:10px; min-height:15px;"></div>
            <button id="btn-login-submit" style="width: 100%; padding: 12px; background: #418d9e; color: white; border: none; border-radius: 25px; font-size: 20px; font-weight: bold; cursor: pointer; box-shadow: 0 4px 10px rgba(65, 141, 158, 0.3); transition: background 0.3s;">登入</button>
            <button id="btn-login-cancel" style="margin-top: 15px; background: none; border: none; color: #888; cursor: pointer; font-size: 14px; text-decoration: underline;">取消登入</button>
        </div>
    </div>`;
    
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    document.getElementById('btn-login-cancel').onclick = () => {
        document.getElementById('login-modal-overlay').style.display = 'none';
    };
    document.getElementById('btn-login-submit').onclick = handleLoginSubmit;
}

async function handleLoginSubmit() {
    const user = document.getElementById('input-username').value;
    const pass = document.getElementById('input-password').value;
    const msg = document.getElementById('login-modal-msg');

    if (!user || !pass) {
        msg.innerText = "請輸入完整帳密";
        return;
    }

    const loginPayload = { "request": { username: user, password: pass } };

    try {
        msg.style.color = "#1a5a7a";
        msg.innerText = "驗證中...";
        
        const response = await fetch(`${CONFIG.API_BASE}/login/`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginPayload)
        });

        const result = await response.json();

        if (response.ok) {
            const token = result.token || (result.data && result.data.token) || result.accessToken;
            if (token) {
                localStorage.setItem(CONFIG.AUTH_KEY, token);
                localStorage.setItem(CONFIG.TIME_KEY, new Date().getTime());
                document.getElementById('login-modal-overlay').style.display = 'none';
                alert("登入成功！");
                checkLoginStatus(); 
                if (typeof window.refreshLogUI === 'function') window.refreshLogUI();
            } else {
                throw new Error("未取得授權碼");
            }
        } else {
            throw new Error(result.message || "驗證失敗");
        }
    } catch (error) {
        msg.style.color = "#e74c3c";
        msg.innerText = error.message;
    }
}

function checkLoginStatus() {
    const token = localStorage.getItem(CONFIG.AUTH_KEY);
    const loginTime = localStorage.getItem(CONFIG.TIME_KEY);
    const loginBtn = document.getElementById('sidebar-login-btn');
    const logoutBtn = document.getElementById('sidebar-logout-btn');
    const statusText = document.getElementById('login-status-text');

    if (token && loginTime) {
        const now = new Date().getTime();
        if (now - loginTime > CONFIG.EXPIRE_TIME) {
            localStorage.removeItem(CONFIG.AUTH_KEY);
            localStorage.removeItem(CONFIG.TIME_KEY);
            showTimeoutModal(); 
            return;
        }
    }

    if (token && loginBtn && logoutBtn) {
        loginBtn.style.display = 'none';
        logoutBtn.style.display = 'block';
        if (statusText) statusText.innerText = "狀態：已授權管理員";
    } else if (loginBtn && logoutBtn) {
        loginBtn.style.display = 'block';
        logoutBtn.style.display = 'none';
        if (statusText) statusText.innerText = "狀態：未登入";
    }
}

function initTimeoutModal() {
    if (document.getElementById('timeout-modal-overlay')) return;
    const timeoutHTML = `
    <div id="timeout-modal-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; z-index:10000; background:rgba(0,0,0,0.6); backdrop-filter: blur(3px); justify-content:center; align-items:center;">
        <div style="background:white; width:320px; border-radius:20px; padding:30px 20px; text-align:center; box-shadow:0 10px 25px rgba(0,0,0,0.3);">
            <div style="margin-bottom:15px;"><img src="picture/alert_icon.png" style="width:80px;" alt="Warning"></div>
            <h2 style="margin:0; color:#333; font-size:22px;">注意</h2>
            <p style="margin:10px 0 20px; color:#e74c3c; font-size:18px; font-weight:bold; line-height:1.5;">登入時間超過請<br>重新登入</p>
            <button id="btn-timeout-confirm" style="background:#bbb; color:#333; border:none; padding:8px 30px; border-radius:20px; font-size:16px; font-weight:bold; cursor:pointer;">確定</button>
        </div>
    </div>`;
    document.body.insertAdjacentHTML('beforeend', timeoutHTML);
    document.getElementById('btn-timeout-confirm').onclick = () => {
        document.getElementById('timeout-modal-overlay').style.display = 'none';
        document.getElementById('login-modal-overlay').style.display = 'block';
        checkLoginStatus();
    };
}

function showTimeoutModal() {
    const overlay = document.getElementById('timeout-modal-overlay');
    if (overlay) overlay.style.display = 'flex';
}

function initSettingsSidebar() {
    const navMenu = document.querySelector('.nav-menu');
    if (!navMenu) return;
    
    if (!document.getElementById('global-settings-btn')) {
        navMenu.insertAdjacentHTML('beforeend', `
            <div class="nav-divider" style="height: 1px; background: rgba(0,0,0,0.1); margin: 10px 0;"></div>
            <a href="javascript:void(0)" class="nav-settings-btn" id="global-settings-btn" title="系統設定" style="display: flex; align-items: center; justify-content: center; padding: 10px; transition: transform 0.3s;">
                <img src="picture/setting.png" alt="Settings" style="width:24px; height:24px;">
            </a>
        `);
    }

 if (!document.getElementById('settings-sidebar')) {
    const sidebarHTML = `
        <div id="sidebar-overlay" class="sidebar-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.3); z-index:9000;"></div>
        
        <div id="settings-sidebar" class="settings-sidebar" style="position:fixed; top:0; right:-300px; width:300px; height:100%; background: var(--sidebar-bg, white); z-index:9001; transition: 0.3s; box-shadow: -5px 0 15px rgba(0,0,0,0.1); color: var(--text-color, #333);">
            
            <div class="sidebar-header" style="padding: 20px; background: var(--sidebar-header-bg, #7f7f7f); border-bottom: 1px solid rgba(0,0,0,0.1); display: flex; align-items: center;">
                <img src="picture/setting.png" alt="icon" style="width:20px; margin-right: 10px;">
                <h2 style="margin:0; font-size: 18px; color: var(--text-color, #333);">工具列</h2>
            </div>
            
            <div id="sidebar-dark-mode-btn" class="sidebar-menu-item" style="padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.05); cursor: pointer;"> 深色模式</div>
            <div class="sidebar-menu-item" style="padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.05); cursor: pointer;"> 語言切換</div>
            
            <div id="sidebar-about-btn" class="sidebar-menu-item" style=" padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.05); cursor:pointer; text-align:left;;">關於</div>
            
            <div id="sidebar-login-btn" class="sidebar-menu-item" style="padding: 15px 20px; color:#2ecc71; font-weight:bold; cursor: pointer;">系統登入</div>
            <div id="sidebar-logout-btn" class="sidebar-menu-item" style="padding: 15px 20px; color:#e74c3c; font-weight:bold; display:none; cursor: pointer;">登出系統</div>
            <div id="login-status-text" style="padding:0 20px; font-size:12px; color:#888; margin-top:5px;"></div>
        </div>
        
        <div id="about-content-page" style="display:none; position:fixed; top:40px; left:10px; right:10px; bottom:10px; background: var(--about-page-bg, #f0f2f5); z-index:8000; padding:20px; pointer-events:auto; border-radius: 15px; overflow: hidden;">
            <div style="background: var(--sidebar-bg, white); height:100%; border-radius:10px; padding:30px; box-shadow:0 2px 10px rgba(0,0,0,0.1); position:relative;">
                <button id="close-about-page" style="position:absolute; top:20px; right:20px; border:none; background:none; font-size:32px; cursor:pointer; color: #888;">&times;</button>
                <h1 style="font-size:48px; margin:0; color: var(--text-color, #333);">關於</h1>
                <hr style="margin: 20px 0; border: 0; border-top: 1px solid rgba(0,0,0,0.1);">
                <p style="font-size: 18px; color: var(--text-sub-color, #666); line-height: 1.6;">088</p>
            </div>
        </div>`;
    document.body.insertAdjacentHTML('beforeend', sidebarHTML);
}

    const btn = document.getElementById('global-settings-btn');
    const sidebar = document.getElementById('settings-sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    const aboutPage = document.getElementById('about-content-page');

    const openSidebar = () => {
        sidebar.style.right = '0';
        overlay.style.display = 'block';
    };

    const closeSidebar = () => {
        sidebar.style.right = '-300px';
        overlay.style.display = 'none';
    };

    btn.onclick = openSidebar;
    overlay.onclick = closeSidebar;
    
    // 綁定深色模式切換 (新增)
    document.getElementById('sidebar-dark-mode-btn').onclick = toggleDarkMode;

    document.getElementById('sidebar-about-btn').onclick = () => {
        closeSidebar();
        aboutPage.style.display = 'block';
    };

    document.getElementById('close-about-page').onclick = () => {
        aboutPage.style.display = 'none';
    };

    document.getElementById('sidebar-login-btn').onclick = () => {
        closeSidebar();
        document.getElementById('login-modal-overlay').style.display = 'block';
    };
    
    document.getElementById('sidebar-logout-btn').onclick = () => {
        localStorage.removeItem(CONFIG.AUTH_KEY);
        localStorage.removeItem(CONFIG.TIME_KEY);
        location.reload(); 
    };
}

function initErrorModal() {
    if (document.getElementById('error-overlay')) return;
    document.body.insertAdjacentHTML('beforeend',`<div id="error-overlay" style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.8);z-index:11000;justify-content:center;align-items:center;backdrop-filter:blur(4px);"><div style="background:#b09090;width:800px;max-width:95%;border-radius:15px;overflow:hidden;box-shadow:0 0 100px rgba(0,0,0,0.8);text-align:center;border:4px solid #333;"><div style="background:#b09090;padding:25px 10px 10px;display:flex;justify-content:center;"><div style="width:0;height:0;border-left:50px solid transparent;border-right:50px solid transparent;border-bottom:85px solid #c00000;position:relative;"><span style="position:absolute;top:15px;left:-8px;color:white;font-size:55px;font-weight:bold;font-family:Arial;">!</span></div></div><div style="height:35px;background:repeating-linear-gradient(45deg,#f1c40f,#f1c40f 20px,#222 20px,#222 40px);border-top:3px solid #333;border-bottom:3px solid #333;"></div><div style="padding:60px 30px;min-height:150px;display:flex;align-items:center;justify-content:center;"><p id="error-message" style="font-size:38px;font-weight:bold;color:white;margin:0;text-shadow:2px 2px 8px rgba(0,0,0,0.6);letter-spacing:2px;">設備異常！</p></div><div style="height:35px;background:repeating-linear-gradient(45deg,#f1c40f,#f1c40f 20px,#222 20px,#222 40px);border-top:3px solid #333;border-bottom:3px solid #333;"></div><button class="close-error" onclick="hideError()" style="width:100%;padding:30px;border:none;background:#E9CFCF;font-size:32px;font-weight:bold;cursor:pointer;color:#c00000;letter-spacing:4px;border-top:2px solid #333;">關閉</button></div></div>`);
}