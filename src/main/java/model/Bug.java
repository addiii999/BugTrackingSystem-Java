package model;

public class Bug {
    public int id;
    public String title;
    public String description;
    public String priority;
    public String status;
    public String assignedTo;

    public Bug(int id, String title, String description, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = "Open";
        this.assignedTo = "Not Assigned";
    }
}
