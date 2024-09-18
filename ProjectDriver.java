import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Iterator;
import java.io.IOException;

/************************************************************************************
 * 		Coded by: Rodrigo Peixoto, Andrew Christensen, Eric George, Alec Hayes 		*
 * 		Homework: FinalProject_Spring24							 					*
 *   	Coded on: 04/25/2024 - 04/27/2024							 				*
 *   	Purpose:  Allows for the user to do multiple handling for students including*
 *   	adding,deleting,sorting,searching. 											*
 *   	Also this code allows for the modification of the lec.txt file and search	*
 *   	Through it, modify it and add things to it.									*
 ************************************************************************************/

/*
	THIS IS THE START OF THE CODE PROVIDED TO US FOR THE lec.txt FILE
*/

enum LectureType {
    GRAD, UNDERGRAD;

    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "Graduate";
            default:
                return "Undergraduate";

        }
    }
}

enum LectureMode {
    F2F, MIXED, ONLINE;

    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "F2F";
            case 1:
                return "Mixed";
            default:
                return "Online";
        }
    }
}
//_______________________________________________________________________________

class Lab {
    private String crn;
    private String classroom;

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    @Override
    public String toString() {
        return crn + "," + classroom;
    }

    public Lab(String crn, String classroom) {
        this.crn = crn;
        this.classroom = classroom;
    }

}//end of class Lab

//_______________________________________________________________________________
class Lecture {

    private String crn;
    private String prefix;
    private String lectureName;

    private LectureType lectureType; //Grad or UnderGrad
    private LectureMode lectureMode; //F2F, Mixed or Online
    private String classroom;
    private boolean hasLabs;
    private int creditHours;
    private int hasStudent;

    ArrayList<Lab> labs;

    // _________________

    //Helper method-used in constructors to set up the common fields
    private void LectureCommonInfoSetUp(String crn, String prefix, String lectureName, LectureType lectureType,
            LectureMode lectureMode) {
        this.crn = crn;
        this.prefix = prefix;
        this.lectureName = lectureName;
        this.lectureType = lectureType;
        this.lectureMode = lectureMode;
    }

    // Non-online with Labs
    public Lecture(String crn,
            String prefix,
            String lectureName,
            LectureType lectureType,
            LectureMode lectureMode,
            String classroom,
            boolean hasLabs,
            int creditHours,
            ArrayList<Lab> labs) {

        LectureCommonInfoSetUp(crn, prefix, lectureName, lectureType, lectureMode);
        this.classroom = classroom;
        this.hasLabs = hasLabs;
        this.creditHours = creditHours;
        this.labs = labs;
    }

    // Constructor for Non-online without Labs
    public Lecture(String crn, String prefix, String lectureName, LectureType lectureType, LectureMode lectureMode,
            String classroom, boolean hasLabs, int creditHours) {
        LectureCommonInfoSetUp(crn, prefix, lectureName, lectureType, lectureMode);
        this.classroom = classroom;
        this.hasLabs = hasLabs;
        this.creditHours = creditHours;
    }

    // Constructor for Online Lectures
    public Lecture(String crn, String prefix, String lectureName, LectureType lectureType, LectureMode lectureMode,
            int creditHours) {
        LectureCommonInfoSetUp(crn, prefix, lectureName, lectureType, lectureMode);
        this.creditHours = creditHours;
    }
    //________

    @Override
    public String toString() {
        String lectureAndLabs = crn + "," + prefix + "," + lectureName + "," + lectureType + "," + // change toString of lectureType
                lectureMode + (lectureMode == LectureMode.ONLINE ? "" : "," + classroom) + ","
                + (lectureMode == LectureMode.ONLINE ? "" : (hasLabs ? "Yes" : "No") + ",") + creditHours + "\n";

        if (labs != null) {//printing corresponding labs
            //lectureAndLabs+="\n";
            for (Lab lab : labs)
                lectureAndLabs += lab + "\n";
        }
        return lectureAndLabs;
    }

    /*
    *  END OF THE PROVIDED CODE IN THE LEC.TXT STUFF
    */

