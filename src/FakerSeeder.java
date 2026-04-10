import com.github.javafaker.Faker;
import java.util.Random;

public class FakerSeeder {
    public static void main(String[] args) throws Exception {
        SqliteDb.initialize();
        seed5000();
    }

    public static void seed5000() throws Exception {
        Faker faker = new Faker();
        Random rnd = new Random();

        String[][] colleges = {
            {"CSM","College of Science and Mathematics"},
            {"CED","College of Education"},
            {"CASS","College of Arts and Social Sciences"},
            {"CBAA","College of Business Administration and Accountancy"},
            {"COE","College of Engineering"},
            {"CCS","College of Computer Studies"},
            {"CHS","College of Health Sciences"},
            {"COT","College of Technology"}
        };

        String[][] programs = {
            {"BSCS", "BS Computer Science", "CCS"},
            {"BSIT", "BS Information Technology", "CCS"},
            {"BSIS", "BS Information Systems", "CCS"},
            {"BSCE", "BS Civil Engineering", "COE"},
            {"BSEE", "BS Electrical Engineering", "COE"},
            {"BSECE", "BS Electronics Engineering", "COE"},
            {"BSME", "BS Mechanical Engineering", "COE"},
            {"BSChE", "BS Chemical Engineering", "COE"},
            {"BSIE", "BS Industrial Engineering", "COE"},
            {"BSMath", "BS Mathematics", "CSM"},
            {"BSStats", "BS Statistics", "CSM"},
            {"BSChem", "BS Chemistry", "CSM"},
            {"BSPhys", "BS Physics", "CSM"},
            {"BSBio", "BS Biology", "CSM"},
            {"BSMST", "BS Mathematics and Science Teaching", "CSM"},
            {"BEED", "Bachelor of Elementary Education", "CED"},
            {"BSED-Eng", "BSEd Major in English", "CED"},
            {"BSED-Math", "BSEd Major in Mathematics", "CED"},
            {"BSED-Sci", "BSEd Major in Science", "CED"},
            {"BSA", "BS Accountancy", "CBAA"},
            {"BSBA-FM", "BSBA Financial Management", "CBAA"},
            {"BSBA-MM", "BSBA Marketing Management", "CBAA"},
            {"BSBA-HRM", "BSBA Human Resource Management", "CBAA"},
            {"ABEnglish", "AB English", "CASS"},
            {"ABPolSci", "AB Political Science", "CASS"},
            {"ABFil", "AB Filipino", "CASS"},
            {"BSPsych", "BS Psychology", "CASS"},
            {"BSN", "BS Nursing", "CHS"},
            {"BSPharm", "BS Pharmacy", "CHS"},
            {"BIT-ET", "Bachelor of Industrial Technology - Electrical Tech", "COT"}
        };

        // colleges
        for (String[] c : colleges) {
            try { SqliteDb.collegeCreate(c[0], c[1]); } catch (Exception ignored) {}
        }

        // programs
        for (String[] p : programs) {
            try { SqliteDb.programCreate(p[0], p[1], p[2]); } catch (Exception ignored) {}
        }

        // students (exactly 5000 IDs)
        String[] genders = {"Male", "Female", "Other"};
        for (int i = 1; i <= 5000; i++) {
            String id = String.format("2026-%04d", i);
            String fn = faker.name().firstName();
            String ln = faker.name().lastName();
            String prog = programs[rnd.nextInt(programs.length)][0];
            String year = String.valueOf(1 + rnd.nextInt(5));
            String gender = genders[rnd.nextInt(genders.length)];

            try {
                SqliteDb.studentCreateWithId(id, fn, ln, prog, year, gender);
            } catch (Exception ignored) {}
        }

        System.out.println("Done: seeded colleges, 30 programs, 5000 students.");
    }
}