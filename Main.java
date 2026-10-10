package hope;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/*
 * Assignment 4: More Features of OOP
 * DCST Information Management System
 * Daniel Fernandes | Roll No. 2615 | Group B1G3
 *
 * Compile/run from Eclipse: hope.Main
 */

enum MemberType { STUDENT, TEACHING_STAFF, NON_TEACHING_STAFF }
enum ActivityType { ACADEMIC, NON_ACADEMIC }

interface Reportable {
    String generateReport();
}

interface Displayable {
    void display();
}

/* Interface inheritance: Exportable inherits two interfaces. */
interface Exportable extends Reportable, Displayable {
    void exportToFile(String fileName) throws IOException;
}

/* Abstract class; subclasses implement getMemberType differently. */
abstract class Person implements Reportable, Cloneable {
    private String id;
    private String name;
    protected String department;
    public static int memberCount = 0;

    public Person(String id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
        memberCount++;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    public abstract MemberType getMemberType();

    @Override
    public String generateReport() {
        return getMemberType() + " | ID: " + id + " | Name: " + name
                + " | Department: " + department;
    }

    public static void displayMemberCount() {
        System.out.println("Total Person objects created: " + memberCount);
    }

    @Override
    public Person clone() {
        try {
            return (Person) super.clone(); // shallow clone
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}

class Student extends Person {
    private String programme;
    private int year;

    public Student(String id, String name, String department, String programme, int year) {
        super(id, name, department);
        this.programme = programme;
        this.year = year;
    }

    public String getProgramme() { return programme; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public MemberType getMemberType() { return MemberType.STUDENT; }

    @Override
    public String generateReport() {
        return super.generateReport() + " | Programme: " + programme + " | Year: " + year;
    }
}

class Staff extends Person {
    private String designation;
    private boolean teaching;

    public Staff(String id, String name, String department, String designation, boolean teaching) {
        super(id, name, department);
        this.designation = designation;
        this.teaching = teaching;
    }

    @Override
    public MemberType getMemberType() {
        return teaching ? MemberType.TEACHING_STAFF : MemberType.NON_TEACHING_STAFF;
    }

    @Override
    public String generateReport() {
        return super.generateReport() + " | Designation: " + designation;
    }
}

class Activity implements Reportable, Cloneable {
    private String name;
    private ActivityType type;
    private LocalDate date;
    private List<String> coordinators;

    public Activity(String name, ActivityType type, LocalDate date, List<String> coordinators) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.coordinators = coordinators;
    }

    public String getName() { return name; }
    public List<String> getCoordinators() { return coordinators; }

    @Override
    public String generateReport() {
        return type + " activity: " + name + " | Date: " + date
                + " | Coordinators: " + coordinators;
    }

    @Override
    public Activity clone() {
        try {
            return (Activity) super.clone(); // shallow clone shares the list
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public Activity deepClone() {
        return new Activity(name, type, date, new ArrayList<>(coordinators));
    }
}

class Course implements Reportable {
    private String code;
    private String name;
    private List<String> subjects;

    public Course(String code, String name, List<String> subjects) {
        this.code = code;
        this.name = name;
        this.subjects = new ArrayList<>(subjects);
    }

    @Override
    public String generateReport() {
        return code + " - " + name + " | Subjects: " + subjects;
    }
}

/* Generic class and generic static method. */
class Registry<T> {
    private final List<T> records = new ArrayList<>();

    public void add(T item) { records.add(item); }
    public List<T> getAll() { return Collections.unmodifiableList(records); }

    public static <E> void printItems(Collection<E> items) {
        for (E item : items) System.out.println("  - " + item);
    }
}

class AccessDemo {
    public String publicNote = "public: accessible from any class";
    private String privateNote = "private: accessible only inside AccessDemo";
    protected String protectedNote = "protected: accessible in the same package or a subclass";

    public void showInsideClass() {
        System.out.println(publicNote);
        System.out.println(privateNote);
        System.out.println(protectedNote);
    }
}

class InvalidRecordException extends Exception {
    public InvalidRecordException(String message) { super(message); }
}

/* Implements multiple interfaces. */
class ConsoleReporter implements Exportable {
    private String report;

    public ConsoleReporter(String report) { this.report = report; }

    @Override
    public String generateReport() { return report; }

    @Override
    public void display() { System.out.println(report); }

    @Override
    public void exportToFile(String fileName) throws IOException {
        Files.writeString(Path.of(fileName), report + System.lineSeparator());
    }
}

public class Main {
    private static final String DATA_FILE = "dcst-report.txt";

    /* Message passing: accepts any implementation of Reportable. */
    public static void printReport(Reportable item) {
        System.out.println(item.generateReport());
    }

    /* Overloaded methods. */
    public static void printHeading(String heading) {
        System.out.println("\n=== " + heading + " ===");
    }

    public static void printHeading(String heading, int width) {
        String line = "=".repeat(Math.max(3, width));
        System.out.println("\n" + line + "\n" + heading + "\n" + line);
    }

    private static void validateName(String name) throws InvalidRecordException {
        if (name == null || name.isBlank()) {
            throw new InvalidRecordException("Name cannot be blank.");
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("DCST INFORMATION MANAGEMENT SYSTEM");
        System.out.println("Assignment 4: Additional OOP Features");
        System.out.println("Programme/activity data below is illustrative; verify official details.");

        Student student = new Student("S001", "Sample Student", "DCST", "MCA", 1);
        Staff teacher = new Staff("T001", "Sample Teaching Staff", "DCST",
                "Teaching Staff", true);
        Staff administrator = new Staff("N001", "Sample Non-Teaching Staff", "DCST",
                "Administrative Staff", false);

        printHeading("1. OBJECTS, CONSTRUCTORS, ENUMS AND POLYMORPHISM");
        List<Person> people = new ArrayList<>();
        people.add(student);
        people.add(teacher);
        people.add(administrator);
        for (Person person : people) {
            printReport(person); // overriding and runtime polymorphism
        }
        Person.displayMemberCount();

        printHeading("2. OBJECT REFERENCING");
        Student referenceA = student;
        Student referenceB = referenceA;
        referenceB.setYear(2);
        System.out.println("Year through reference A: " + referenceA.getYear());
        System.out.println("Both references point to the same Student object.");

        printHeading("3. ACCESS MODIFIERS");
        AccessDemo access = new AccessDemo();
        System.out.println(access.publicNote);
        System.out.println(access.protectedNote);
        access.showInsideClass();
        System.out.println("Private field is accessed through its own class method, not directly.");

        printHeading("4. GENERICS AND COLLECTION FRAMEWORK");
        Registry<Person> registry = new Registry<>();
        people.forEach(registry::add);
        System.out.println("Generic Registry / List:");
        Registry.printItems(registry.getAll());

        Queue<String> tasks = new LinkedList<>();
        tasks.offer("Verify student information");
        tasks.offer("Verify staff information");
        System.out.println("Queue next task: " + tasks.poll());

        Set<String> activityNames = new LinkedHashSet<>();
        activityNames.add("ISA");
        activityNames.add("Midpoint");
        activityNames.add("ISA");
        System.out.println("Set (duplicate ISA removed): " + activityNames);

        Map<String, String> programmes = new LinkedHashMap<>();
        programmes.put("MCA", "Master of Computer Applications");
        programmes.put("MSCAI", "M.Sc. Artificial Intelligence");
        programmes.put("MSCDS", "M.Sc. Data Science");
        System.out.println("Programme Map: " + programmes);

        printHeading("5. ACADEMIC AND NON-ACADEMIC ACTIVITIES");
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("ISA", ActivityType.ACADEMIC, LocalDate.of(2026, 9, 1),
                new ArrayList<>(List.of("Department"))));
        activities.add(new Activity("Midpoint", ActivityType.ACADEMIC, LocalDate.of(2026, 9, 15),
                new ArrayList<>(List.of("Department"))));
        activities.add(new Activity("SEA", ActivityType.ACADEMIC, LocalDate.of(2026, 10, 1),
                new ArrayList<>(List.of("Department"))));
        activities.add(new Activity("Study Tour", ActivityType.ACADEMIC, LocalDate.of(2026, 10, 10),
                new ArrayList<>(List.of("Faculty coordinator"))));
        activities.add(new Activity("Placements", ActivityType.ACADEMIC, LocalDate.of(2026, 11, 1),
                new ArrayList<>(List.of("Placement cell"))));
        activities.add(new Activity("Seminar / Guest Lecture", ActivityType.ACADEMIC,
                LocalDate.of(2026, 11, 5), new ArrayList<>(List.of("Department"))));
        activities.add(new Activity("InfoFest", ActivityType.NON_ACADEMIC, LocalDate.of(2026, 11, 10),
                new ArrayList<>(List.of("Student committee"))));
        activities.add(new Activity("Plateaunia", ActivityType.NON_ACADEMIC, LocalDate.of(2026, 12, 1),
                new ArrayList<>(List.of("Student committee"))));
        activities.add(new Activity("Play-on", ActivityType.NON_ACADEMIC, LocalDate.of(2027, 1, 10),
                new ArrayList<>(List.of("Student committee"))));
        activities.add(new Activity("Sports and Cultural Events", ActivityType.NON_ACADEMIC,
                LocalDate.of(2027, 2, 1), new ArrayList<>(List.of("Student committee"))));
        for (Activity activity : activities) printReport(activity);

        printHeading("6. PROGRAMMES AND SAMPLE SUBJECTS");
        List<Course> courses = List.of(
                new Course("P-01", "MCA", List.of("Object-Oriented Programming",
                        "Database Management Systems", "Operating Systems", "Computer Networks")),
                new Course("P-02", "M.Sc. Artificial Intelligence", List.of(
                        "Artificial Intelligence", "Machine Learning", "Data Mining")),
                new Course("P-03", "M.Sc. Data Science", List.of(
                        "Statistics", "Data Analytics", "Machine Learning"))
        );
        for (Course course : courses) printReport(course);
        System.out.println("Note: subject lists are illustrative, not an official syllabus.");

        printHeading("7. SHALLOW AND DEEP CLONING");
        Activity original = new Activity("InfoFest", ActivityType.NON_ACADEMIC, LocalDate.now(),
                new ArrayList<>(List.of("Original coordinator")));
        Activity shallow = original.clone();
        Activity deep = original.deepClone();
        shallow.getCoordinators().add("Added through shallow clone");
        System.out.println("Original after editing shallow clone: " + original.getCoordinators());
        deep.getCoordinators().add("Added only to deep clone");
        System.out.println("Original after editing deep clone: " + original.getCoordinators());

        printHeading("8. EXCEPTION HANDLING");
        try {
            System.out.print("Enter a name to validate: ");
            String name = input.nextLine();
            validateName(name);
            System.out.println("Valid name: " + name);
        } catch (InvalidRecordException e) {
            System.out.println("Custom exception caught: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Runtime error handled: " + e.getMessage());
        } finally {
            System.out.println("Validation finished (finally block).");
        }

        printHeading("9. REFLECTION AND RTTI");
        Class<?> runtimeClass = student.getClass();
        System.out.println("Runtime class: " + runtimeClass.getName());
        System.out.println("Declared methods:");
        for (Method method : runtimeClass.getDeclaredMethods()) {
            System.out.println("  " + method.getName());
        }

        printHeading("10. FILE I/O AND PERSISTENCE");
        try {
            StringBuilder report = new StringBuilder("DCST Sample Report\n");
            for (Person person : people) {
                report.append(person.generateReport()).append('\n');
            }
            for (Activity activity : activities) {
                report.append(activity.generateReport()).append('\n');
            }
            for (Course course : courses) {
                report.append(course.generateReport()).append('\n');
            }
            Files.writeString(Path.of(DATA_FILE), report.toString());
            System.out.println("Saved report to " + DATA_FILE);
            System.out.println("Reading first lines back from file:");
            try (BufferedReader reader = Files.newBufferedReader(Path.of(DATA_FILE))) {
                for (int i = 0; i < 4; i++) {
                    String line = reader.readLine();
                    if (line == null) break;
                    System.out.println(line);
                }
            }
            ConsoleReporter reporter = new ConsoleReporter("Exported using Exportable interface.");
            reporter.display();
            reporter.exportToFile("interface-export.txt");
        } catch (IOException e) {
            System.out.println("File I/O error: " + e.getMessage());
        }

        printHeading("11. MULTIPLE INTERFACES AND MESSAGE PASSING", 52);
        ConsoleReporter reporter = new ConsoleReporter(
                "ConsoleReporter implements Exportable (Reportable + Displayable).");
        printReport(reporter);
        reporter.display();

        printHeading("END OF DEMONSTRATION");
        System.out.println("Replace illustrative information with verified DCST/GBS details.");
        input.close();
    }
}