    public String getCrn() {
        return crn;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLectureName() {
        return lectureName;
    }

    public LectureType getLectureType() {
        return lectureType;
    }

    public LectureMode getLectureMode() {
        return lectureMode;
    }

    public String getClassroom() {
        return classroom;
    }

    public boolean isHasLabs() {
        return hasLabs;
    }

    public ArrayList<Lab> getLabs() {
        return labs;
    }

    public int getCreditHours() {
        return creditHours;
    }
    
    public int getHasStudent() {
        return hasStudent;
    }
    
    public void setHasStudent(int hasStudent) {
        this.hasStudent = hasStudent;
    }
}

abstract class Student {
    private String id;
    private String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Abstract method to be implemented in subclasses
    abstract public void printInvoice();
}

class underGraduate extends Student {
    private int[] coursesTakenUG;
    private double gpa;
    private boolean resident;

    public underGraduate(String id, String name, int[] coursesTakenUG, double gpa, boolean resident) {
        super(id, name);
        this.coursesTakenUG = coursesTakenUG;
        this.gpa = gpa;
        this.resident = resident;
    }

    public Lecture getLectureByCRN(int crn, ArrayList<Lecture> listOfLectures) {
        for (Lecture lecture : listOfLectures)
            if (lecture.getCrn().equals(Integer.toString(crn)))
                return lecture;

        // If the correct lecture is not found for whatever reason, return null and abort the invoice printing
        return null;
    }

    @Override
    public void printInvoice() {
        final ArrayList<Lecture> listOfLectures = ProjectDriver.getListOfLectures();
        final double creditHrCost = resident ? 120.25 : 240.50;
        final double healthAndIdFees = 35.00;
        double total = 0;
        System.out.println("VALENCE COLLEGE\nORLANDO FL 10101\n--------------------------------------");
        System.out.println("Fee Invoice Prepared for Student:");
        System.out.println(getId() + " - " + getName() + "\t(" + (resident ? "FL resident" : "out-of-state") + ")\n");
        System.out.println("1 Credit Hour = $" + creditHrCost);
        System.out.println("\nCRN\tCR_PREFIX\tCR_HOURS");

        for (int crn : coursesTakenUG) {
            Lecture lecture = getLectureByCRN(crn, listOfLectures);
            if (lecture == null) {
                System.out.println("Class with CRN " + crn + " not found. Aborting invoice printing.");
                return;
            }

            double courseFee = creditHrCost * lecture.getCreditHours();
            System.out.printf("%d \t%s \t%d \t $ %.2f\n", crn, lecture.getPrefix(), lecture.getCreditHours(),
                    courseFee);
            total += courseFee;
        }

        System.out.printf("\n\t\tHealth & id fees $ %.2f\n", healthAndIdFees);
        total += healthAndIdFees;

        if (gpa >= 3.5 && total > 500) {
            double discount = total * 0.25;
            System.out.println("--------------------------------------------");
            System.out.printf("\t\t\t\t $ %.2f\n", total);
            System.out.printf("\t\t\t\t $ -%.2f\n", discount);
            total -= discount;
        }

        System.out.println("                               -------------");
        System.out.printf("\t\tTOTAL PAYMENTS\t $ %.2f\n", total);
    }
}

abstract class GraduateStudent extends Student {
    public GraduateStudent(String id, String name) {
        super(id, name);
    }

    public abstract void printInvoice();
}

class MsStudent extends GraduateStudent {
    private int[] gradCrnsTaken;
    private static final double CREDIT_HOUR_FEE = 300.00;
    private static final double HEALTH_AND_ID_FEES = 35.00;

    // Constructor for ms student
    public MsStudent(String name, String id, int[] gradCrnsTaken) {
        super(name, id);
        this.gradCrnsTaken = gradCrnsTaken;
    }

    public Lecture getLectureByCRN(int crn, ArrayList<Lecture> listOfLectures) {
        for (Lecture lecture : listOfLectures)
            if (lecture.getCrn().equals(Integer.toString(crn)))
                return lecture;

        // If the correct lecture is not found for whatever reason, return null and abort the invoice printing
        return null;
    }

