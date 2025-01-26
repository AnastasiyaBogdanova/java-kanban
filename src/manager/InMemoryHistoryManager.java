package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> historyTasks = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {

        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node curNode = head;
        while (curNode != null) {
            tasks.add(curNode.data);
            curNode = curNode.next;
        }
        return tasks;
    }


    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            Node newNode = linkLast(task);
            historyTasks.put(task.getId(), newNode);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyTasks.get(id));
        historyTasks.remove(id);
    }

    private Node linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        final Node next = node.next;
        final Node prev = node.prev;
        node.data = null;
        if (head == node && tail == node) { //если элемент был только 1
            head = null;
            tail = null;
        } else if (head == node) { //если элемент был первым, то 2й элемнт становится первым
            head = next;
            head.prev = null;
        } else if (tail == node) { //если элемент был последним, то предпоследний элемнт становится последним
            tail = prev;
            tail.next = null;
        } else { //если элемент был где-то в середине
            prev.next = next;
            next.prev = prev;
        }

    }
}
