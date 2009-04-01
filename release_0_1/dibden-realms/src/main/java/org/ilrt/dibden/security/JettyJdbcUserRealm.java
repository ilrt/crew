package org.ilrt.dibden.security;

import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.Request;
import org.mortbay.util.Loader;
import org.mortbay.resource.Resource;
import org.mortbay.log.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Properties;
import java.security.Principal;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: JettyJdbcUserRealm.java 11 2008-06-04 10:41:37Z cmmaj $
 *
 **/
public class JettyJdbcUserRealm extends HashUserRealm implements UserRealm {

    public JettyJdbcUserRealm() {
        super();
    }

    public JettyJdbcUserRealm(String name) {
        super(name);
    }

    public JettyJdbcUserRealm(String name, String config) throws IllegalAccessException,
            IOException, InstantiationException, ClassNotFoundException {
        super(name);
        setConfig(config);
        Loader.loadClass(this.getClass(), _jdbcDriver).newInstance();
        connectDatabase();
    }


    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getConfig() {
        return super.getConfig();
    }


    public void setConfig(String config) throws IOException {
        super.setConfig(config);
        Properties properties = new Properties();
        Resource resource = Resource.newResource(config);
        properties.load(resource.getInputStream());

        _jdbcDriver = properties.getProperty("jdbcdriver");
        _url = properties.getProperty("url");
        _userName = properties.getProperty("username");
        _password = properties.getProperty("password");
        _userTable = properties.getProperty("usertable");
        _userTableKey = properties.getProperty("usertablekey");
        _userTableUserField = properties.getProperty("usertableuserfield");
        _userTablePasswordField = properties.getProperty("usertablepasswordfield");
        _roleTable = properties.getProperty("roletable");
        _roleTableKey = properties.getProperty("roletablekey");
        _roleTableRoleField = properties.getProperty("roletablerolefield");
        _userRoleTable = properties.getProperty("userroletable");
        _userRoleTableUserKey = properties.getProperty("userroletableuserkey");
        _userRoleTableRoleKey = properties.getProperty("userroletablerolekey");
        _cacheTime = new Integer(properties.getProperty("cachetime"));

        if (_jdbcDriver == null || _jdbcDriver.equals("")
                || _url == null || _url.equals("")
                || _userName == null || _userName.equals("")
                || _password == null
                || _cacheTime < 0) {
            if (Log.isDebugEnabled()) Log.debug("UserRealm " + getName()
                    + " has not been properly configured");
        }
        _cacheTime *= 1000;
        _lastHashPurge = 0;
        _userSql = "select " + _userTableKey + ","
                + _userTablePasswordField + " from "
                + _userTable + " where "
                + _userTableUserField + " = ?";
        _roleSql = "select r." + _roleTableRoleField
                + " from " + _roleTable + " r, "
                + _userRoleTable + " u where u."
                + _userRoleTableUserKey + " = ?"
                + " and r." + _roleTableKey + " = u."
                + _userRoleTableRoleKey;
    }

    public void logout(Principal user) {
    }

    public void connectDatabase() {
        try {
            Class.forName(_jdbcDriver);
            _con = DriverManager.getConnection(_url, _userName, _password);
        }
        catch (SQLException e) {
            Log.warn("UserRealm " + getName()
                    + " could not connect to database; will try later", e);
        }
        catch (ClassNotFoundException e) {
            Log.warn("UserRealm " + getName()
                    + " could not connect to database; will try later", e);
        }
    }

    /* ------------------------------------------------------------ */
    public Principal authenticate(String username,
                                  Object credentials,
                                  Request request) {
        synchronized (this) {

            long now = System.currentTimeMillis();
            if (now - _lastHashPurge > _cacheTime || _cacheTime == 0) {
                _users.clear();
                _roles.clear();
                _lastHashPurge = now;
            }
            Principal user = super.getPrincipal(username);
            if (user == null) {
                loadUser(username);
                user = super.getPrincipal(username);
            }
        }
        return super.authenticate(username, credentials, request);
    }


    /* ------------------------------------------------------------ */
    private void loadUser(String username) {
        try {
            if (null == _con)
                connectDatabase();

            if (null == _con)
                throw new SQLException("Can't connect to database");

            PreparedStatement stat = _con.prepareStatement(_userSql);
            stat.setObject(1, username);
            ResultSet rs = stat.executeQuery();

            if (rs.next()) {
                String user = rs.getString(_userTableKey);
                put(username, "MD5:" + rs.getString(_userTablePasswordField));
                stat.close();

                stat = _con.prepareStatement(_roleSql);
                stat.setString(1, user);
                rs = stat.executeQuery();

                while (rs.next())
                    addUserToRole(username, rs.getString(_roleTableRoleField));

                stat.close();
            }
        }
        catch (SQLException e) {
            Log.warn("UserRealm " + getName()
                    + " could not load user information from database", e);
            connectDatabase();
        }
    }

    private String _jdbcDriver;
    private String _url;
    private String _userName;
    private String _password;
    private String _userTable;
    private String _userTableKey;
    private String _userTableUserField;
    private String _userTablePasswordField;
    private String _roleTable;
    private String _roleTableKey;
    private String _roleTableRoleField;
    private String _userRoleTable;
    private String _userRoleTableUserKey;
    private String _userRoleTableRoleKey;
    private int _cacheTime;

    private long _lastHashPurge;
    private Connection _con;
    private String _userSql;
    private String _roleSql;

}
