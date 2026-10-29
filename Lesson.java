public class Lesson {
    private final int id;
    private final int subjectId;
    private final String title;
    private final String contentChunk;

    public Lesson(int id, int subjectId, String title, String contentChunk) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.contentChunk = contentChunk;
    }

    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getTitle() { return title; }
    public String getContentChunk() { return contentChunk; }
}