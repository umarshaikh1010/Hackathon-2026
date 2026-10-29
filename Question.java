public class Question {
    private final int id;
    private final int lessonId;
    private final String questionText;
    private final String[] options;
    private final int correctAnswerIndex;

    public Question(int id, int lessonId, String questionText, String[] options, int correctAnswerIndex) {
        this.id = id;
        this.lessonId = lessonId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public int getId() { return id; }
    public int getLessonId() { return lessonId; }
    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
}