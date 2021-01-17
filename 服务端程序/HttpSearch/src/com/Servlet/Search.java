package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.DBTool.*;

public class Search extends HttpServlet{
	public Search() {
		super();
	}
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		  // Put your code here
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			   throws ServletException, IOException {
		this.doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			   throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
	
		String searchType = request.getParameter("type");
		String truckID = request.getParameter("truckID");
		int id = Integer.parseInt(truckID);
		

		try {
			String sql="";
			Connection con=DBUtil.getConnection();
			Statement stmt=con.createStatement();
			if(searchType.equals("T")) {
				sql = "select * from sensor,device where id=deviceid and truckid='"+id+"' and type ='"+searchType+"' order by time";
			}else if(searchType.equals("G")) {
				sql = "select * from gps,device where id=deviceid and truckid='"+id+"' and type ='"+searchType+"' order by time2";
			}
			ResultSet rs=stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			JSONArray array = new JSONArray();
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				for(int i = 1;i<=columnCount;i++) {
					String columnName = metaData.getColumnLabel(i);  
					String value =rs.getString(columnName);  
					jsonObj.put(columnName, value);  
				}  
		           	array.put(jsonObj); 
			}
			//String jsonString = JsonTools.createJsonString("temperature",array);
			out.write(array.toString());
			con.close();
		}catch(Exception ex)
        {
        	ex.printStackTrace();
        	out.println("异常");
        }
		finally
        {
        	DBUtil.Close();
        	//out.print(type);
        	out.flush();
        	out.close();
        }
		
	}
}
