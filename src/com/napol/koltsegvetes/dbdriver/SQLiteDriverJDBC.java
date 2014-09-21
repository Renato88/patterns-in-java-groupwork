package com.napol.koltsegvetes.dbdriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.napol.koltsegvetes.db.ISQLiteHelper;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class SQLiteDriverJDBC implements ISQLiteHelper
{
    private ISQLCommands sql;
    private String dbname;
    private Connection c = null;

    public SQLiteDriverJDBC(String dbname, ISQLCommands sql)
    {
        super();
        this.dbname = dbname;
        this.sql = sql;
    }

    @Override
    public void onCreate()
    {
        onOpen();
        
        try
        {
            Statement s = c.createStatement();
            System.out.println(sql.sqlCreateTableCA());
            s.executeUpdate(sql.sqlCreateTableCA());
            s.close();
            System.out.println("<> Table created");
        }
        catch (SQLException e)
        {
            System.out.println("<> Table already existists!");
        }
        
        try
        {
            Statement s = c.createStatement();
            System.out.println(sql.sqlCreateTableTR());
            s.executeUpdate(sql.sqlCreateTableTR());
            s.close();
            System.out.println("<> Table created");
        }
        catch (SQLException e)
        {
            System.out.println("<> Table already existists!");
        }
    }

    @Override
    public void onUpgrade()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOpen()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbname);
        }
        catch (Exception e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("<> Opened database successfully");
    }

    @Override
    public void onClose()
    {
        // TODO Auto-generated method stub

    }

}
