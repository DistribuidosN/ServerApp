package enfok.server.model.entity.bd;

public class Transformation {
    private int id;
    private int imageId;
    private int typeId;
    private String name;   // Nombre textual: "blur", "ocr", "format:png", etc.
    private String params; // JSON string con parámetros

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
}
