package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();

    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();
    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.removeSubTasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(1);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .timeout(Duration.ofSeconds(30))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllTasks()), response.body(), "Не совпали возвращаемые значения");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getTaskById(task.getId()).get()), response.body(), "Не совпали возвращаемые значения");

        url = URI.create("http://localhost:8080/tasks/100500");
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testRemoveTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getTaskById(task.getId()).get()), response.body(), "Не совпали возвращаемые значения");
    }

    @Test
    public void testAddIntersectionTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(1));
        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testAddAndGetEpicAndSubtask() throws IOException, InterruptedException {
        //добавляем эпик
        Epic epic = new Epic("Сделать список дел", "Он ниже");
        String epicGson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicGson))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //добавляем все сабтаски
        Subtask subtask1 = new Subtask("дело 1", "важное",
                Status.NEW, 0, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(1));
        Subtask subtask2 = new Subtask("дело 2", "важное",
                Status.NEW, 0, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(10));
        String subtaskGson1 = gson.toJson(subtask1);
        String subtaskGson2 = gson.toJson(subtask2);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskGson1))
                .timeout(Duration.ofSeconds(30))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskGson2))
                .timeout(Duration.ofSeconds(30))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        //получаем все сабтаски
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllSubTasks()), response.body(), "Не совпали возвращаемые значения");
        //получаем 1 сабтаск
        url = URI.create("http://localhost:8080/subtasks/" + 1);
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getSubTaskById(1).get()), response.body(), "Не совпали возвращаемые значения");
        //получаем 1  несуществующий сабтаск
        url = URI.create("http://localhost:8080/subtasks/" + 100500);
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testIntersectionSubtask() throws IOException, InterruptedException {
        //добавляем эпик
        Epic epic = new Epic("Сделать список дел", "Он ниже");
        String epicGson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicGson))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

//добавляем все сабтаски
        Subtask subtask1 = new Subtask("дело 1", "важное",
                Status.NEW, 0, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(1));
        Subtask subtask2 = new Subtask("дело 2", "важное",
                Status.NEW, 0, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(2));
        String subtaskGson1 = gson.toJson(subtask1);
        String subtaskGson2 = gson.toJson(subtask2);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskGson1))
                .timeout(Duration.ofSeconds(30))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskGson2))
                .timeout(Duration.ofSeconds(30))
                .build();
        response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size());
    }

    @Test
    public void testDeleteEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Сделать список дел", "Он ниже");
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("дело 1", "важное",
                Status.NEW, epic.getId(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(1));
        Subtask subtask2 = new Subtask("дело 2", "важное",
                Status.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(10));
        manager.addNewSubTask(subtask1);
        manager.addNewSubTask(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(Optional.empty(), manager.getSubTaskById(subtask1.getId()));

        url = URI.create("http://localhost:8080/epics/" + epic.getId());
        request = HttpRequest.newBuilder().uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .timeout(Duration.ofSeconds(30))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(Optional.empty(), manager.getEpicById(epic.getId()));
    }

    @Test
    public void testGetPrioritizedTasks() throws InterruptedException, IOException {
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task(
                "Навестить бабушку",
                "Купить ей торт",
                Status.NEW,
                Duration.ofMinutes(120),
                LocalDateTime.now().plusDays(2)
        );

        client.send(HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build(),
                HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(
                "Навестить дедушку",
                "Купить ему селедку",
                Status.NEW,
                Duration.ofMinutes(120),
                LocalDateTime.now().plusDays(1)
        );
        client.send(HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build(), HttpResponse.BodyHandlers.ofString());

        Set<Task> tasksFromManager = manager.getPrioritizedTasks();

        url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), "Статус код должен быть 200");
        Assertions.assertEquals(gson.toJson(tasksFromManager), response.body(), "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testGetHistoryTasks() throws InterruptedException, IOException {
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task(
                "Навестить бабушку",
                "Купить ей торт",
                Status.NEW,
                Duration.ofMinutes(120),
                LocalDateTime.now().plusDays(2)
        );

        client.send(HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build(),
                HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(
                "Навестить дедушку",
                "Купить ему селедку",
                Status.NEW,
                Duration.ofMinutes(120),
                LocalDateTime.now().plusDays(1)
        );
        client.send(HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build(), HttpResponse.BodyHandlers.ofString());

        manager.getTaskById(1);

        url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), "Статус код должен быть 200");
        Assertions.assertEquals(gson.toJson(manager.getHistory()), response.body(), "Ответ не совпадает с ожидаемым");
    }
}
