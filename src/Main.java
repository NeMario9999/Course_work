import java.util.*;


public class Main {

    private static Map<String, Set<String>> prereqs = new HashMap<>();
    private static Map<String, Set<String>> studentRecords = new HashMap<>();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printHelp();
        String input;

        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toUpperCase();
            String[] argsArray = Arrays.copyOfRange(parts, 1, parts.length);

            try {
                switch (command) {
                    case "HELP":
                        printHelp();
                        break;
                    case "ADD_COURSE":
                        handleAddCourse(argsArray);
                        break;
                    case "ADD_PREREQ":
                        handleAddPrereq(argsArray);
                        break;
                    case "PREREQS":
                        handlePrereqs(argsArray);
                        break;
                    case "COMPLETE":
                        handleComplete(argsArray);
                        break;
                    case "DONE":
                        handleDone(argsArray);
                        break;
                    case "CAN_TAKE":
                        handleCanTake(argsArray);
                        break;
                    case "EXIT":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Unknown command. Type HELP to see available commands.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Error: Missing arguments for command " + command);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("\n--- Course Enrollment Planner ---");
        System.out.println("Available Commands:");
        System.out.println("  HELP");
        System.out.println("  ADD_COURSE <C>");
        System.out.println("  ADD_PREREQ <C> <P>");
        System.out.println("  PREREQS <C>");
        System.out.println("  COMPLETE <student> <C>");
        System.out.println("  DONE <student>");
        System.out.println("  CAN_TAKE <student> <C>");
        System.out.println("  EXIT");
        System.out.println("----------------------------------\n");
    }

    private static void validateIdentifier(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be empty.");
        }
        if (!id.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Identifier must contain only letters, digits, or underscores.");
        }
    }

    private static void handleAddCourse(String[] args) {
        if (args.length < 1) throw new ArrayIndexOutOfBoundsException();
        String course = args[0];
        validateIdentifier(course);

        prereqs.putIfAbsent(course, new HashSet<>());
        System.out.println("Added course: " + course);
    }

    private static void handleAddPrereq(String[] args) {
        if (args.length < 2) throw new ArrayIndexOutOfBoundsException();
        String course = args[0];
        String prereqCourse = args[1];

        validateIdentifier(course);
        validateIdentifier(prereqCourse);

        if (course.equals(prereqCourse)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }

        prereqs.putIfAbsent(course, new HashSet<>());
        prereqs.putIfAbsent(prereqCourse, new HashSet<>());

        prereqs.get(course).add(prereqCourse);
        System.out.println("Added prereq: " + prereqCourse + " -> " + course);

        if (prereqs.getOrDefault(prereqCourse, new HashSet<>()).contains(course)) {
            System.out.println("Warning: Potential cycle detected between " + course + " and " + prereqCourse);
        }
    }

    private static void handlePrereqs(String[] args) {
        if (args.length < 1) throw new ArrayIndexOutOfBoundsException();
        String course = args[0];
        validateIdentifier(course);

        Set<String> coursePrereqs = prereqs.get(course);
        if (coursePrereqs == null) {
            System.out.println("Course not found");
        } else {
            System.out.println("Prereqs for " + course + ": " + coursePrereqs);
        }
    }

    private static void handleComplete(String[] args) {
        if (args.length < 2) throw new ArrayIndexOutOfBoundsException();
        String student = args[0];
        String course = args[1];

        validateIdentifier(student);
        validateIdentifier(course);

        studentRecords.putIfAbsent(student, new HashSet<>());

        prereqs.putIfAbsent(course, new HashSet<>());

        studentRecords.get(student).add(course);
        System.out.println(student + " completed " + course);
    }

    private static void handleDone(String[] args) {
        if (args.length < 1) throw new ArrayIndexOutOfBoundsException();
        String student = args[0];
        validateIdentifier(student);

        Set<String> completed = studentRecords.get(student);
        if (completed == null || completed.isEmpty()) {
            System.out.println("No record");
        } else {
            System.out.println("Courses completed by " + student + ": " + completed);
        }
    }

    private static void handleCanTake(String[] args) {
        if (args.length < 2) throw new ArrayIndexOutOfBoundsException();
        String student = args[0];
        String course = args[1];

        validateIdentifier(student);
        validateIdentifier(course);

        Set<String> requiredPrereqs = prereqs.get(course);

        if (requiredPrereqs == null || requiredPrereqs.isEmpty()) {
            System.out.println("YES");
            return;
        }

        Set<String> completedCourses = studentRecords.getOrDefault(student, new HashSet<>());

        if (completedCourses.containsAll(requiredPrereqs)) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }
}