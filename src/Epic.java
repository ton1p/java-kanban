import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    private Status status;

    public Status getStatus() {
        return this.status;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
        this.status = this.computeStatus();
    }

    public Status computeStatus() {
        int doneSize = 0;
        int newSize = 0;

        if (subTasks.isEmpty()) {
            return Status.NEW;
        }

        for (SubTask subTask : subTasks) {
            if (subTask.status == Status.DONE) {
                doneSize++;
            }
            if (subTask.status == Status.NEW) {
                newSize++;
            }
        }

        if (doneSize == subTasks.size()) {
            return Status.DONE;
        }

        if (newSize == subTasks.size()) {
            return  Status.NEW;
        }

        return Status.IN_PROGRESS;
    }
}