    // Method to print the invoice for ms student
    @Override
    public void printInvoice() {
        final ArrayList<Lecture> listOfLectures = ProjectDriver.getListOfLectures();
        double total = 0;
        System.out.println("VALENCE COLLEGE\nORLANDO FL 10101");
        System.out.println("---------------------------------------------");
        System.out.println("Fee Invoice Prepared for Student:");
        System.out.println(getId() + " - " + getName() + "\n");
        System.out.println("1 Credit Hour = $" + CREDIT_HOUR_FEE);
        System.out.println("\nCRN\tCR_PREFIX\tCR_HOURS");

        for (int crn : gradCrnsTaken) {
            Lecture lecture = getLectureByCRN(crn, listOfLectures);
            if (lecture == null) {
                System.out.println("Class with CRN " + crn + "not found. Aborting invoice printing.");
                return;
            }

            double courseFee = CREDIT_HOUR_FEE * lecture.getCreditHours();
            System.out.printf("%d \t%s \t%d \t $ %.2f\n", crn, lecture.getPrefix(), lecture.getCreditHours(),
                    courseFee);
            total += courseFee;
        }

        System.out.printf("\n\t\tHealth & id fees $ %.2f\n", HEALTH_AND_ID_FEES);
        total += HEALTH_AND_ID_FEES;

        System.out.println("                               --------------");
        System.out.printf("\t\tTOTAL PAYMENTS\t $ %.2f\n", total);
    }
}

class PhdStudent extends GraduateStudent {
    // Data fields
    private int[] LabsSupervised;
    private String advisor;
    private String researchSubject;

    public PhdStudent(String name, String id, String advisor, String researchSubject, int[] LabsSupervised) {
        //LabsSupervised is the labs the phd student supervised for
        super(name, id);
        this.advisor = advisor;
        this.researchSubject = researchSubject;
        this.LabsSupervised = LabsSupervised;
    }

    @Override
    // Prints the invoice for a Phd student
    public void printInvoice() {
        double total = 0; // Initialize total to zero

        System.out.println("VALENCE COLLEGE\nORLANDO FL 10101\n---------------------");
        System.out.println("Fee Invoice Prepared for Student:\n" + getId() + "-" + getName());

        int labs = LabsSupervised.length;

        if (labs >= 2 && labs < 3) {
            System.out.println("RESEARCH\n" + researchSubject + "\t\t$ 700.00");
            System.out.println("\n" + researchSubject + "\t\t$ -350.00");
            total += 700 - 350; // Add to total with deduction
        } else if (labs >= 3) {
            System.out.println("RESEARCH\n" + researchSubject + "\t\t$ -700.00");
            total -= 700; // Deduct from total
        } else {
            System.out.println("RESEARCH\n" + researchSubject + "\t\t$ 700.00");
            total += 700; // Add to total
        }

        // Add health & id fees
        System.out.println("\nHealth & id fees\t$ 35.00");
        total += 35; // Add to total

        // Print total
        System.out.println("                 ----------------");
        System.out.printf("TOTAL PAYMENTS\t\t$%.2f\n", total);
    }
}

public class ProjectDriver {
    private static final String LECTURE_FILE_PATH = "lec.txt";
    private static ArrayList<Student> students = new ArrayList<>(); // Array with all the students
    public static ArrayList<Lecture> listOfLectures = new ArrayList<>(); // Array of all the lectures

    //This method is written to check that all lectures/labs are correctly transferred to an
    //ArrayList of type Lecture
    private static void printClasses(ArrayList<Lecture> listOfLectures) {
        System.out.println("\n_________________________________\n");
        System.out.print("Printing everything from the ArrayList....");
        System.out.println("\n_________________________________\n");

        for (Lecture lec : listOfLectures)
            System.out.print(lec);
    }

    public static ArrayList<Lecture> getListOfLectures() {
        return listOfLectures;
    }

