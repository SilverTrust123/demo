/**
 * js/log.js - 兼容純文字與 JSON 格式的日誌處理器
 * 已整合 CONFIG.API_BASE 實現全域 IP 管理
 */

document.addEventListener('DOMContentLoaded', () => {
    const logListElement = document.getElementById('log-list');
    const infoBtn = document.getElementById('btn-info');
    
    // --- 1. 配置 API 端點 (引用 global.js 的 CONFIG) ---
    const endpoints = {
        all: `${CONFIG.API_BASE}/log/all`,
        warn: `${CONFIG.API_BASE}/log/warn`,
        error: `${CONFIG.API_BASE}/log/error`,
        info: `${CONFIG.API_BASE}/fullLog` 
    };

    window.refreshLogUI = function() {
        const token = localStorage.getItem(CONFIG.AUTH_KEY); // 使用 CONFIG 的 KEY
        if (token) {
            infoBtn.classList.remove('locked');
            infoBtn.innerHTML = "Information";
        } else {
            infoBtn.classList.add('locked');
            infoBtn.innerHTML = "Information 🔒";
        }
    };

    async function fetchLogs(type) {
        const startVal = document.getElementById('filter-start').value;
        const endVal = document.getElementById('filter-end').value;
        const startTimeTs = startVal ? new Date(startVal).getTime() : 0;
        const endTimeTs = endVal ? new Date(endVal).getTime() : Date.now();

        const params = new URLSearchParams({ start: startTimeTs, end: endTimeTs });

        document.querySelectorAll('.log-btn').forEach(btn => btn.classList.remove('active'));
        const activeBtn = document.getElementById(`btn-${type}`);
        if(activeBtn) activeBtn.classList.add('active');

        logListElement.innerHTML = `<li>正在檢索數據 [${type.toUpperCase()}]...</li>`;

        try {
            const endpointUrl = `${endpoints[type]}?${params.toString()}`;
            
            const response = await window.fetchWithAuth(endpointUrl, { method: 'GET' });

            if (response.status === 403) throw new Error("存取權限不足");
            if (!response.ok) throw new Error(`HTTP 錯誤: ${response.status}`);

            // 1. 先獲取原始文字
            const rawData = await response.text();
            console.log(`📥 收到 ${type} 原始數據內容`);

            // 2. 判斷是 JSON 還是純文字 Log
            let logs = [];
            if (rawData.trim().startsWith('[') || rawData.trim().startsWith('{')) {
                try {
                    const jsonData = JSON.parse(rawData);
                    logs = Array.isArray(jsonData) ? jsonData : (jsonData.response || [jsonData]);
                } catch (e) {
                    logs = parseRawTextToLogs(rawData);
                }
            } else {
                logs = parseRawTextToLogs(rawData);
            }

            renderLogs(logs);
        } catch (error) {
            console.error("Fetch Error:", error);
            logListElement.innerHTML = `<li style="color:#e74c3c">載入失敗: ${error.message}</li>`;
        }
    }

    /**
     * 將純文字 Log 轉換為物件格式
     */
    function parseRawTextToLogs(text) {
        const lines = text.split(/\n/); 
        return lines.filter(line => line.trim() !== "").map(line => {
            let level = 'INFO';
            if (line.includes('WARN')) level = 'WARN';
            if (line.includes('ERROR')) level = 'ERROR';

            return {
                timestamp: null, 
                log_level: level,
                source: 'RAW',
                message: line,
                isRawText: true 
            };
        });
    }

    function renderLogs(logs) {
        logListElement.innerHTML = "";
        if (!logs || logs.length === 0) {
            logListElement.innerHTML = "<li>目前無日誌數據。</li>";
            return;
        }

        logs.forEach(log => {
            const li = document.createElement('li');
            li.className = "log-item";
            
            if (log.log_level === 'ERROR') li.classList.add('log-warning-row');

            if (log.isRawText) {
                li.innerHTML = `<span class="log-msg-raw" style="white-space: pre-wrap; font-family: monospace; font-size: 0.9rem;">${log.message}</span>`;
            } else {
                const timeStr = log.timestamp ? new Date(log.timestamp).toLocaleString() : 'N/A';
                const levelClass = `level-${(log.log_level || 'info').toLowerCase()}`;
                li.innerHTML = `
                    <span style="color: #888; font-size: 0.85rem; min-width: 160px;">[${timeStr}]</span>
                    <b class="${levelClass}" style="min-width: 70px; display: inline-block;">[${log.log_level || 'INFO'}]</b>
                    <span style="color: #2980b9; font-weight: bold; min-width: 100px;">[${log.source || 'SYS'}]</span>
                    <span class="log-msg">${log.message || '無內容'}</span>
                `;
            }
            logListElement.appendChild(li);
        });
    }

    // 事件綁定
    ['all', 'warn', 'error', 'info'].forEach(type => {
        const btn = document.getElementById(`btn-${type}`);
        if(btn) btn.addEventListener('click', () => fetchLogs(type));
    });

    const searchBtn = document.getElementById('btn-search-time');
    if(searchBtn) {
        searchBtn.addEventListener('click', () => {
            const active = document.querySelector('.log-btn.active');
            fetchLogs(active ? active.id.replace('btn-', '') : 'all');
        });
    }

    window.refreshLogUI(); 
    fetchLogs('all');      
});