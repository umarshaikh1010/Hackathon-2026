import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyPortal {
    private static final List<Subject> SUBJECTS = new ArrayList<>();
    private static final List<Lesson> LESSONS = new ArrayList<>();
    private static final List<Question> QUESTIONS = new ArrayList<>();
    private static final String[] SECTION_TITLES = {"A living system", "The energy exchange", "A changing world"};
    private static final String[] SECTION_TEXT = {
        "Ecology is the study of how living things relate to one another and to the places they inhabit. A forest, for example, is not simply a collection of trees. It is a connected system of plants, animals, fungi, water, soil, and climate.",
        "Energy moves through an ecosystem in one direction. Producers such as plants capture sunlight and store it as chemical energy. Consumers obtain that energy by eating plants or other animals, while decomposers return nutrients to the soil.",
        "Ecosystems are always responding to change. A shift in rainfall can alter plant growth; that change may affect the animals that depend on those plants. Understanding these relationships helps us make better decisions about conservation and the resources we share."
    };

    public static void main(String[] args) throws IOException {
        seedData();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/", StudyPortal::handleRequest);
        server.start();
        System.out.println("Study Portal is running at http://localhost:8080/home");
    }

    private static void seedData() {
        SUBJECTS.add(new Subject(1, "Life sciences", "M12 3 4 7v5c0 5 3.4 8.8 8 10 4.6-1.2 8-5 8-10V7l-8-4Zm0 4.2 4 2v2.8c0 3-1.6 5.5-4 6.7-2.4-1.2-4-3.7-4-6.7V9.2l4-2Z", "Cells, ecosystems & the living world", "sage"));
        SUBJECTS.add(new Subject(2, "Mathematics", "M4 19h16v2H2V3h2v16Zm2-2 4.5-5.5 3 3L20 7.5 18.5 6l-5 5.5-3-3L6 15.5 7.5 17Z", "Patterns, proof & problem solving", "blue"));
        SUBJECTS.add(new Subject(3, "Physical sciences", "M12 2a2 2 0 0 0-2 2c0 .4.1.8.3 1.1A8 8 0 0 0 4 12a8 8 0 0 0 16 0 8 8 0 0 0-6.3-7.8c.2-.3.3-.7.3-1.1a2 2 0 0 0-2-2Zm0 6a4 4 0 1 1 0 8 4 4 0 0 1 0-8Zm0 2a2 2 0 1 0 0 4 2 2 0 0 0 0-4Z", "Matter, motion & the forces around us", "coral"));
        SUBJECTS.add(new Subject(4, "World history", "M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm7.9 9h-3a15.7 15.7 0 0 0-1.4-5.1 8 8 0 0 1 4.4 5.1ZM12 4c.9 1.2 1.6 3.2 1.9 7h-3.8c.3-3.8 1-5.8 1.9-7ZM4.1 13h3a15.7 15.7 0 0 0 1.4 5.1A8 8 0 0 1 4.1 13Zm3-2h-3a8 8 0 0 1 4.4-5.1A15.7 15.7 0 0 0 7.1 11ZM12 20c-.9-1.2-1.6-3.2-1.9-7h3.8c-.3 3.8-1 5.8-1.9 7Zm3.5-1.9a15.7 15.7 0 0 0 1.4-5.1h3a8 8 0 0 1-4.4 5.1Z", "Ideas, people & the shape of the past", "gold"));
        for (int section = 0; section < SECTION_TITLES.length; section++) {
            LESSONS.add(new Lesson(section + 1, 1, SECTION_TITLES[section], SECTION_TEXT[section]));
        }
        QUESTIONS.add(new Question(1, 1, "Which organisms capture sunlight and store it as chemical energy?", new String[]{"Consumers", "Producers", "Decomposers", "Predators"}, 1));
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/style.css")) {
            serveStylesheet(exchange);
            return;
        }
        if (path.equals("/") || path.equals("/home")) {
            sendHtml(exchange, homePage());
        } else if (path.equals("/lesson")) {
            int section = parseInt(queryParams(exchange).get("page"), 1);
            sendHtml(exchange, lessonPage(section));
        } else if (path.equals("/quiz") && exchange.getRequestMethod().equals("POST")) {
            sendHtml(exchange, quizResult(exchange));
        } else if (path.equals("/quiz")) {
            sendHtml(exchange, quizPage(null));
        } else {
            sendHtml(exchange, notFoundPage());
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
        }
    }

    private static String homePage() {
        StringBuilder cards = new StringBuilder();
        for (Subject subject : SUBJECTS) {
            cards.append("<a class=\"subject-card ").append(subject.getColor()).append("\" href=\"/lesson?page=1\">")
                .append("<span class=\"subject-icon\"><svg viewBox=\"0 0 24 24\" aria-hidden=\"true\"><path d=\"").append(subject.getIconSvgPath()).append("\"/></svg></span>")
                .append("<span class=\"card-index\">0").append(subject.getId()).append("</span>")
                .append("<h2>").append(escape(subject.getName())).append("</h2><p>").append(escape(subject.getDescription())).append("</p>")
                .append("<span class=\"card-link\">Explore subject <span aria-hidden=\"true\">↗</span></span></a>");
        }
        return layout("Your learning, in focus", "home", "<main class=\"shell dashboard\"><header class=\"page-intro\"><p class=\"eyebrow\">A little progress, every day</p><h1>Make room<br>for <em>curiosity.</em></h1><p class=\"intro-copy\">A quieter place to learn what matters. Choose a subject and pick up where you left off.</p></header>"
            + "<section class=\"subject-section\" aria-labelledby=\"subjects-title\"><div class=\"section-heading\"><h2 id=\"subjects-title\">Your subjects</h2><span>04 COURSES</span></div><div class=\"subject-grid\">" + cards + "</div></section>"
            + "<section class=\"continue-row\"><div><p class=\"eyebrow\">Continue learning</p><h2>Ecology: connections in nature</h2><p>Life sciences <span aria-hidden=\"true\">·</span> 3 short sections</p></div><a class=\"text-link\" href=\"/lesson?page=1\">Resume lesson <span aria-hidden=\"true\">→</span></a></section></main>");
    }

    private static String lessonPage(int requestedSection) {
        int section = Math.max(1, Math.min(requestedSection, LESSONS.size()));
        Lesson lesson = LESSONS.get(section - 1);
        String[] paragraphs = lesson.getContentChunk().split("\\n\\n");
        StringBuilder body = new StringBuilder();
        for (String paragraph : paragraphs) body.append("<p>").append(escape(paragraph)).append("</p>");
        String previous = section > 1 ? "<a class=\"previous-link\" href=\"/lesson?page=" + (section - 1) + "\">← Previous</a>" : "<a class=\"previous-link\" href=\"/home\">← All subjects</a>";
        String next = section < LESSONS.size()
            ? "<a class=\"button button-dark\" href=\"/lesson?page=" + (section + 1) + "\">Next section <span aria-hidden=\"true\">→</span></a>"
            : "<a class=\"button button-dark\" href=\"/quiz\">Check your understanding <span aria-hidden=\"true\">→</span></a>";
        String content = "<main class=\"reader-shell\"><div class=\"reader-topline\"><a href=\"/home\">Life sciences</a><span>LESSON 01 <i></i> SECTION 0" + section + " OF 0" + LESSONS.size() + "</span></div>"
            + "<article class=\"lesson-article\"><p class=\"eyebrow\">Ecology · 6 min read</p><h1>Connections<br><em>in nature.</em></h1><div class=\"reading-progress\" aria-label=\"Section " + section + " of " + LESSONS.size() + "\"><span style=\"width:" + (section * 100 / LESSONS.size()) + "%\"></span></div>"
            + "<div class=\"section-label\"><span>0" + section + "</span><h2>" + escape(lesson.getTitle()) + "</h2></div><div class=\"lesson-copy\">" + body + "</div>"
            + "<aside class=\"reader-note\"><span class=\"note-mark\">✳</span><p><strong>Keep in mind</strong><br>Every organism is part of a larger system. Follow the connections, not just the individual pieces.</p></aside></article>"
            + "<nav class=\"reader-nav\" aria-label=\"Lesson navigation\"><div class=\"reader-nav-inner\">" + previous + next + "</div></nav></main>";
        return layout("Connections in nature", "lesson", content);
    }

    private static String quizPage(String result) {
        String content;
        if (result != null) {
            content = "<main class=\"quiz-shell result-shell\"><a class=\"back-link\" href=\"/home\">← Back to subjects</a><section class=\"result-card\"><span class=\"result-symbol\">" + (result.equals("correct") ? "✓" : "↗") + "</span><p class=\"eyebrow\">Lesson check · Ecology</p><h1>" + (result.equals("correct") ? "Nicely understood." : "Keep connecting the dots.") + "</h1><p class=\"result-grade\">" + (result.equals("correct") ? "1 / 1" : "0 / 1") + " <span>correct</span></p><p class=\"result-copy\">" + (result.equals("correct") ? "Producers are the starting point for energy in an ecosystem. You have the idea." : "Plants and other producers capture sunlight and store it as energy. A quick reread will make it stick.") + "</p><a class=\"button button-dark\" href=\"/lesson?page=1\">" + (result.equals("correct") ? "Read again" : "Review lesson") + " <span aria-hidden=\"true\">→</span></a></section></main>";
        } else {
            Question question = QUESTIONS.get(0);
            StringBuilder options = new StringBuilder();
            String[] answers = question.getOptions();
            for (int index = 0; index < answers.length; index++) {
                options.append("<label class=\"answer-option\"><input type=\"radio\" name=\"answer\" value=\"").append(index).append("\" required><span class=\"option-letter\">").append((char) ('A' + index)).append("</span><span class=\"option-text\"> ").append(escape(answers[index])).append("</span><span class=\"option-check\" aria-hidden=\"true\">✓</span></label>");
            }
            content = "<main class=\"quiz-shell\"><a class=\"back-link\" href=\"/lesson?page=3\">← Back to lesson</a><section class=\"quiz-content\"><p class=\"eyebrow\">A moment to reflect <span>01 / 01</span></p><h1>What stayed<br><em>with you?</em></h1><form action=\"/quiz\" method=\"post\"><fieldset><legend>" + escape(question.getQuestionText()) + "</legend><div class=\"answer-list\">" + options + "</div></fieldset><button class=\"button button-dark submit-button\" type=\"submit\">See my result <span aria-hidden=\"true\">→</span></button></form></section></main>";
        }
        return layout(result == null ? "Lesson check" : "Your result", "quiz", content);
    }

    private static String quizResult(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseForm(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        int answer = parseInt(form.get("answer"), -1);
        return quizPage(answer == QUESTIONS.get(0).getCorrectAnswerIndex() ? "correct" : "incorrect");
    }

    private static String notFoundPage() {
        return layout("Page not found", "", "<main class=\"shell not-found\"><p class=\"eyebrow\">404 · Not found</p><h1>That page<br><em>isn't here.</em></h1><a class=\"button button-dark\" href=\"/home\">Return home <span aria-hidden=\"true\">→</span></a></main>");
    }

    private static String layout(String title, String active, String content) {
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><meta name=\"theme-color\" content=\"#f7f7f2\"><meta name=\"description\" content=\"A quieter place to learn. Open, accessible lessons designed for every connection.\"><title>" + escape(title) + " · Commonplace</title><link rel=\"stylesheet\" href=\"/style.css\"></head><body><header class=\"site-header\"><a class=\"wordmark\" href=\"/home\"><span class=\"brand-mark\">c.</span><span>commonplace</span></a><nav class=\"site-nav\" aria-label=\"Main navigation\"><a class=\"" + (active.equals("home") ? "active" : "") + "\" href=\"/home\">Subjects</a><a class=\"" + (active.equals("lesson") ? "active" : "") + "\" href=\"/lesson?page=1\">Learning</a><a class=\"" + (active.equals("quiz") ? "active" : "") + "\" href=\"/quiz\">Check-in</a></nav><span class=\"header-meta\">OPEN LEARNING <span>·</span> NO. 01</span></header>" + content + "<footer class=\"site-footer\"><span>COMMONPLACE <span>·</span> LEARN AT YOUR OWN PACE</span><span>MADE FOR EVERY CONNECTION</span></footer></body></html>";
    }

    private static void serveStylesheet(HttpExchange exchange) throws IOException {
        Path stylesheet = Path.of("src", "main", "resources", "static", "style.css");
        byte[] bytes = Files.readAllBytes(stylesheet);
        exchange.getResponseHeaders().set("Content-Type", "text/css; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "public, max-age=3600");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static Map<String, String> queryParams(HttpExchange exchange) {
        return parseForm(exchange.getRequestURI().getRawQuery() == null ? "" : exchange.getRequestURI().getRawQuery());
    }

    private static Map<String, String> parseForm(String raw) {
        Map<String, String> values = new HashMap<>();
        for (String pair : raw.split("&")) {
            if (pair.isEmpty()) continue;
            String[] parts = pair.split("=", 2);
            values.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8), parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "");
        }
        return values;
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (NumberFormatException exception) { return fallback; }
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}