package enfok.server.model.entity.bd;

public enum TransformationType {

    // 📐 Geometría
    FLIP(1, "flip", 0.15),
    CROP(2, "crop", 0.20),
    ROTATE(3, "rotate", 0.30),
    RESIZE(4, "resize", 0.60),

    // 🎨 Filtros básicos
    GRAYSCALE(5, "grayscale", 0.25),
    SEPIA(6, "sepia", 0.35),
    COLOR_TINT(7, "color_tint", 0.40),
    POSTERIZE(8, "posterize", 0.45),
    PIXELATE(9, "pixelate", 0.50),

    // ✨ Ajustes
    BRIGHTNESS(10, "brightness", 0.30),
    CONTRAST(11, "contrast", 0.30),
    BRIGHTNESS_CONTRAST(12, "brightness_contrast", 0.45),
    BLUR(13, "blur", 0.55),
    SHARPEN(14, "sharpen", 0.65),

    // 🏷️ Anotación
    WATERMARK_TEXT(15, "watermark_text", 0.80),

    // 🧠 Inteligencia
    OCR(16, "ocr", 3.00),
    INFERENCE(17, "inference", 4.00),

    // 📦 Conversión de formato (I/O + compresión)
    FORMAT_BMP(18, "format:bmp", 0.20),
    FORMAT_JPG(19, "format:jpg", 0.30),
    FORMAT_JPEG(20, "format:jpeg", 0.30),
    FORMAT_PNG(21, "format:png", 0.35),
    FORMAT_TIF(22, "format:tif", 0.40),
    FORMAT_TIFF(23, "format:tiff", 0.40),
    FORMAT_ICO(24, "format:ico", 0.45),
    FORMAT_WEBP(25, "format:webp", 0.50),
    FORMAT_GIF(26, "format:gif", 0.55),

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

    public static double getWeightById(int typeId) {
        for (TransformationType t : TransformationType.values()) {
            if (t.getId() == typeId) {
                return t.getCostWeight();
            }
        }
        return UNKNOWN.getCostWeight();
    }

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