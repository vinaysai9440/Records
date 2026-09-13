const playerNameInput = document.getElementById("playerName");
const playerNamesList = document.getElementById("playerNames");
const formatToggle = document.getElementById("formatToggle");
const searchBtn = document.getElementById("searchBtn");
const resultArea = document.getElementById("resultArea");

let selectedFormat = "TEST";

formatToggle.addEventListener("click", (e) => {
    const btn = e.target.closest(".format-btn");
    if (!btn) return;
    formatToggle.querySelectorAll(".format-btn").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    selectedFormat = btn.dataset.format;
});

playerNameInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter") search();
});

searchBtn.addEventListener("click", search);

async function loadPlayerNames() {
    try {
        const res = await fetch("/api/players/names");
        if (!res.ok) return;
        const names = await res.json();
        playerNamesList.innerHTML = names.map((n) => `<option value="${escapeHtml(n)}"></option>`).join("");
    } catch (err) {
        // Autocomplete is a nice-to-have; ignore failures silently.
    }
}

async function search() {
    const playerName = playerNameInput.value.trim();
    if (!playerName) {
        renderMessage("Enter a player name to search.", true);
        return;
    }

    searchBtn.disabled = true;
    searchBtn.textContent = "Searching...";
    renderMessage("Loading...", false);

    try {
        const params = new URLSearchParams({ playerName, format: selectedFormat });
        const res = await fetch(`/api/players/stats?${params.toString()}`);
        const data = await res.json();

        if (!res.ok) {
            renderMessage(data.message || "Player not found", true);
            return;
        }

        renderStats(data);
    } catch (err) {
        renderMessage("Could not reach the server. Please try again.", true);
    } finally {
        searchBtn.disabled = false;
        searchBtn.textContent = "Search";
    }
}

function renderMessage(text, isError) {
    resultArea.hidden = false;
    resultArea.innerHTML = `<div class="message${isError ? " error" : ""}">${escapeHtml(text)}</div>`;
}

function renderStats(data) {
    resultArea.hidden = false;
    resultArea.innerHTML = `
        <div class="stats-card">
            <div class="player-heading">
                <h2>${escapeHtml(data.playerName)}</h2>
                <span>${escapeHtml(data.country || "")} &middot; ${escapeHtml(data.format)}</span>
            </div>
            <div class="stats-grid">
                ${statTile(data.matches, "Matches")}
                ${statTile(data.innings, "Innings")}
                ${statTile(data.runs, "Runs")}
                ${statTile(data.average, "Average")}
                ${statTile(data.strikeRate, "Strike Rate")}
                ${statTile(data.hundreds, "100s")}
                ${statTile(data.fifties, "50s")}
                ${statTile(data.fours, "4s")}
                ${statTile(data.sixes, "6s")}
            </div>
        </div>
    `;
}

function statTile(value, label) {
    return `<div class="stat-tile"><div class="value">${value}</div><div class="label">${label}</div></div>`;
}

function escapeHtml(str) {
    const div = document.createElement("div");
    div.textContent = str;
    return div.innerHTML;
}

loadPlayerNames();
