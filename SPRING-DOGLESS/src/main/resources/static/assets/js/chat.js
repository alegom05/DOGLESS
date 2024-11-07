document.addEventListener('DOMContentLoaded', function() {
    console.log('El script chat.js se ha cargado correctamente');
    const chatButton = document.getElementById('chatButton');
    const chatModal = document.getElementById('chatModal');
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendMessage');
    const chatMessages = document.getElementById('chatMessages');

    // Toggle chat modal
    chatButton.addEventListener('click', function() {
        chatModal.classList.toggle('active');
    });

    // Send message
    function sendMessage() {
        const message = messageInput.value.trim();
        if (message) {
            // Add user message to chat
            addMessage(message, 'user');

            // Send to backend
            fetch('/api/chat/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ content: message })
            })
            .then(response => response.json())
            .then(data => {
                // Add bot response to chat
                addMessage(data.content, 'bot');
            })
            .catch(error => {
                console.error('Error:', error);
                addMessage('Lo siento, hubo un error al procesar tu mensaje.', 'bot');
            });

            messageInput.value = '';
        }
    }

    // Add message to chat
    function addMessage(message, type) {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', `${type}-message`);
        messageElement.textContent = message;
        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // Event listeners
    sendButton.addEventListener('click', sendMessage);
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });
});