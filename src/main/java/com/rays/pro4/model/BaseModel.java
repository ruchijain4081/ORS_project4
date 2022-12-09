package com.rays.pro4.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.rays.pro4.bean.DropdownListBean;
import com.rays.pro4.exception.ApplicationException;
import com.rays.pro4.exception.DatabaseException;
import com.rays.pro4.util.DataUtility;
import com.rays.pro4.util.JDBCDataSource;

/**
 * The Class BaseModel
 * 
 * @author Ruchi Jain
 *
 */

/**
 * @author admin7879
 *
 */
public abstract class BaseModel implements  Serializable,DropdownListBean {

	private static Logger log =Logger.getLogger(BaseModel.class);

	protected long id;
	protected String createdBy;
	protected String modifiedBy;
	protected Timestamp createdDatetime;
	protected Timestamp modifiedDateTime;
	public static Logger getLog() {
		return log;
	}
	/**
	 * @param log
	 */
	public static void setLog(Logger log) {
		BaseModel.log = log;
	}
	/**
	 * @return long
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return string
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return string
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return timestamp
	 */
	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}
	/**
	 * @param createdDatetime
	 */
	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	/**
	 * @return timestamp
	 */
	public Timestamp getModifiedDateTime() {
		return modifiedDateTime;
	}
	/**
	 * @param modifiedDateTime
	 */
	public void setModifiedDateTime(Timestamp modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}
	
	
	
	/**
	 * @param next
	 * @return int
	 */
	public int compareTo(BaseModel next){
		return(int)(id-next.getId());
	}
	
	/**
	 * @return long
	 * @throws DatabaseException
	 */
	public long nextPK()throws DatabaseException{
		log.debug("Model nextPK Started");;
		Connection conn=null;
		long pk=0;
		try{
			conn=JDBCDataSource.getConnection();
			PreparedStatement pstml=conn.prepareStatement("SELECT MAX(ID)FROM"+getTableName());
			ResultSet rs=pstml.executeQuery();
			while(rs.next()){
				pk=rs.getInt(1);
			}
			rs.close();
		}catch(Exception e){
			log.error("Database Exception..",e);
			throw new DatabaseException("Excertion : Exception in getting PK");
			
		}finally{
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model nextPK End");
		return pk+1;
		
	}
	
	/**
	 * @return
	 */
	public abstract String getTableName();
	
	
	
	/**
	 * @throws ApplicationException
	 */
	public void updateCreatesInfo()throws ApplicationException{
		log.debug("Model update Started..."+createdBy);
		
		Connection conn=null;
		
		String sql="UPDATE"+getTableName()+"SET CREATED_BY=?,CREATED_DATETIME=?WHERE ID=?";
		log.debug(sql);
		
		try{
			conn=JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, createdBy);
			pstmt.setTimestamp(2, DataUtility.getCurrentTimestamp());
			pstmt.setLong(3, id);
			
			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		}catch(SQLException e){
			log.error("Database Exception..",e);
			JDBCDataSource.trnRollback(conn);
			throw new ApplicationException(e.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model update End");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void updateModifiedInfo()throws Exception{
		log.debug("Model update Startes");
		Connection conn=null;
		
		
		String sql="UPDATE"+getTableName()+"SET MODIFIED_BY=?,MODIFIED_DATETIME=? WHERE ID=?";
		
		try{
			conn=JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, modifiedBy);
			pstmt.setTimestamp(2,DataUtility.getCurrentTimestamp());
			pstmt.setLong(3, id);
			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		}catch(SQLException e){
			log.error("Database Exception...",e);
			JDBCDataSource.trnRollback(conn);
			
		}finally{
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model update End");
		
	}
	
	
	/**
	 * @param <T>
	 * @param model
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected <T extends BaseModel> T populateModel(T model,ResultSet rs)throws SQLException{
		model.setId(rs.getLong("ID"));
		model.setCreatedBy(rs.getString("CREATED_BY"));
		model.setModifiedBy(rs.getString("MODIFIED_BY"));
		
		model.setCreatedDatetime(rs.getTimestamp("CREATED_DATETIME"));
		
		model.setModifiedDateTime(rs.getTimestamp("MODIFIED_DATETIME"));
		
		return model;
	}
	
}
