package enfok.server.model.entity.bd;

public enum TransformationType {
    FLIP(1, "flip", 0.20),
    CROP(2, "crop", 0.25),
    GRAYSCALE(3, "grayscale", 0.35),
    BRIGHTNESS(4, "brightness", 0.60),
    CONTRAST(5, "contrast", 0.60),
    BRIGHTNESS_CONTRAST(6, "brightness_contrast", 0.60),
    ROTATE(7, "rotate", 0.65),
    RESIZE(8, "resize", 0.95),
    SHARPEN(9, "sharpen", 1.00),
    WATERMARK(10, "watermark", 1.10),
    WATERMARK_TEXT(11, "watermark_text", 1.10),
    BLUR(12, "blur", 1.30),
    OCR(13, "ocr", 3.20),
    INFERENCE(14, "inference", 3.80),
    
    // Convertidores
    FORMAT_BMP(15, "format:bmp", 0.255),
    FORMAT_JPG(16, "format:jpg", 0.300),
    FORMAT_JPEG(17, "format:jpeg", 0.300),
    FORMAT_PNG(18, "format:png", 0.315),
    FORMAT_TIF(19, "format:tif", 0.345),
    FORMAT_TIFF(20, "format:tiff", 0.345),
    FORMAT_ICO(21, "format:ico", 0.360),
    FORMAT_WEBP(22, "format:webp", 0.375),
    FORMAT_GIF(23, "format:gif", 0.405),
    
    UNKNOWN(99, "unknown", 1.0);

    private final int id;
    private final String name;
    private final double costWeight;

    TransformationType(int id, String name, double costWeight) {
        this.id = id;
        this.name = name;
        this.costWeight = costWeight;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getCostWeight() { return costWeight; }

    /**
     * Busca la constante según su TypeId almacenado en Base de Datos.
     */
    public static double getWeightById(int typeId) {
        for (TransformationType t : TransformationType.values()) {
            if (t.getId() == typeId) {
                return t.getCostWeight();
            }
        }
        return UNKNOWN.getCostWeight();
    }

    /**
     * Busca la constante según el nombre textual (ej: "blur", "format:png").
     * Ignora mayúsculas/minúsculas.
     */
    public static double getWeightByName(String name) {
        if (name == null || name.isEmpty()) return UNKNOWN.getCostWeight();
        for (TransformationType t : TransformationType.values()) {
            if (t.getName().equalsIgnoreCase(name.trim())) {
                return t.getCostWeight();
            }
        }
        return UNKNOWN.getCostWeight();
    }
}
