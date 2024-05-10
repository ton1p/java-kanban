package ru.yandex.schedule.tasks.enums;

public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    public static Status getStatusByString(String str) {
        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(str)) {
                return status;
            }
        }
        return null;
    }
}
