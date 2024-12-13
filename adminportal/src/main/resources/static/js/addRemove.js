function loadServers() {
    fetch('/admin/server-status')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('server-list');
            tableBody.innerHTML = ''; 

            data.forEach(server => {
                const protocol = server.port === 8443 ? 'https' : 'http';
                const url = `${protocol}://${server.domain}:${server.port}`; 
                const statusClass = getStatusClass(server.statusName);

                const actionButton = server.statusName.toLowerCase() === 'running' 
                    ? `<button onclick="stopServer('${server.id}')">Stop</button>` 
                    : `<button onclick="startServer('${server.id}')">Start</button>`;

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${server.domain}</td>
                    <td>${server.ipAddress}</td>
                    <td>${server.port}</td>
                    <td class="${statusClass}">${server.statusName}</td>
                    <td><a href="${url}" target="_blank">Test Site</a></td>
                    <td>
                        ${actionButton}
                        <button onclick="removeServer('${server.id}')">Remove</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching server status:', error));
}

function removeServer(siteId) {
    fetch('/admin/remove-server', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ siteId })
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message);
        loadServers(); 
    })
    .catch(error => console.error('Error:', error));
}

function stopServer(siteId) {
    fetch('/admin/stop-server', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ siteId })
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message);
        loadServers(); 
    })
    .catch(error => console.error('Error:', error));
}

function startServer(siteId) {
    fetch('/admin/start-server', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ siteId })
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message);
        loadServers(); 
    })
    .catch(error => console.error('Error:', error));
}

function initAddRemoveListeners() {
    const popup = document.getElementById('add-server-popup');
    const closeButton = document.querySelector('.close-button');

    document.getElementById('add-server-button').addEventListener('click', () => {
        popup.style.display = 'flex';
    });

    closeButton.addEventListener('click', () => {
        popup.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target === popup) {
            popup.style.display = 'none';
        }
    });
}
