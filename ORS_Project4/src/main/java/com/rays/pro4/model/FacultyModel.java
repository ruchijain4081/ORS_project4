package com.rays.pro4.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rays.pro4.bean.CollegeBean;
import com.rays.pro4.bean.CourseBean;
import com.rays.pro4.bean.FacultyBean;
import com.rays.pro4.bean.SubjectBean;
import com.rays.pro4.exception.ApplicationException;
import com.rays.pro4.exception.DatabaseException;
import com.rays.pro4.exception.DuplicateRecordException;
import com.rays.pro4.util.JDBCDataSource;

/**
 * JDBC Implementation of FacultyModel.
 * 
 * @author Ruchi Jain
 *
 */
public class FacultyModel {

	private static Logger log = Logger.getLogger(FacultyModel.class);
	/**
	 * create non bussiness primary key
	 * @return integer
	 * @throws DatabaseException
	 */

	private Integer nextPK() throws DatabaseException {
		log.debug("Model nextpk Started");
		Connection conn = null;
		int pk = 0;
		try {

			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(ID) FROM ST_FACULTY");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				pk = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			throw new DatabaseException("Exception : Exception in getting pk");

		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model next pk End");
		return pk = pk + 1;
	}
	/**
	 * add record in database
	 * @param bean
	 * @return long
	 * @throws ApplicationException
	 * @throws DuplicateRecordException
	 */
	public long add(FacultyBean bean) throws ApplicationException, DuplicateRecordException {
		//   log.debug("Model add Started");
		   Connection conn=null;
		   int pk=0;
		   
		   
		   CollegeModel collegeModel = new CollegeModel();	
			CollegeBean collegeBean = collegeModel.findByPK(bean.getCollegeId());
			bean.setCollegeName(collegeBean.getName());
			
			CourseModel courseModel = new CourseModel();	
			CourseBean courseBean = courseModel.FindByPK(bean.getCourseId());
			bean.setCourseName(courseBean.getName());
			
			SubjectModel subjectModel = new SubjectModel();
			SubjectBean subjectBean = subjectModel.FindByPK(bean.getSubjectId());
			bean.setSubjectName(subjectBean.getSubjectName());
			
			FacultyBean beanExist = findByEmailId(bean.getEmailId());
			if (beanExist != null) { 
				  throw new DuplicateRecordException("Email already exists"); 
				  }
		/*
		 * FacultyBean duplicatefacultyName = findByName(bean.getFirstName());
		 * if(duplicatefacultyName != null) { throw new
		 * DuplicateRecordException("course already exist"); }
		 */
			 
		   
		   
		   try {
			   conn = JDBCDataSource.getConnection();
			   pk=nextPK();
			   conn.setAutoCommit(false);
			   System.out.println("666666666666");
			   PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ST_FACULTY VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			   	pstmt.setInt(1, pk);
			   	pstmt.setLong(2, bean.getCollegeId());
			   	pstmt.setLong(3,bean.getSubjectId());
			    pstmt.setLong(4, bean.getCourseId());
			   	pstmt.setString(5, bean.getFirstName());
			    pstmt.setString(6, bean.getLastName());
			    pstmt.setString(7, bean.getGender());
			    pstmt.setDate(8, new java.sql.Date(bean.getDob().getTime()));
			    pstmt.setString(9, bean.getEmailId());
			    pstmt.setString(10, bean.getMobileNo());
			    pstmt.setString(11, bean.getCourseName());
	            pstmt.setString(12, bean.getCollegeName());
			    pstmt.setString(13, bean.getSubjectName());
			    pstmt.setString(14, bean.getCreatedBy());
			    pstmt.setString(15, bean.getModifiedBy());
			    pstmt.setTimestamp(16, bean.getCreatedDatetime());
			    pstmt.setTimestamp(17, bean.getModifiedDatetime());
			   	pstmt.executeUpdate();
			   	conn.commit();
			   	pstmt.close();
			   	conn.close();
		   }catch(Exception e) {
		//	   log.error("Database Exception....",e);
			   try {
				   conn.rollback();
			   }catch(Exception ex) {
				   ex.printStackTrace();
				 //  throw new ApplicationException("Excetion : add rollback Exception " +ex.getMessage());
			   }
			 //  throw new ApplicationException("Exception : Exception in add course" );
		   }finally {
			   JDBCDataSource.closeConnection(conn);
		   }
		//   log.debug("Model add End");
		return pk;
		  
	   }

	/**
	 * delete record in database
	 * @param bean
	 * @throws ApplicationException
	 */

	public void delete(FacultyBean bean) throws ApplicationException {

		log.debug("Model delete Started");
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ST_FACULTY WHERE ID=?");
			pstmt.setLong(1, bean.getId());
			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (Exception e) {
			log.error("Database Exception.." + e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : delete rollback exception  " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model delete End");
	}
	 /**
	 * update record in database
	 * @param bean
	 * @throws ApplicationException
	 * @throws DuplicateRecordException
	 */

	public void update(FacultyBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("model update Started");
		Connection conn = null;

		/*
		 * FacultyBean beanExist = findByName(bean.getFirstName()); if(beanExist !=null
		 * && beanExist.getId() != bean.getId()) { throw new
		 * DuplicateRecordException("Course is alredy Exist"); }
		 */
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement(
					"UPDATE ST_FACULTY SET FIRST_NAME=?,LAST_NAME=?,GENDER=?,EMAIL_ID=?,MOBILE_NO=?,COLLEGE_ID=?,COLLEGE_NAME=?,COURSE_ID=?,COURSE_NAME=?,DOB=?,SUBJECT_ID=?,SUBJECT_NAME=?, CREATED_BY=?,MODIFIED_BY=?,CREATED_DATETIME=?,MODIFIED_DATETIME=? WHERE ID=?");

			pstmt.setString(1, bean.getFirstName());
			pstmt.setString(2, bean.getLastName());
			pstmt.setString(3, bean.getGender());
			pstmt.setString(4, bean.getEmailId());
			pstmt.setString(5, bean.getMobileNo());
			pstmt.setLong(6, bean.getCollegeId());
			pstmt.setString(7, bean.getCollegeName());
			pstmt.setLong(8, bean.getCourseId());
			pstmt.setString(9, bean.getCourseName());
			pstmt.setDate(10, new java.sql.Date(bean.getDob().getTime()));
			pstmt.setLong(11, bean.getSubjectId());
			pstmt.setString(12, bean.getSubjectName());
			pstmt.setString(13, bean.getCreatedBy());
			pstmt.setString(14, bean.getModifiedBy());
			pstmt.setTimestamp(15, bean.getCreatedDatetime());
			pstmt.setTimestamp(16, bean.getModifiedDatetime());
			pstmt.setLong(17, bean.getId());

			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Database Exception..", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : update rollback Exception " + ex.getMessage());
			}
//			 throw new ApplicationException("Exception in updatingcourse" );
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model update End");
	}
	 /**
	 * find record by pk from database
	 * @param pk
	 * @return bean
	 * @throws ApplicationException
	 */
	public FacultyBean findByPK(long pk) throws ApplicationException {
		log.debug("Model findByPK Started");
		StringBuffer sql = new StringBuffer("SELECT * FROM ST_FACULTY WHERE ID=?");
		FacultyBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, pk);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new FacultyBean();
				bean.setId(rs.getLong(1));
				bean.setCollegeId(rs.getLong(2));
				bean.setSubjectId(rs.getLong(3));
				bean.setCourseId(rs.getLong(4));
				bean.setFirstName(rs.getString(5));
				bean.setLastName(rs.getString(6));
				bean.setGender(rs.getString(7));
				bean.setDob(rs.getDate(8));
				bean.setEmailId(rs.getString(9));
				bean.setMobileNo(rs.getString(10));
				bean.setCourseName(rs.getString(11));
				bean.setCollegeName(rs.getString(12));
				bean.setSubjectName(rs.getString(13));
				bean.setCreatedBy(rs.getString(14));
				bean.setModifiedBy(rs.getString(15));
				bean.setCreatedDatetime(rs.getTimestamp(16));
				bean.setModifiedDatetime(rs.getTimestamp(17));
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			throw new ApplicationException("Exception : Exception in getting User by pk");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model findByPK End");
		return bean;
	}
	 /**
		 * find record by emailid from database
		 * @param email
		 * @return bean
		 * @throws ApplicationException
		 */
		
	public FacultyBean findByEmailId(String Email) throws ApplicationException {
		log.debug("Model findBy Email Started");
		StringBuffer sql = new StringBuffer("SELECT * FROM ST_FACULTY WHERE EMAIL_id=?");
		FacultyBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, Email);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new FacultyBean();
				bean.setId(rs.getLong(1));
				bean.setCollegeId(rs.getLong(2));
				bean.setSubjectId(rs.getLong(3));
				bean.setCourseId(rs.getLong(4));
				bean.setFirstName(rs.getString(5));
				bean.setLastName(rs.getString(6));
				bean.setGender(rs.getString(7));
				bean.setDob(rs.getDate(8));
				bean.setEmailId(rs.getString(9));
				bean.setMobileNo(rs.getString(10));
				bean.setCourseName(rs.getString(11));
				bean.setCollegeName(rs.getString(12));
				bean.setSubjectName(rs.getString(13));
				bean.setCreatedBy(rs.getString(14));
				bean.setModifiedBy(rs.getString(15));
				bean.setCreatedDatetime(rs.getTimestamp(16));
				bean.setModifiedDatetime(rs.getTimestamp(17));
	}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			// throw new ApplicationException("Exception : Exception in getting User by
			// Email");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model findBy Email End");
		return bean;
	}
	/**
	 * list of records from database
	 * @return list
	 * @throws ApplicationException
	 */
	
	public List list() throws ApplicationException {
		return list(0, 0);
	}
	/**
	 * list of records from database
	 * @param pageNo
	 * @param pageSize
	 * @return list
	 * @throws ApplicationException
	 */
	public List list(int pageNo, int pageSize) throws ApplicationException {
		log.debug("Model list Started");
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer("select * from ST_FACULTY");

		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + "," + pageSize);

		}

		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				FacultyBean bean = new FacultyBean();
				bean.setId(rs.getLong(1));
				bean.setCollegeId(rs.getLong(2));
				bean.setSubjectId(rs.getLong(3));
				bean.setCourseId(rs.getLong(4));
				bean.setFirstName(rs.getString(5));
				bean.setLastName(rs.getString(6));
				bean.setGender(rs.getString(7));
				bean.setDob(rs.getDate(8));
				bean.setEmailId(rs.getString(9));
				bean.setMobileNo(rs.getString(10));
				bean.setCourseName(rs.getString(11));
				bean.setCollegeName(rs.getString(12));
				bean.setSubjectName(rs.getString(13));
				bean.setCreatedBy(rs.getString(14));
				bean.setModifiedBy(rs.getString(15));
				bean.setCreatedDatetime(rs.getTimestamp(16));
				bean.setModifiedDatetime(rs.getTimestamp(17));
	            list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception...", e);
			throw new ApplicationException("Exception : Exception in getting list of faculty");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model list End");
		return list;
	}
	/**
	 * search record from database
	 * @param bean
	 * @return list
	 * @throws ApplicationException
	 */

	public List search(FacultyBean bean) throws ApplicationException {
		return search(bean, 0, 0);
	}
	 /**
		 * search record from database
		 * @param bean
		 * @param pageNo
		 * @param PageSize
		 * @return list
		 * @throws ApplicationException
		 */

	public List search(FacultyBean bean, int pageNo, int pageSize) throws ApplicationException {
		log.debug("model search started");

		StringBuffer sql = new StringBuffer("select * from ST_FACULTY WHERE 1=1");
		if (bean!=null) {
			if (bean.getId()>0) {
				sql.append(" AND id = " + bean.getId());
			}
			if (bean.getCollegeId() > 0) {
				sql.append(" AND college_Id = " + bean.getCollegeId());
			}
			if (bean.getFirstName() != null && bean.getFirstName().trim().length() > 0) {
				sql.append(" AND FIRST_NAME like '" + bean.getFirstName() + "%'");
			}
			if (bean.getLastName() != null && bean.getLastName().trim().length() > 0) {
				sql.append(" AND LAST_NAME like '" + bean.getLastName() + "%'");
			}
			
			if (bean.getEmailId()!=null && bean.getEmailId().length()>0) {
				sql.append(" AND Email_Id like '" + bean.getEmailId() + "%'");
			}
			
			if (bean.getGender()!=null && bean.getGender().length()>0) {
				sql.append(" AND Gender like '" + bean.getGender() + "%'");
			}
	
		
			if (bean.getMobileNo()!=null && bean.getMobileNo().length()>0) {
				sql.append(" AND Mobile_No like '" + bean.getMobileNo() + "%'");
			}
			
			if (bean.getCollegeName()!=null && bean.getCollegeName().length()>0) {
				sql.append(" AND college_Name like '" + bean.getCollegeName() + "%'");
			}
			if (bean.getCourseId() > 0) {
				sql.append(" AND course_Id = " + bean.getCourseId());
			}
			if (bean.getCourseName()!=null && bean.getCourseName().length()>0) {
				sql.append(" AND course_Name like '" + bean.getCourseName() + "%'");
			}
			if (bean.getSubjectId() > 0) {
				sql.append(" AND Subject_Id = " + bean.getSubjectId());
			}
			if (bean.getSubjectName()!=null && bean.getSubjectName().length()>0) {
				sql.append(" AND subject_Name like '" + bean.getSubjectName() + "%'");
			}
		}
		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + "," + pageSize);
		}

		ArrayList list = new ArrayList();
		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			System.out.println(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new FacultyBean();
				bean.setId(rs.getLong(1));
				bean.setCollegeId(rs.getLong(2));
				bean.setSubjectId(rs.getLong(3));
				bean.setCourseId(rs.getLong(4));
				bean.setFirstName(rs.getString(5));
				bean.setLastName(rs.getString(6));
				bean.setGender(rs.getString(7));
				bean.setDob(rs.getDate(8));
				bean.setEmailId(rs.getString(9));
				bean.setMobileNo(rs.getString(10));
				bean.setCourseName(rs.getString(11));
				bean.setCollegeName(rs.getString(12));
				bean.setSubjectName(rs.getString(13));
				bean.setCreatedBy(rs.getString(14));
				bean.setModifiedBy(rs.getString(15));
				bean.setCreatedDatetime(rs.getTimestamp(16));
				bean.setModifiedDatetime(rs.getTimestamp(17));
	            list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception .....", e);
			//throw new ApplicationException("Exception in the search" + e.getMessage());
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model search End ");
		return list;
	}
	
}