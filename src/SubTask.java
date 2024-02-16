public class SubTask extends Task {
    public String epicId;

    public SubTask(String name, String description, Status status, String epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
}
