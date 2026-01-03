package sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Student.Course.CourseData;
import Student.Grade.GradeData;
public class StuMysql {

		 private static final String URL ="jdbc:mysql://146.56.239.8/studentms?useSSL=false&serverTimezone=UTC";
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
	
		    public static boolean queryList(int id,String password) {
		    	String sql="select sid,password from s";
		        try (
		            Connection conn = getConnection();
		            PreparedStatement ps = conn.prepareStatement(sql)
		        ) {
		            ResultSet rs = ps.executeQuery();

		            while (rs.next()) {
		            	int sid=rs.getInt("sid");
		            	String spassword=rs.getString("password");
		                if(sid==id&&spassword.equals(password))return true;
		            }
		        } catch (Exception e) {
		            throw new RuntimeException(e);
		        }
		        return false;
		    }
		    public static String querySname(int id) {
		    	String sql="select sname from s where sid=?";
		    	String sname = null;
		        try (
		            Connection conn = getConnection();
		            PreparedStatement ps = conn.prepareStatement(sql);
		        ) {
	        		ps.setInt(1, id);
		            ResultSet rs = ps.executeQuery();

		            while (rs.next()) {
		            	sname=rs.getString("sname");
		            }
		        } catch (Exception e) {
		            throw new RuntimeException(e);
		        }
		        return sname;
		    }
		    
		    public static List<CourseData> queryCourse() {
		    	List<CourseData> courseList = new ArrayList<>();
		    	String sql="select c.cid,c.cname,t.tid,t.tname from c,ct,t where c.cid=ct.cid and ct.tid=t.tid";
		        try (
		            Connection conn = getConnection();
		            PreparedStatement ps = conn.prepareStatement(sql)
		        ) {
		            ResultSet rs = ps.executeQuery();

		            while (rs.next()) {
		            	int cid=rs.getInt("cid");
		            	String cname=rs.getString("cname");
		            	int tid=rs.getInt("tid");
		            	String tname=rs.getString("tname");
		            	CourseData courseData=new CourseData(cid,cname,tid,tname);
		            	courseList.add(courseData);
		            }
		        } catch (Exception e) {
		            throw new RuntimeException(e);
		        }
		        return courseList;
		    }

            public static List<String> queryTermsBySid(int sid){
                List<String> terms = new ArrayList<>();
                String sql = "select distinct term from sct where sid=? order by term";
                try (
                    Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)
                ) {
                    ps.setInt(1, sid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        terms.add(rs.getString("term"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return terms;
            }

            public static List<GradeData> queryGradesBySidAndTerm(int sid, String term){
                List<GradeData> list = new ArrayList<>();
                String sql = "select c.cid,c.cname,t.tid,t.tname,c.ccredit,sct.grade " +
                             "from sct join c on sct.cid=c.cid " +
                             "join t on sct.tid=t.tid " +
                             "where sct.sid=? and sct.term=?";
                try (
                    Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)
                ) {
                    ps.setInt(1, sid);
                    ps.setString(2, term);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int cid = rs.getInt("cid");
                        String cname = rs.getString("cname");
                        int tid = rs.getInt("tid");
                        String tname = rs.getString("tname");
                        int credit = rs.getInt("ccredit");
                        Double grade = rs.getObject("grade") == null ? null : rs.getDouble("grade");
                        list.add(new GradeData(cid, cname, tid, tname, credit, grade));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return list;
            }


}
