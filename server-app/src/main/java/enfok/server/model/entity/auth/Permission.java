package enfok.server.model.entity.auth;

public class Permission {
    private int id;
    private String description;
    private String route;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
}
