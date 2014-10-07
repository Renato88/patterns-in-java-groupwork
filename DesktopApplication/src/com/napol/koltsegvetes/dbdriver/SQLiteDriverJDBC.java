package com.napol.koltsegvetes.dbdriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.dbinterface.ISQLCommands;
import com.napol.koltsegvetes.dbinterface.ISQLiteHelper;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class SQLiteDriverJDBC implements ISQLiteHelper
{
    private static final SQLiteDriverJDBC instance = new SQLiteDriverJDBC();

    public static ISQLiteHelper instance()
    {
        return instance;
    }

    private ISQLCommands sql;
    private Connection c = null;

    @Override
    public synchronized ISQLiteHelper setSqlInterface(ISQLCommands sql)
    {
        this.sql = sql;
        return this;
    }

    @Override
    public synchronized void onCreate()
    {
        onOpen();

        for (String command : sql.sqlCreateTableCommands())
        {
            try
            {
                Statement s = c.createStatement();
                System.out.println(command);
                s.executeUpdate(command);
                s.close();
                System.out.println("<> Table created");
            }
            catch (SQLException e)
            {
                System.out.println("<> Table already existists!");
            }
        }
    }

    @Override
    public synchronized void onUpgrade(int newVersion)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void onDestroy()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void onOpen()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + sql.getFilename());
        }
        catch (Exception e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("<> Opened database successfully");
    }

    @Override
    public synchronized void onClose()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public AbstractQuery execSQL(String sqlcommand, EColumnNames... cols)
    {
        try
        {
            Statement s = c.createStatement();
            System.out.println(sqlcommand);
            s.executeUpdate(sqlcommand);
            s.close();
            System.out.println("pcz> row inserted");
        }
        catch (SQLException e)
        {
            System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
        }
        return new AbstractQuery().setTypes(cols).addRecord(0);
    }

    @Override
    public void execSQL(String sqlcommand)
    {
        // TODO Auto-generated method stub
    }

}
