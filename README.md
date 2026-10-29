# Commonplace Study Portal

A low-bandwidth, responsive study portal built with semantic HTML, custom CSS, and Java's built-in HTTP server. The server stores its demo subjects, lesson chunks, and quiz question in in-memory Java collections; there are no external libraries or database setup steps.

## Run

Requires a JDK 11 or newer. From the project root:

```powershell
javac -d out src/main/java/*.java
java -cp out StudyPortal
```

Open http://localhost:8080/home. The server binds to `127.0.0.1:8080` and serves `/home`, `/lesson?page=1`, and `/quiz`.

## Routes

- `/home` shows the subject dashboard.
- `/lesson?page=1` through `/lesson?page=3` paginate the lesson content.
- `/quiz` shows the quiz; submitting the form returns an in-memory score result.
  
