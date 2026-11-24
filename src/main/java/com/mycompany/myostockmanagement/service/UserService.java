package com.mycompany.myostockmanagement.service;

import com.mycompany.myostockmanagement.dao.UserDAO;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO dao=new UserDAO();
    public List<UserDAO.User> findAll() throws SQLException{return dao.findAll();}
    public void create(String employeeId,String username,String password,String name,String role)throws SQLException{validate(employeeId,username,password,name,role);dao.insert(employeeId,username,password,name,role);}
    public void update(int id,String employeeId,String username,String password,String name,String role)throws SQLException{if(id<=0)throw new IllegalArgumentException("Select a user first.");validate(employeeId,username,password,name,role);dao.update(id,employeeId,username,password,name,role);}
    public void delete(int id)throws SQLException{if(id<=0)throw new IllegalArgumentException("Select a user first.");dao.delete(id);}
    private void validate(String e,String u,String p,String n,String r){if(e==null||e.isBlank()||u==null||u.isBlank()||n==null||n.isBlank()||r==null||r.isBlank())throw new IllegalArgumentException("Employee ID, username, full name, and role are required.");if(p!=null&&!p.isBlank()&&p.length()<4)throw new IllegalArgumentException("Password must be at least 4 characters.");}
}
