public class Subject {
    private final int id;
    private final String name;
    private final String iconSvgPath;
    private final String description;
    private final String color;

    public Subject(int id, String name, String iconSvgPath, String description, String color) {
        this.id = id;
        this.name = name;
        this.iconSvgPath = iconSvgPath;
        this.description = description;
        this.color = color;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIconSvgPath() { return iconSvgPath; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
}