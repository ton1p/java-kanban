package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    static class Node {
        public Node prev;

        public Task data;
        public Node next;


        public Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(prev, node.prev) && Objects.equals(data, node.data) && Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, data, next);
        }
    }

    private Node head;

    private Node tail;

    private final Map<Integer, Node> nodeMap;

    public InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        Node node = new Node(null, task, null);
        if (head == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        return node;
    }

    private void removeNode(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            if (node.equals(head)) {
                head = node.next;
            } else if (node.equals(tail)) {
                tail = node.prev;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            removeNode(task.getId());
        }
        nodeMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }
}
