package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Student.Course.CourseData;
import Student.Grade;

public class StuMysql {

	private static final String URL = "jdbc:mysql://146.56.239.8/studentms?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "123";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // 加载驱动
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws Exception {
		return DriverManager.getConnection(URL, USER, PASSWORD);

	}

	public static boolean queryList(int id, String password) {
		String sql = "select sid,password from s";
		try (
				Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int sid = rs.getInt("sid");
				String spassword = rs.getString("password");
				if (sid == id && spassword.equals(password))
					return true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public static String querySname(int id) {
		String sql = "select sname from s where sid=?";
		String sname = null;
		try (
				Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				sname = rs.getString("sname");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sname;
	}

	public static boolean queryTeacherLogin(int id, String password) {
		String sql = "select tid,password from t where tid=? and password=?";
		try (
				Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String queryTname(int id) {
		String sql = "select tname from t where tid=?";
		String tname = null;
		try (
				Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tname = rs.getString("tname");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return tname;
	}

	public static List<CourseData> queryCourse() {
		List<CourseData> courseList = new ArrayList<>();
		String sql = "select c.cid,c.cname,t.tid,t.tname from c,ct,t where c.cid=ct.cid and ct.tid=t.tid";
		try (
				Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int cid = rs.getInt("cid");
				String cname = rs.getString("cname");
				int tid = rs.getInt("tid");
				String tname = rs.getString("tname");
				CourseData courseData = new CourseData(cid, cname, tid, tname);
				courseList.add(courseData);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return courseList;
	}

	public static List<String> queryTerms(int sid) {
		List<String> terms = new ArrayList<>();
		String sql = "SELECT DISTINCT term FROM sct WHERE sid = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				terms.add(rs.getString("term"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return terms;
	}

	public static List<Grade.GradeData> queryGrade(int sid, String term) {
		List<Grade.GradeData> gradeList = new ArrayList<>();
		String sql = "SELECT c.cid, c.cname, t.tid, t.tname, c.ccredit, sct.grade " +
				"FROM sct " +
				"JOIN c ON sct.cid = c.cid " +
				"JOIN t ON sct.tid = t.tid " +
				"WHERE sct.sid = ? AND sct.term = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, sid);
			ps.setString(2, term);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int cid = rs.getInt("cid");
				String cname = rs.getString("cname");
				int tid = rs.getInt("tid");
				String tname = rs.getString("tname");
				int ccredit = rs.getInt("ccredit");
				float grade = rs.getFloat("grade");
				if (rs.wasNull()) {
					grade = -1.0f;
				}
				gradeList.add(new Grade.GradeData(cid, cname, tid, tname, ccredit, grade));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gradeList;
	}

	public static List<CourseData> queryAllC(String cid, String cname, boolean fuzzy, String minCredit,
			String maxCredit) {
		List<CourseData> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT cid, cname, ccredit FROM c WHERE 1=1");
		if (cid != null && !cid.isEmpty()) {
			sql.append(" AND cid = ?");
		}
		if (cname != null && !cname.isEmpty()) {
			if (fuzzy) {
				sql.append(" AND cname LIKE ?");
			} else {
				sql.append(" AND cname = ?");
			}
		}
		if (minCredit != null && !minCredit.isEmpty()) {
			sql.append(" AND ccredit >= ?");
		}
		if (maxCredit != null && !maxCredit.isEmpty()) {
			sql.append(" AND ccredit <= ?");
		}

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			if (cid != null && !cid.isEmpty()) {
				ps.setString(idx++, cid);
			}
			if (cname != null && !cname.isEmpty()) {
				ps.setString(idx++, fuzzy ? "%" + cname + "%" : cname);
			}
			if (minCredit != null && !minCredit.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(minCredit));
			}
			if (maxCredit != null && !maxCredit.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(maxCredit));
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("cid");
				String name = rs.getString("cname");
				int credit = rs.getInt("ccredit");
				list.add(new CourseData(id, name, 0, "", credit));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean openCourse(int tid, int cid, String term) {
		String sql = "INSERT INTO ct (cid, tid, term) VALUES (?, ?, ?)";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cid);
			ps.setInt(2, tid);
			ps.setString(3, term);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<CourseData> queryTeacherCourses(int tid) {
		List<CourseData> list = new ArrayList<>();
		String sql = "SELECT c.cid, c.cname, c.ccredit, ct.term FROM ct JOIN c ON ct.cid = c.cid WHERE ct.tid = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, tid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("cid");
				String name = rs.getString("cname");
				int credit = rs.getInt("ccredit");
				String term = rs.getString("term");
				list.add(new CourseData(id, name, tid, term, credit));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<TeacherGradeData> queryTeacherGrades(int tid, String sid, String sname, boolean sFuzzy,
			String cid, String cname, boolean cFuzzy, String minGrade, String maxGrade, String term) {
		List<TeacherGradeData> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT sct.sid, s.sname, sct.cid, c.cname, sct.grade, sct.term " +
						"FROM sct " +
						"JOIN s ON sct.sid = s.sid " +
						"JOIN c ON sct.cid = c.cid " +
						"WHERE sct.tid = ?");

		if (sid != null && !sid.isEmpty())
			sql.append(" AND sct.sid = ?");
		if (sname != null && !sname.isEmpty())
			sql.append(sFuzzy ? " AND s.sname LIKE ?" : " AND s.sname = ?");
		if (cid != null && !cid.isEmpty())
			sql.append(" AND sct.cid = ?");
		if (cname != null && !cname.isEmpty())
			sql.append(cFuzzy ? " AND c.cname LIKE ?" : " AND c.cname = ?");
		if (minGrade != null && !minGrade.isEmpty())
			sql.append(" AND sct.grade >= ?");
		if (maxGrade != null && !maxGrade.isEmpty())
			sql.append(" AND sct.grade <= ?");
		if (term != null && !term.isEmpty())
			sql.append(" AND sct.term = ?");

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			ps.setInt(idx++, tid);
			if (sid != null && !sid.isEmpty())
				ps.setInt(idx++, Integer.parseInt(sid));
			if (sname != null && !sname.isEmpty())
				ps.setString(idx++, sFuzzy ? "%" + sname + "%" : sname);
			if (cid != null && !cid.isEmpty())
				ps.setInt(idx++, Integer.parseInt(cid));
			if (cname != null && !cname.isEmpty())
				ps.setString(idx++, cFuzzy ? "%" + cname + "%" : cname);
			if (minGrade != null && !minGrade.isEmpty())
				ps.setFloat(idx++, Float.parseFloat(minGrade));
			if (maxGrade != null && !maxGrade.isEmpty())
				ps.setFloat(idx++, Float.parseFloat(maxGrade));
			if (term != null && !term.isEmpty())
				ps.setString(idx++, term);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				float grade = rs.getFloat("grade");
				if (rs.wasNull())
					grade = -1.0f;
				list.add(new TeacherGradeData(
						rs.getInt("cid"),
						rs.getInt("sid"),
						rs.getString("cname"),
						rs.getString("sname"),
						grade,
						rs.getString("term")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean updateGrade(int sid, int cid, int tid, String term, float grade) {
		String sql = "UPDATE sct SET grade = ? WHERE sid = ? AND cid = ? AND tid = ? AND term = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setFloat(1, grade);
			ps.setInt(2, sid);
			ps.setInt(3, cid);
			ps.setInt(4, tid);
			ps.setString(5, term);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static class TeacherGradeData {
		private int cid;
		private int sid;
		private String cname;
		private String sname;
		private float grade;
		private String term;

		public TeacherGradeData(int cid, int sid, String cname, String sname, float grade, String term) {
			this.cid = cid;
			this.sid = sid;
			this.cname = cname;
			this.sname = sname;
			this.grade = grade;
			this.term = term;
		}

		public int getCid() {
			return cid;
		}

		public int getSid() {
			return sid;
		}

		public String getCname() {
			return cname;
		}

		public String getSname() {
			return sname;
		}

		public float getGrade() {
			return grade;
		}

		public String getTerm() {
			return term;
		}

		public String getGradeStr() {
			return grade == -1.0f ? "" : String.valueOf(grade);
		}
	}
}
