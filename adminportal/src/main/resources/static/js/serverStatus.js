function loadServerStatus() {
    fetch('/admin/server-status')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('server-status-body');
            tableBody.innerHTML = ''; 

            data.forEach(server => {
                const statusClass = getStatusClass(server.statusName); 
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${server.domain}</td>
                    <td>${server.ipAddress}</td>
                    <td>${server.port}</td>
                    <td class="${statusClass}">${server.statusName}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching server status:', error));
}


