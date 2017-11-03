package com.dcits.dao;

import java.util.List;

import com.dcits.bean.ServerInfo;
import com.dcits.mapper.ServerInfoMapper;
import com.dcits.util.DcitsUtil;

/**
 * 记录linuxInfo和weblogicInfo的历史记录<br>
 * <br>type = 0 LinuxInfo
 * <br>type = 1 weblogicInfo
 * <br>type = 2 Tomcat
 * <br>type = 3 Jvm
 * ServerInfo (id integer primary key, host text,port text,username text,password text,mark text,time text,type text)
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170317
 *
 */

public class ServerDao {
	
	/**
	 * 保存服务器信息
	 * @param info
	 * @throws Exception 
	 */
	public int saveServer(ServerInfo info) throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "insert into ServerInfo values(null,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			mapper.execSQL(sql, new Object[]{info.getHost(), info.getPort()
							, info.getUsername(), info.getPassword(), info.getMark()
							, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN)
							, info.getType(), info.getParameters(), info.getTags()});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
	
	/**
	 * 更新
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public int updateServer(ServerInfo info) throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "update ServerInfo set host=?,port=?,username=?,password=?,mark=?,type=?,parameters=?,tags=? where id=?";
		
		try {
			mapper.execSQL(sql, info.getHost(), info.getPort(), info.getUsername(), info.getPassword(), info.getMark()
						, info.getType(), info.getParameters(), info.getTags(),info.getServerId());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return mapper.getEffectRowCount();
	}
	
	/**
	 * 根据id查找信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ServerInfo findById(Integer id) throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "select * from ServerInfo where id=?";
		try {
			mapper.execSQL(sql, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObject();
		
	}
	
	/**
	 * 根据type查找指定的历史记录
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	public List<ServerInfo> findByType (String type) throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "select * from ServerInfo where type=?";
		try {
			mapper.execSQL(sql, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObjectList();
		
	}
	
	/**
	 * 更新最近使用时间
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public int updateTime(Integer id) throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "update ServerInfo set lastUseTime=? where id=?";
		
		try {
			mapper.execSQL(sql, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
		
	}
	
	/**
	 * 查找新添加的服务器信息是否已经在数据库中
	 * @param info
	 * @return
	 * @throws Exception 
	 */
	public Integer checkExistServerInDB(ServerInfo info) throws Exception  {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "select * from ServerInfo where host=? and port=? and username=?";
		
		try {
			mapper.execSQL(sql, info.getHost(), info.getPort(), info.getUsername(), info.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		if (mapper.getQueryCount() > 0) {
			return mapper.getObject().getServerId();
		}
		
		return null;	
	}
	
	/**
	 * 根据id删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int delServer(Integer id) throws Exception  {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "delete from ServerInfo where id=?";
		
		try {
			mapper.execSQL(sql, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
				
	}
	
	public List<ServerInfo> findAll () throws Exception {
		ServerInfoMapper mapper = new ServerInfoMapper();
		String sql = "select * from ServerInfo";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObjectList();
	}
	
	/**
	 * 保存服务器信息之后是否添加进库还是更新时间
	 * @param info
	 * @throws Exception 
	 */
	public void saveServerAfter(ServerInfo info) throws Exception {
		Integer id = checkExistServerInDB(info);
		if (id == null) {
			//添加
			saveServer(info);
		} else {
			//更新
			updateTime(id);
		}
	}
}