    public static void main(String[] args) throws FileNotFoundException {

        /*
         * THIS IS THE START OF THE CODE PROVIDED TO US IN THE readingLecTxtCode.java DOCUMENT PROVIDED TO US
         */

        Scanner s = new Scanner(new File("lec.txt"));

        String line = "";
        String[] lectureItems;
        Lecture lecture = null;

        boolean skipLine = false;
        boolean oneMorePass = false;

        while (s.hasNextLine() || oneMorePass) {

            if (skipLine == false) {
                line = s.nextLine();
            }

            oneMorePass = false;

            lectureItems = line.split(",");

            // --------------------------------------------------------------------
            if (lectureItems.length > 2) {// It must be F2F, Mixed or Online lecture

                LectureType type; // Grad or UnderGrad
                LectureMode mode; // Online, F2F or Mixed
                ArrayList<Lab> labList = new ArrayList<>();

                type = LectureType.GRAD;
                if (lectureItems[3].compareToIgnoreCase("Graduate") != 0)
                    type = LectureType.UNDERGRAD;

                // ________________________________________
                if (lectureItems[4].compareToIgnoreCase("ONLINE") == 0) {
                    skipLine = false;
                    lecture = new Lecture(lectureItems[0], lectureItems[1], lectureItems[2], type, LectureMode.ONLINE,
                            Integer.parseInt(lectureItems[5]));

                } else {

                    mode = LectureMode.F2F;
                    if (lectureItems[4].compareToIgnoreCase("F2F") != 0)
                        mode = LectureMode.MIXED;

                    boolean hasLabs = true;
                    if (lectureItems[6].compareToIgnoreCase("yes") != 0)
                        hasLabs = false;

                    if (hasLabs) {//Lecture has a lab
                        skipLine = true;

                        String[] labItems;
                        while (s.hasNextLine()) {
                            line = s.nextLine();
                            if (line.length() > 15) {//True if this is not a lab!

                                if (s.hasNextLine() == false) {//reading the last line if any...
                                    oneMorePass = true;
                                }

                                break;
                            }
                            labItems = line.split(",");
                            Lab lab = new Lab(labItems[0], labItems[1]);
                            labList.add(lab);

                        } //end of while
                        lecture = new Lecture(lectureItems[0], lectureItems[1], lectureItems[2], type, mode,
                                lectureItems[5], hasLabs,
                                Integer.parseInt(lectureItems[7]), labList);

                    } else {//Lecture doesn't have a lab
                        skipLine = false;
                        lecture = new Lecture(lectureItems[0], lectureItems[1], lectureItems[2], type, mode,
                                lectureItems[5], hasLabs,
                                Integer.parseInt(lectureItems[7]));

                    }
                }

            }

            listOfLectures.add(lecture);
        } //end of while

        //Calling printClasses to check that loading to the arrayList is correct
        printClasses(listOfLectures);

        /*
         * THIS IS THE END OF THE CODE PROVIDED TO US IN THE readingLecTxtCode.java DOCUMENT PROVIDED TO US
         */

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            int choice = 0;
            boolean isValidChoice = false;

            // Display main menu options
            System.out.println("Main Menu:");
            System.out.println("1. Student Management");
            System.out.println("2. Course Management");
            System.out.println("3. Exit");
            while (!isValidChoice) {
                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice >= 1 && choice <= 3)
                        isValidChoice = true; // Valid choice entered, exit loop
                    else
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                } else {
                    // Consume the invalid input
                    scanner.nextLine();
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            switch (choice) {
                case 1:
                    studentManagementMenu(scanner);
                    break;
                case 2:
                    courseManagementMenu(scanner);
                    break;
                case 3:
                    exit = true;
                    System.out.println("\nTake Care!");
                    System.out.println("Coded By:\nRodrigo Peixoto\nAndrew Christensen\nEric George\nAlec Hayes");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }

        s.close();
        scanner.close();
    }

    private static void courseManagementMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nClass Management:");
            System.out.println("A. Search for a class or lab using the class/lab number");
            System.out.println("B. Delete a class");
            System.out.println("C. Add a lab to a class");
            System.out.println("X. Back to Main Menu");
            System.out.print("Enter your choice: ");

            char choice = scanner.next().charAt(0);
            scanner.nextLine();

