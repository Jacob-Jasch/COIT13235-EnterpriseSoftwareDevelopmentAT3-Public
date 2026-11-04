async function launchConsole() {
            const btn = document.getElementById('launchBtn');
            const buttonText = document.getElementById('buttonText');
            const spinner = document.getElementById('loadingSpinner');
            const statusDiv = document.getElementById('statusMessage');
            
            // Show loading state
            btn.disabled = true;
            spinner.style.display = 'inline-block';
            buttonText.textContent = 'Starting Console...';
            statusDiv.style.display = 'none';
            
            try {
                const response = await fetch('/console/start', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                const result = await response.text();
                const data = JSON.parse(result);
                
                statusDiv.className = 'status-message status-' + data.status;
                statusDiv.textContent = data.message;
                statusDiv.style.display = 'block';
                
                if (data.status === 'success') {
                    buttonText.textContent = 'Console Started Successfully';
                    setTimeout(() => {
                        btn.disabled = false;
                        buttonText.textContent = 'Start Another Console Session';
                        spinner.style.display = 'none';
                    }, 3000);
                } else {
                    btn.disabled = false;
                    buttonText.textContent = 'Start Console Interface';
                    spinner.style.display = 'none';
                }
                
            } catch (error) {
                statusDiv.className = 'status-message status-error';
                statusDiv.textContent = 'Connection Error: Unable to reach the server. Please ensure the application is running.';
                statusDiv.style.display = 'block';
                
                btn.disabled = false;
                buttonText.textContent = 'Retry Console Launch';
                spinner.style.display = 'none';
            }
        }