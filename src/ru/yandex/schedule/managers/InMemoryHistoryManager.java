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

    private final Map<String, Node> nodeMap;

    public InMemoryHistoryManager() {
        this.nodeMap = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return this.getTasks();
    }

    private void linkLast(Task task) {
        Node node = nodeMap.get(task.getId());

        if (node != null) {
            node.prev = node.next;
            node.data = task;
        } else {
            node = new Node(null, task, null);
        }

        if (head == null) {
            head = tail = node;
        } else {
            if (!node.equals(head) && !node.equals(tail)) {
                node.prev = tail;
                tail.next = node;
                tail = node;
            }
        }
        nodeMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
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
        Node currentNode = this.head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        this.linkLast(task);
    }

    @Override
    public void remove(String id) {
        Node node = this.nodeMap.get(id);
        this.removeNode(node);
    }
}