            switch (Character.toLowerCase(choice)) {
                case 'a':
                    searchClassByCRN(scanner);
                    break;
                case 'b':
                    deleteClass(scanner);
                    break;
                case 'c':
                    addLabToClass(scanner);
                    break;
                case 'x':
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void deleteClass(Scanner scanner) {
    	System.out.print("Enter the CRN of the class to delete: ");
        String crn = scanner.next();
        Lecture deletedLecture = null;
        for (Iterator<Lecture> iterator = listOfLectures.iterator(); iterator.hasNext();) {
            Lecture lecture = iterator.next();
            if (lecture.getCrn().equals(crn) && lecture.getHasStudent()==0) {
                deletedLecture = lecture;
                iterator.remove();
                System.out.println("Class deleted successfully.");
                break;
            } else if (lecture.getHasStudent()>1) {
                System.out.println("Cant be deleted this class has a student");
            }
        }
        if (deletedLecture != null) {
            updateLectureFile(listOfLectures);
        } else {
            System.out.println("Class with CRN " + crn + " not found.");
        }
    }

    private static void searchClassByCRN(Scanner scanner) {
        System.out.print("Enter the CRN of the class: ");
        String crn = scanner.nextLine();
        for (Lecture lecture : listOfLectures) {
            if (lecture.getCrn().equals(crn)) {
                System.out.println("Class found:");
                System.out.println(lecture.getCrn() + "," + lecture.getPrefix() + "," + lecture.getLectureName());
                return;
            }
            if (lecture.isHasLabs() == true) {
                for (Lab lab : lecture.getLabs()) {
                    if (lab.getCrn().equals(crn)) {
                        System.out.println("Lab for " + lecture.getCrn() + "," + lecture.getPrefix() + ","
                                + lecture.getLectureName());
                        System.out.println("Lab Room " + lab.getClassroom());

                        return;
                    }
                }
            }
        }

        System.out.println("Class with CRN " + crn + " not found.");
    }

    private static void addLabToClass(Scanner scanner) {
        System.out.print("Enter the CRN of the class to add lab: ");
        String crn = scanner.nextLine();
        for (Lecture lecture : listOfLectures) {
            if (lecture.getCrn().equals(crn)) {
                System.out.print("Enter the CRN of the lab: ");
                String labCRN = scanner.nextLine();
                System.out.print("Enter the classroom for the lab: ");
                String classroom = scanner.nextLine();
                try {
                    lecture.getLabs().add(new Lab(labCRN, classroom));
                    System.out.println("Lab added successfully to class with CRN " + crn);
                } catch (NullPointerException e) {
                    System.out.println("Class has no existing labs");
                }

                updateLectureFile(listOfLectures);
                return;
            }
        }
        System.out.println("Class with CRN " + crn + " not found.");
    }

    private static void updateLectureFile(ArrayList<Lecture> listOfLectures) {
        try (FileWriter writer = new FileWriter(LECTURE_FILE_PATH)) {
            for (Lecture lecture : listOfLectures) {
                writer.write(lecture.toString());
            }
            System.out.println("lec.txt file updated successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the lec.txt file: " + e.getMessage());
        }
    }

    private static void studentManagementMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            // Display student management menu options
            System.out.println("\nStudent Management:");
            System.out.println("A. Add Student");
            System.out.println("B. Search Student by ID");
            System.out.println("C. Delete Student");
            System.out.println("D. Print Fee Invoice by ID");
            System.out.println("E. Print All Students Grouped by Type");
            System.out.println("X. Back to Main Menu");
            System.out.print("Enter your choice: ");

            char choice = scanner.next().charAt(0);
            scanner.nextLine();

            switch (Character.toLowerCase(choice)) {
                case 'a':
                    addStudent(scanner);
                    break;
                case 'b':
                    searchStudentByID(scanner);
                    break;
                case 'c':
                    deleteStudent(scanner);
                    break;
                case 'd':
                    printFeeInvoiceByID(scanner);
                    break;
                case 'e':
                    printStudentsGroupedByType();
                    break;
                case 'x':
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void addStudent(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        // Assuming for now the user will specify the type of student
        System.out.print("Enter student type (UG, MS, or PhD): ");
        String type = scanner.nextLine();

        switch (type.toLowerCase()) {
            case "ug":
                System.out.print("Enter student GPA: ");
                double gpa = scanner.nextDouble();
                System.out.print("Enter if student is FL resident(True/False): ");
                boolean flResident = scanner.nextBoolean();
                scanner.nextLine(); // Consume newline character left by nextBoolean()
                // Prompt the user to enter the number of courses taken
                System.out.print("Enter the number of courses taken: ");
                int numCoursesUG = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                int[] coursesTakenUG = new int[numCoursesUG]; // Array to store course IDs

                // Prompt the user to enter the course IDs
                for (int i = 0; i < numCoursesUG; i++) {
                    System.out.printf("Enter course ID %d: ", i + 1);
                    coursesTakenUG[i] = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character

                }

                students.add(new underGraduate(id, name, coursesTakenUG, gpa, flResident));
                break;
            case "ms":
                // Prompt the user to enter the number of courses taken
                System.out.print("Enter the number of courses taken: ");
                int numCoursesMS = scanner.nextInt();
                scanner.nextLine();

                int[] coursesTakenMS = new int[numCoursesMS]; // Array to store course IDs

                // Prompt the user to enter the course IDs
                for (int i = 0; i < numCoursesMS; i++) {
                    System.out.printf("Enter course ID %d: ", i + 1);
                    coursesTakenMS[i] = scanner.nextInt();
                    scanner.nextLine();
                }

                // Create MsStudent object with the courses taken by the user
                students.add(new MsStudent(id, name, coursesTakenMS));
                break;
            case "phd":
                System.out.print("Enter advisor name: ");
                String advisor = scanner.nextLine();
                System.out.print("Enter research subject: ");
                String subject = scanner.nextLine();
                // Prompt the user to enter the number of labs supervised
                System.out.print("Enter the number of labs supervised: ");
                int numLabs = scanner.nextInt();
                scanner.nextLine();

                int[] LabsSupervisedPHD = new int[numLabs]; // Array to store lab IDs
                // Prompt the user to enter the lab IDs
                for (int i = 0; i < numLabs; i++) {
                    System.out.printf("Enter lab ID %d: ", i + 1);
                    LabsSupervisedPHD[i] = scanner.nextInt();
                    scanner.nextLine();
                }
                // Create PhdStudent object with the labs and other stuff taken by the user
                students.add(new PhdStudent(id, name, advisor, subject, LabsSupervisedPHD));
                break;
            default:
                System.out.println("Invalid student type.");
        }
        System.out.println("Student added successfully.");
    }

    private static void searchStudentByID(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        for (Student student : students) {
            if (student.getId().equalsIgnoreCase(id)) { // Use equalsIgnoreCase() to ignore case sensitivity
                System.out.println("Student found: " + student.getName());
                return;
            }
        }
        System.out.println("Student with ID " + id + " not found.");
    }

    private static void deleteStudent(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        for (Student student : students) {
            if (student.getId().equals(id)) {
                students.remove(student);
                System.out.println("Student deleted successfully.");
                return;
            }
        }
        System.out.println("Student with ID " + id + " not found.");
    }

    private static void printFeeInvoiceByID(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        for (Student student : students) {
            if (student.getId().equals(id)) {
                student.printInvoice();
                return;
            }
        }
        System.out.println("Student with ID " + id + " not found.");
    }

    private static void printStudentsGroupedByType() {
        HashMap<String, ArrayList<String>> groupedStudents = new HashMap<>();
        for (Student student : students) {
            String studentType = student.getClass().getSimpleName();
            if (!groupedStudents.containsKey(studentType)) {
                groupedStudents.put(studentType, new ArrayList<>());
            }
            groupedStudents.get(studentType).add(student.getName());
        }

        System.out.println("\nPhD Students\n------------");
        printStudents(groupedStudents.get("PhdStudent"));
        System.out.println("\nMS Students\n------------");
        printStudents(groupedStudents.get("MsStudent"));
        System.out.println("\nUndergraduate Students\n------------");
        printStudents(groupedStudents.get("underGraduate"));
    }

    private static void printStudents(ArrayList<String> students) {
        if (students != null) {
            for (String student : students) {
                System.out.println(student);
            }
        }
    }
}