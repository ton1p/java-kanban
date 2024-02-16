import java.util.Objects;
import java.util.UUID;

public class Task {
    public String name;

    public String description;

    private String id;

    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
