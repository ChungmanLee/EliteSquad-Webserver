<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Scrum Board Application</title>
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
            max-width: 1200px;
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .board-column {
            width: 30%;
            display: inline-block;
            vertical-align: top;
            margin-right: 1%;
        }
        .board-column:last-child {
            margin-right: 0;
        }
        .board-column h2 {
            text-align: center;
            color: #007bff;
        }
        .todo-item {
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 10px;
            margin-bottom: 10px;
            position: relative;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .todo-item strong {
            display: block;
            margin-bottom: 5px;
            color: #007bff;
        }
        .todo-item .btn-group {
            position: absolute;
            top: 10px;
            right: 10px;
        }
        .todo-item .btn {
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
        .board-column {
            height: 600px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Scrum Board Application</h1>
    <div class="form-group">
        <input type="text" id="task" class="form-control" placeholder="Add a new task" required>
        <input type="text" id="assignee" class="form-control mt-2" placeholder="Assignee" required>
        <button id="add-task-btn" class="btn btn-primary mt-2">Add Task</button>
    </div>
    <div class="row">
        <div class="board-column" id="backlog">
            <h2>Backlog</h2>
        </div>
        <div class="board-column" id="in-progress">
            <h2>In Progress</h2>
        </div>
        <div class="board-column" id="done">
            <h2>Done</h2>
        </div>
    </div>
</div>

<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editModalLabel">Edit Task</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="editTaskId">
                <div class="form-group">
                    <textarea id="editTask" class="form-control" rows="3" placeholder="Edit your task..." required></textarea>
                </div>
                <div class="form-group">
                    <input type="text" id="editAssignee" class="form-control" placeholder="Assignee" required>
                </div>
                <div class="form-group">
                    <select id="editStatus" class="form-control" required>
                        <option value="backlog">Backlog</option>
                        <option value="in-progress">In Progress</option>
                        <option value="done">Done</option>
                    </select>
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

    // fetch tasks
    function fetchTasks() {
        fetch(`${apiUrl}/data/tasks`)
            .then(response => response.json())
            .then(data => {
                const backlog = document.getElementById('backlog');
                const inProgress = document.getElementById('in-progress');
                const done = document.getElementById('done');
                backlog.innerHTML = '<h2>Backlog</h2>';
                inProgress.innerHTML = '<h2>In Progress</h2>';
                done.innerHTML = '<h2>Done</h2>';
                data.forEach(task => {
                    const taskElement = `
                        <div class="todo-item" id="task-${task.id}">
                            <strong>${task.task}</strong>
                            <p>Assignee: ${task.assignee}</p>
                            <p>Status: ${task.status}</p>
                            <div class="btn-group">
                                <button class="btn btn-sm btn-warning" onclick="editTask('${task.id}', '${task.task}', '${task.assignee}', '${task.status}')">Edit</button>
                                <button class="btn btn-sm btn-danger" onclick="deleteTask('${task.id}')">Delete</button>
                            </div>
                        </div>`;
                    if (task.status === 'backlog') {
                        backlog.innerHTML += taskElement;
                    } else if (task.status === 'in-progress') {
                        inProgress.innerHTML += taskElement;
                    } else if (task.status === 'done') {
                        done.innerHTML += taskElement;
                    }
                });
            });
    }

    // add a new task
    const postTask = (task, assignee) => {
        fetch(`${apiUrl}/data/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `task=${encodeURIComponent(task)}&assignee=${encodeURIComponent(assignee)}&status=backlog`
        }).then(response => {
            if (response.status === 201) {
                fetchTasks(); // Fetch tasks after posting
            } else {
                console.error('Failed to post task:', response.statusText);
            }
        }).catch(error => {
            console.error('Error posting task:', error);
        });
    };

    // delete a task
    function deleteTask(id) {
        console.log(`Deleting task ID: ${id}`);
        fetch(`${apiUrl}/data/tasks/${id}`, {
            method: 'DELETE'
        }).then(() => {
            console.log(`Deleted task ID: ${id}`);
            fetchTasks();
        }).catch(error => {
            console.error('Error deleting task:', error);
        });
    }

    // update a task
    function updateTask(id, task, assignee, status) {
        console.log(`Updating task ID: ${id}`);
        fetch(`${apiUrl}/data/tasks/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `task=${encodeURIComponent(task)}&assignee=${encodeURIComponent(assignee)}&status=${encodeURIComponent(status)}`
        }).then(() => {
            console.log(`Updated task ID: ${id}`);
            fetchTasks();
        }).catch(error => {
            console.error('Error updating task:', error);
        });
    }

    // edit task
    function editTask(id, task, assignee, status) {
        console.log(`Editing task ID: ${id}, Task: ${task}, Assignee: ${assignee}, Status: ${status}`);
        document.getElementById('editTaskId').value = id;
        document.getElementById('editTask').value = task;
        document.getElementById('editAssignee').value = assignee;
        document.getElementById('editStatus').value = status;
        $('#editModal').modal('show');
    }

    document.getElementById('add-task-btn').addEventListener('click', function(e) {
        e.preventDefault();
        const task = document.getElementById('task').value;
        const assignee = document.getElementById('assignee').value;
        postTask(task, assignee);
        document.getElementById('task').value = '';
        document.getElementById('assignee').value = '';
    });

    document.getElementById('saveEditButton').addEventListener('click', function() {
        const id = document.getElementById('editTaskId').value;
        const task = document.getElementById('editTask').value;
        const assignee = document.getElementById('editAssignee').value;
        const status = document.getElementById('editStatus').value;
        updateTask(id, task, assignee, status);
        $('#editModal').modal('hide');
    });

    // fetch tasks on initial load
    fetchTasks();
</script>
</body>
</html>
