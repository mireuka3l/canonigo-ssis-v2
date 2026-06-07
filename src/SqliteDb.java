import java.sql.*;
import java.time.Year;
import java.util.*;

public class SqliteDb {

    private static final String DB_FILE = "students.db";

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static class CollegeError extends Exception { public CollegeError(String m) { super(m); } }
    public static class ProgramError extends Exception { public ProgramError(String m) { super(m); } }
    public static class StudentError  extends Exception { public StudentError(String m)  { super(m); } }

    public static final List<String> VALID_YEARS   = Arrays.asList("1","2","3","4","5");
    public static final List<String> VALID_GENDERS = Arrays.asList("Male","Female","Other");

    public static void initialize() throws SQLException {
        try (Connection conn = connect();
             Statement  st   = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS college (
                    code TEXT PRIMARY KEY,
                    name TEXT NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS program (
                    code    TEXT PRIMARY KEY,
                    name    TEXT NOT NULL,
                    college TEXT NOT NULL REFERENCES college(code)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS student (
                    id           TEXT PRIMARY KEY
                                      CHECK (id GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
                    firstname    TEXT NOT NULL,
                    lastname     TEXT NOT NULL,
                    program_code TEXT NOT NULL REFERENCES program(code),
                    year         INTEGER NOT NULL CHECK (year BETWEEN 1 AND 5),
                    gender       TEXT NOT NULL
                                      CHECK (gender IN ('Male','Female','Other'))
                )
            """);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  COLLEGE
    // ═════════════════════════════════════════════════════════════════════════

    public static List<Map<String,String>> collegeList(
            String sortBy, boolean reverse, int page, int size)
            throws SQLException {

        String order  = reverse ? "DESC" : "ASC";
        String col    = allowedColumn(sortBy, "code", "name");
        String limit  = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql    = "SELECT code, name FROM college ORDER BY " + col + " " + order + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (size > 0) {
                ps.setInt(1, size);
                ps.setInt(2, (page - 1) * size);
            }
            return toList(ps.executeQuery(), "code", "name");
        }
    }

    public static Map<String,String> collegeGet(String code) throws SQLException, CollegeError {
        String sql = "SELECT code, name FROM college WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            List<Map<String,String>> rows = toList(ps.executeQuery(), "code", "name");
            if (rows.isEmpty()) throw new CollegeError("College '" + code + "' not found.");
            return rows.get(0);
        }
    }

    public static List<Map<String,String>> collegeSearch(
            String query, String field, int page, int size)
            throws SQLException {

        String col   = allowedColumn(field, "code", "name");
        String limit = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql   = "SELECT code, name FROM college WHERE " + col + " LIKE ?" + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            if (size > 0) { ps.setInt(2, size); ps.setInt(3, (page - 1) * size); }
            return toList(ps.executeQuery(), "code", "name");
        }
    }

    public static Map<String,String> collegeCreate(String code, String name)
            throws SQLException, CollegeError {

        code = code.trim().toUpperCase();
        name = name.trim();
        if (code.isEmpty() || name.isEmpty())
            throw new CollegeError("College code and name must not be empty.");

        String sql = "INSERT INTO college (code, name) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE"))
                    throw new CollegeError("College '" + code + "' already exists.");
                throw e;
            }
        }
        return Map.of("code", code, "name", name);
    }

    public static Map<String,String> collegeUpdate(String code, String name)
            throws SQLException, CollegeError {

        name = name.trim();
        if (name.isEmpty()) throw new CollegeError("College name must not be empty.");

        String sql = "UPDATE college SET name = ? WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, code.trim().toUpperCase());
            if (ps.executeUpdate() == 0)
                throw new CollegeError("College '" + code + "' not found.");
        }
        return Map.of("code", code.toUpperCase(), "name", name);
    }

    public static void collegeDelete(String code) throws SQLException, CollegeError {
        String sql = "DELETE FROM college WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            try {
                if (ps.executeUpdate() == 0)
                    throw new CollegeError("College '" + code + "' not found.");
            } catch (SQLException e) {
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new CollegeError(
                        "Cannot delete college '" + code + "': programs still reference it. " +
                        "Delete or reassign those programs first.");
                throw e;
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PROGRAM
    // ═════════════════════════════════════════════════════════════════════════

    public static List<Map<String,String>> programList(
            String sortBy, boolean reverse, int page, int size)
            throws SQLException {

        String order = reverse ? "DESC" : "ASC";
        String col   = allowedColumn(sortBy, "code", "name", "college");
        String limit = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql   = "SELECT code, name, college FROM program ORDER BY " + col + " " + order + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (size > 0) { ps.setInt(1, size); ps.setInt(2, (page - 1) * size); }
            return toList(ps.executeQuery(), "code", "name", "college");
        }
    }

    public static Map<String,String> programGet(String code) throws SQLException, ProgramError {
        String sql = "SELECT code, name, college FROM program WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            List<Map<String,String>> rows = toList(ps.executeQuery(), "code", "name", "college");
            if (rows.isEmpty()) throw new ProgramError("Program '" + code + "' not found.");
            return rows.get(0);
        }
    }

    public static List<Map<String,String>> programSearch(
            String query, String field, int page, int size)
            throws SQLException {

        String col   = allowedColumn(field, "code", "name", "college");
        String limit = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql   = "SELECT code, name, college FROM program WHERE " + col + " LIKE ?" + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            if (size > 0) { ps.setInt(2, size); ps.setInt(3, (page - 1) * size); }
            return toList(ps.executeQuery(), "code", "name", "college");
        }
    }

    public static Map<String,String> programCreate(String code, String name, String college)
            throws SQLException, ProgramError {

        code    = code.trim().toUpperCase();
        name    = name.trim();
        college = college.trim().toUpperCase();
        if (code.isEmpty() || name.isEmpty() || college.isEmpty())
            throw new ProgramError("All program fields are required.");

        String sql = "INSERT INTO program (code, name, college) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, college);
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE"))
                    throw new ProgramError("Program '" + code + "' already exists.");
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new ProgramError("College '" + college + "' does not exist. Create it first.");
                throw e;
            }
        }
        return Map.of("code", code, "name", name, "college", college);
    }

    public static Map<String,String> programUpdate(String code, String name, String college)
            throws SQLException, ProgramError {

        List<String> sets   = new ArrayList<>();
        List<String> params = new ArrayList<>();

        if (name != null) {
            if (name.trim().isEmpty()) throw new ProgramError("Program name must not be empty.");
            sets.add("name = ?"); params.add(name.trim());
        }
        if (college != null) {
            college = college.trim().toUpperCase();
            sets.add("college = ?"); params.add(college);
        }
        if (sets.isEmpty()) throw new ProgramError("Nothing to update.");

        String sql = "UPDATE program SET " + String.join(", ", sets) + " WHERE code = ?";
        params.add(code.trim().toUpperCase());

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
            try {
                if (ps.executeUpdate() == 0)
                    throw new ProgramError("Program '" + code + "' not found.");
            } catch (SQLException e) {
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new ProgramError("College '" + college + "' does not exist.");
                throw e;
            }
        }
        return programGet(code);
    }

    public static void programDelete(String code) throws SQLException, ProgramError {
        String sql = "DELETE FROM program WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            try {
                if (ps.executeUpdate() == 0)
                    throw new ProgramError("Program '" + code + "' not found.");
            } catch (SQLException e) {
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new ProgramError(
                        "Cannot delete program '" + code + "': students still enrolled. " +
                        "Delete or reassign those students first.");
                throw e;
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  STUDENT
    // ═════════════════════════════════════════════════════════════════════════

    public static List<Map<String,String>> studentList(
            String sortBy, boolean reverse, int page, int size)
            throws SQLException {

        String order = reverse ? "DESC" : "ASC";
        String col   = allowedColumn(sortBy, "id","firstname","lastname","program_code","year","gender");
        String limit = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql   = "SELECT id, firstname, lastname, program_code, year, gender " +
                       "FROM student ORDER BY " + col + " " + order + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (size > 0) { ps.setInt(1, size); ps.setInt(2, (page - 1) * size); }
            return toList(ps.executeQuery(), "id","firstname","lastname","program_code","year","gender");
        }
    }

    public static Map<String,String> studentGet(String id) throws SQLException, StudentError {
        String sql = "SELECT id, firstname, lastname, program_code, year, gender " +
                     "FROM student WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            List<Map<String,String>> rows =
                toList(ps.executeQuery(), "id","firstname","lastname","program_code","year","gender");
            if (rows.isEmpty()) throw new StudentError("Student '" + id + "' not found.");
            return rows.get(0);
        }
    }

    public static List<Map<String,String>> studentSearch(
            String query, String field, int page, int size)
            throws SQLException {

        String col   = allowedColumn(field, "id","firstname","lastname","program_code","year","gender");
        String limit = (size > 0) ? " LIMIT ? OFFSET ?" : "";
        String sql   = "SELECT id, firstname, lastname, program_code, year, gender " +
                       "FROM student WHERE " + col + " LIKE ?" + limit;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            if (size > 0) { ps.setInt(2, size); ps.setInt(3, (page - 1) * size); }
            return toList(ps.executeQuery(), "id","firstname","lastname","program_code","year","gender");
        }
    }

    public static String nextStudentId() throws SQLException {
        int year = Year.now().getValue();
        String pattern = year + "-%";
        String sql = "SELECT MAX(CAST(SUBSTR(id, 6) AS INTEGER)) FROM student WHERE id LIKE ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ResultSet rs  = ps.executeQuery();
            int       max = rs.getInt(1);
            return String.format("%d-%04d", year, max + 1);
        }
    }

    public static Map<String,String> studentCreateWithId(
            String id, String firstname, String lastname,
            String programCode, String year, String gender)
            throws SQLException, StudentError {

        id          = id.trim();
        firstname   = firstname.trim();
        lastname    = lastname.trim();
        programCode = programCode.trim().toUpperCase();
        year        = year.trim();
        gender      = gender.trim();

        if (id.isEmpty() || firstname.isEmpty() || lastname.isEmpty()
                || programCode.isEmpty() || year.isEmpty() || gender.isEmpty())
            throw new StudentError("All fields are required.");
        if (!id.matches("\\d{4}-\\d{4}"))
            throw new StudentError("ID must be in format YYYY-NNNN.");
        int idYear = Integer.parseInt(id.substring(0, 4));
        if (idYear < 2010 || idYear > Year.now().getValue() + 1)
            throw new StudentError("ID year is out of range.");
        if (!VALID_YEARS.contains(year))
            throw new StudentError("Year must be one of: " + VALID_YEARS);
        if (!VALID_GENDERS.contains(gender))
            throw new StudentError("Gender must be one of: " + VALID_GENDERS);

        String sql = "INSERT INTO student (id, firstname, lastname, program_code, year, gender) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, firstname);
            ps.setString(3, lastname);
            ps.setString(4, programCode);
            ps.setInt   (5, Integer.parseInt(year));
            ps.setString(6, gender);
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE"))
                    throw new StudentError("Student ID '" + id + "' already exists.");
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new StudentError("Program '" + programCode + "' does not exist.");
                throw e;
            }
        }
        return Map.of("id", id, "firstname", firstname, "lastname", lastname,
                      "program_code", programCode, "year", year, "gender", gender);
    }

    public static Map<String,String> studentCreate(
            String firstname, String lastname,
            String programCode, String year, String gender)
            throws SQLException, StudentError {

        return studentCreateWithId(nextStudentId(), firstname, lastname, programCode, year, gender);
    }

    public static Map<String,String> studentUpdate(
            String id, String firstname, String lastname,
            String programCode, String year, String gender)
            throws SQLException, StudentError {

        List<String> sets   = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (firstname != null) {
            if (firstname.trim().isEmpty()) throw new StudentError("firstname must not be empty.");
            sets.add("firstname = ?"); params.add(firstname.trim());
        }
        if (lastname != null) {
            if (lastname.trim().isEmpty()) throw new StudentError("lastname must not be empty.");
            sets.add("lastname = ?"); params.add(lastname.trim());
        }
        if (programCode != null) {
            sets.add("program_code = ?"); params.add(programCode.trim().toUpperCase());
        }
        if (year != null) {
            if (!VALID_YEARS.contains(year.trim()))
                throw new StudentError("Year must be one of: " + VALID_YEARS);
            sets.add("year = ?"); params.add(Integer.parseInt(year.trim()));
        }
        if (gender != null) {
            if (!VALID_GENDERS.contains(gender.trim()))
                throw new StudentError("Gender must be one of: " + VALID_GENDERS);
            sets.add("gender = ?"); params.add(gender.trim());
        }
        if (sets.isEmpty()) throw new StudentError("Nothing to update.");

        String sql = "UPDATE student SET " + String.join(", ", sets) + " WHERE id = ?";
        params.add(id.trim());

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                Object v = params.get(i);
                if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
                else                      ps.setString(i + 1, (String) v);
            }
            try {
                if (ps.executeUpdate() == 0)
                    throw new StudentError("Student '" + id + "' not found.");
            } catch (SQLException e) {
                if (e.getMessage().contains("FOREIGN KEY"))
                    throw new StudentError("Program '" + programCode + "' does not exist.");
                throw e;
            }
        }
        return studentGet(id);
    }

    public static void studentDelete(String id) throws SQLException, StudentError {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            if (ps.executeUpdate() == 0)
                throw new StudentError("Student '" + id + "' not found.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  COUNT helpers
    // ═════════════════════════════════════════════════════════════════════════

    public static int studentCount() throws SQLException {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM student")) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static int programCount() throws SQLException {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM program")) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static int collegeCount() throws SQLException {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM college")) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SEARCH EXPRESSION HELPERS
    //  Each returns a WHERE fragment with exactly 2 bind params: (query, %query%)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns a SQL WHERE fragment for searching students.
     * searchField values: "all", "id", "firstname", "lastname",
     *                     "program_code", "college_code", "year", "gender"
     *
     * The "all" case concatenates every column so a single LIKE covers all fields.
     */
    private static String studentSearchExpr(String searchField) {
        return switch (searchField) {
            case "id"           -> "(? = '' OR s.id LIKE ?)";
            case "lastname"     -> "(? = '' OR s.lastname LIKE ?)";
            case "program_code" -> "(? = '' OR s.program_code LIKE ?)";
            case "college_code" -> "(? = '' OR p.college LIKE ?)";
            case "year"         -> "(? = '' OR CAST(s.year AS TEXT) LIKE ?)";
            case "gender"       -> "(? = '' OR s.gender LIKE ?)";
            // "all": search across every student field in one pass
            case "all"          ->
                "(? = '' OR (s.id || ' ' || s.firstname || ' ' || s.lastname || ' ' || " +
                "s.program_code || ' ' || p.college || ' ' || " +
                "CAST(s.year AS TEXT) || ' ' || s.gender) LIKE ?)";
            default             -> "(? = '' OR s.firstname LIKE ?)"; // default: firstname
        };
    }

    /**
     * Returns a SQL WHERE fragment for searching programs.
     * searchField values: "all", "code", "name", "college"
     */
    private static String programSearchExpr(String searchField) {
        return switch (searchField) {
            case "code"    -> "(? = '' OR code LIKE ?)";
            case "college" -> "(? = '' OR college LIKE ?)";
            // "all": search across code, name, and college at once
            case "all"     -> "(? = '' OR (code || ' ' || name || ' ' || college) LIKE ?)";
            default        -> "(? = '' OR name LIKE ?)"; // default: name
        };
    }

    /**
     * Returns a SQL WHERE fragment for searching colleges.
     * searchField values: "all", "code", "name"
     */
    private static String collegeSearchExpr(String searchField) {
        return switch (searchField) {
            case "code" -> "(? = '' OR code LIKE ?)";
            // "all": search across both code and name at once
            case "all"  -> "(? = '' OR (code || ' ' || name) LIKE ?)";
            default     -> "(? = '' OR name LIKE ?)";  // default: name
        };
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FILTERED COUNT + LIST  (single search bar + searchField)
    // ═════════════════════════════════════════════════════════════════════════

    public static int studentCountFiltered(String nameQuery, String searchField)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM student s " +
                     "JOIN program p ON p.code = s.program_code " +
                     "WHERE " + studentSearchExpr(searchField);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static List<Map<String,String>> studentListFiltered(
            String sortBy, boolean reverse, int page, int size,
            String nameQuery, String searchField) throws SQLException {

        String col = allowedColumn(sortBy,
            "id","firstname","lastname","program_code","college_code","year","gender");
        String order = reverse ? "DESC" : "ASC";

        String orderExpr = switch (col) {
            case "college_code" -> "p.college";
            case "firstname"    -> "s.firstname";
            case "lastname"     -> "s.lastname";
            case "program_code" -> "s.program_code";
            case "year"         -> "s.year";
            case "gender"       -> "s.gender";
            default             -> "s.id";
        };

        String sql =
            "SELECT s.id, s.firstname, s.lastname, s.program_code, " +
            "p.college AS college_code, s.year, s.gender " +
            "FROM student s " +
            "JOIN program p ON p.code = s.program_code " +
            "WHERE " + studentSearchExpr(searchField) + " " +
            "ORDER BY " + orderExpr + " " + order + " " +
            "LIMIT ? OFFSET ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ps.setInt(3, size);
            ps.setInt(4, (page - 1) * size);
            return toList(ps.executeQuery(),
                "id","firstname","lastname","program_code","college_code","year","gender");
        }
    }

    public static int programCountFiltered(String nameQuery, String searchField)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM program WHERE " + programSearchExpr(searchField);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static List<Map<String,String>> programListFiltered(
            String sortBy, boolean reverse, int page, int size,
            String nameQuery, String searchField) throws SQLException {

        String col   = allowedColumn(sortBy, "code", "name", "college");
        String order = reverse ? "DESC" : "ASC";

        String sql =
            "SELECT code, name, college FROM program " +
            "WHERE " + programSearchExpr(searchField) + " " +
            "ORDER BY " + col + " " + order + " " +
            "LIMIT ? OFFSET ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ps.setInt(3, size);
            ps.setInt(4, (page - 1) * size);
            return toList(ps.executeQuery(), "code", "name", "college");
        }
    }

    public static int collegeCountFiltered(String nameQuery, String searchField)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM college WHERE " + collegeSearchExpr(searchField);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static List<Map<String,String>> collegeListFiltered(
            String sortBy, boolean reverse, int page, int size,
            String nameQuery, String searchField) throws SQLException {

        String col   = allowedColumn(sortBy, "code", "name");
        String order = reverse ? "DESC" : "ASC";

        String sql =
            "SELECT code, name FROM college " +
            "WHERE " + collegeSearchExpr(searchField) + " " +
            "ORDER BY " + col + " " + order + " " +
            "LIMIT ? OFFSET ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameQuery);
            ps.setString(2, "%" + nameQuery + "%");
            ps.setInt(3, size);
            ps.setInt(4, (page - 1) * size);
            return toList(ps.executeQuery(), "code", "name");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Utility helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static List<Map<String,String>> toList(ResultSet rs, String... columns)
            throws SQLException {
        List<Map<String,String>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String,String> row = new LinkedHashMap<>();
            for (String col : columns) row.put(col, rs.getString(col));
            list.add(row);
        }
        return list;
    }

    private static String allowedColumn(String input, String... allowed) {
        if (input == null) return allowed[0];
        for (String a : allowed) {
            if (a.equalsIgnoreCase(input)) return a;
        }
        return allowed[0];
    }
}