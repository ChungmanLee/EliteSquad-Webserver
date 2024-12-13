<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat Application</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f0f2f5;
            color: #333;
        }
        h1 {
            text-align: center;
            font-weight: 700;
            color: #333;
        }
        .container {
            margin-top: 50px;
            max-width: 600px;
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .message-box {
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 10px;
            margin-bottom: 10px;
            position: relative;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .message-box strong {
            display: block;
            margin-bottom: 5px;
            color: #007bff;
        }
        .message-box .btn-group {
            position: absolute;
            top: 10px;
            right: 10px;
        }
        .message-box .btn {
            padding: 2px 6px;
            font-size: 10px;
            margin-left: 5px;
            width: 50px;
        }
        .btn-primary, .btn-secondary {
            padding: 5px 10px;
            font-size: 12px;
            width: 100%;
            margin-bottom: 5px;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            border-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
            border-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
            border-color: #545b62;
        }
        .form-buttons {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 10px;
        }
        #chat-box {
            height: 500px;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Chat Application</h1>
    <div id="chat-box" class="border p-3 mb-3" style="overflow-y: scroll;">
        <!-- messages -->
    </div>
    <form id="message-form">
        <div class="form-group">
            <input type="text" id="username" class="form-control" placeholder="Your Name" required>
        </div>
        <div class="form-group">
            <textarea id="message" class="form-control" rows="3" placeholder="Type your message here..." required></textarea>
        </div>
        <div class="form-buttons">
            <button type="submit" class="btn btn-primary">Send</button>
            <button id="refresh-btn" class="btn btn-secondary">Refresh</button>
        </div>
    </form>
</div>

<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editModalLabel">Edit Message</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="editMessageId">
                <div class="form-group">
                    <input type="text" id="editUsername" class="form-control" placeholder="Your Name" required>
                </div>
                <div class="form-group">
                    <textarea id="editMessage" class="form-control" rows="3" placeholder="Edit your message..." required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="saveEditButton">Save changes</button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<script>
    const apiUrl = window.location.origin; // API URL 동적으로 설정

    // fetch messages
    function fetchMessages() {
        fetch(`${apiUrl}/data/messages`)
            .then(response => response.json())
            .then(data => {
                const chatBox = document.getElementById('chat-box');
                chatBox.innerHTML = '';
                data.forEach(message => {
                    chatBox.innerHTML += `
                        <div class="message-box">
                            <strong>${message.username}:</strong> ${message.text}
                            <div class="btn-group">
                                <button class="btn btn-sm btn-warning" onclick="editMessage('${message.id}', '${message.username}', '${message.text}')">Edit</button>
                                <button class="btn btn-sm btn-danger" onclick="deleteMessage('${message.id}')">Delete</button>
                            </div>
                        </div>`;
                });
                chatBox.scrollTop = chatBox.scrollHeight; // Scroll to bottom
            });
    }

    // send a new message
    const postMessage = (username, text) => {
        fetch(`${apiUrl}/data/messages`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `username=${encodeURIComponent(username)}&text=${encodeURIComponent(text)}`
        }).then(response => {
            if (response.status === 201) {
                fetchMessages(); // Fetch messages after posting
            } else {
                console.error('Failed to post message:', response.statusText);
            }
        }).catch(error => {
            console.error('Error posting message:', error);
        });
    };

    // delete a message
    function deleteMessage(id) {
        console.log(`Deleting message ID: ${id}`);
        fetch(`${apiUrl}/data/messages/${id}`, {
            method: 'DELETE'
        }).then(() => {
            console.log(`Deleted message ID: ${id}`);
            fetchMessages();
        }).catch(error => {
            console.error('Error deleting message:', error);
        });
    }

    // update a message
    function updateMessage(id, username, text) {
        console.log(`Updating message ID: ${id}`);
        fetch(`${apiUrl}/data/messages/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `username=${encodeURIComponent(username)}&text=${encodeURIComponent(text)}`
        }).then(() => {
            console.log(`Updated message ID: ${id}`);
            fetchMessages();
        }).catch(error => {
            console.error('Error updating message:', error);
        });
    }

    // edit message
    function editMessage(id, username, text) {
        console.log(`Editing message ID: ${id}, Username: ${username}, Text: ${text}`);
        document.getElementById('editMessageId').value = id;
        document.getElementById('editUsername').value = username;
        document.getElementById('editMessage').value = text;
        $('#editModal').modal('show');
    }

    document.getElementById('message-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const message = document.getElementById('message').value;
        postMessage(username, message);
        document.getElementById('message-form').reset();
    });

    document.getElementById('refresh-btn').addEventListener('click', fetchMessages);

    document.getElementById('saveEditButton').addEventListener('click', function() {
        const id = document.getElementById('editMessageId').value;
        const username = document.getElementById('editUsername').value;
        const message = document.getElementById('editMessage').value;
        updateMessage(id, username, message);
        $('#editModal').modal('hide');
    });

    // fetch messages on initial load
    fetchMessages();
</script>
</body>
</html>
