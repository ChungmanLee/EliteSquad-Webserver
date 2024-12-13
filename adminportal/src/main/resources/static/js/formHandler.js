function initFormHandler() {
    document.getElementById('add-server-form').addEventListener('submit', (e) => {
        e.preventDefault();

        const domainInput = document.getElementById('domain');
        const domainValue = domainInput.value;

        
        fetch(`/admin/check-domain?domain=${encodeURIComponent(domainValue)}`)
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    alert('This domain name is already taken. Please choose another one.');
                    domainInput.focus();
                } else {
                    
                    const formData = new FormData(e.target);

                    fetch('/admin/add-server', {
                        method: 'POST',
                        body: formData
                    })
                    .then(response => response.json())
                    .then(data => {
                        alert(`${data.message}. Domain: ${data.domain}`);
                        
                        
                        const popupElement = window.frameElement.closest('.popup'); 
                        if (popupElement) {
                            popupElement.style.display = 'none';
                        } else {
                            console.error('Popup element not found.');
                        }
                        
                        
                        if (window.parent && window.parent.loadServers) {
                            window.parent.loadServers(); 
                        } else {
                            console.error('Parent window or loadServers function not found.');
                        }

                        
                        e.target.reset();
                    })
                    .catch(error => console.error('Error:', error));
                }
            })
            .catch(error => console.error('Error checking domain:', error));
    });
}
