const API_URL = 'http://localhost:8080/api';

function toggleTheme() {
    document.body.classList.toggle('light-theme');
    localStorage.setItem('theme', document.body.classList.contains('light-theme') ? 'light' : 'dark');
}

if (localStorage.getItem('theme') === 'light') document.body.classList.add('light-theme');

async function loadData() {
    const statusRes = await fetch(`${API_URL}/status`);
    const statusData = await statusRes.json();

    document.getElementById('timer').innerHTML =
        `<span>${statusData.days}</span> <small>days</small> <span>${statusData.hours}</span> <small>hours</small>`;
    document.getElementById('lastReason').innerText = `Last incident: ${statusData.lastReason}`;

    const historyRes = await fetch(`${API_URL}/history`);
    const historyList = await historyRes.json();

    renderHistory(historyList);
    renderGraph(historyList);
}

function renderHistory(list) {
    const container = document.getElementById('historyList');
    container.innerHTML = '';
    if (list.length === 0) {
        container.innerHTML = '<div class="log-item" style="color: #8b949e; justify-content: center;">You are pure!</div>';
        return;
    }
    list.slice().reverse().forEach(item => {
        const div = document.createElement('div');
        div.className = 'log-item';
        const dateStr = new Date(item.relapseDate).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
        div.innerHTML = `<div class="log-reason">${item.reason}</div><div class="log-date">${dateStr}</div>`;
        container.appendChild(div);
    });
}

function renderGraph(historyList) {
    const graphContainer = document.getElementById('graph');
    graphContainer.innerHTML = '';

    const sinDates = new Set();
    historyList.forEach(item => {
        sinDates.add(new Date(item.relapseDate).toISOString().split('T')[0]);
    });

    const totalDays = 13 * 5;

    for (let i = totalDays - 1; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        const dateKey = d.toISOString().split('T')[0];

        const box = document.createElement('div');
        box.className = 'day-box';
        box.title = dateKey;

        if (sinDates.has(dateKey)) {
            box.classList.add('day-relapse');
        } else {
            box.classList.add('day-clean');
        }

        graphContainer.appendChild(box);
    }
}

async function commitSin() {
    const reason = document.getElementById('reasonInput').value.trim();
    if(!reason) return alert("Please enter a reason!");

    if(confirm("Are you sure? This will reset your counter.")) {
        await fetch(`${API_URL}/reset`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ reason: reason })
        });
        document.getElementById('reasonInput').value = '';
        loadData();
    }
}

async function clearAllHistory() {
    if(confirm("DANGER! This will delete ALL history logs. Are you sure?")) {
        await fetch(`${API_URL}/clear`, { method: 'DELETE' });
        loadData();
    }
}

loadData();
setInterval(loadData, 60000);