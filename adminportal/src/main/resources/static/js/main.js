document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;

    if (currentPath.endsWith('index.html')) {

    } else if (currentPath.endsWith('server_status.html')) {
        loadServerStatus();
    } else if (currentPath.endsWith('add_remove.html')) {
        loadServers();
        initAddRemoveListeners();
    } else if (currentPath.endsWith('add_server.html')) {
        initFormHandler();
       
    }
});

function getStatusClass(status) {
    switch (status.toLowerCase()) {
        case 'running':
            return 'status-running';
        case 'off':
            return 'status-off';
        case 'critical':
            return 'status-critical';
        default:
            return '';  
    }
}
