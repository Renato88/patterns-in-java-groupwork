package com.napol.koltsegvetes.dbdriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;

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
                System.out.println("pcz> Catched exception: ");
                e.printStackTrace();
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
            ResultSet r = s.executeQuery(sqlcommand);
            AbstractQuery q = new AbstractQuery(cols);
            System.out.println("cols.length = " + cols.length);
            while (r.next())
            {
                Object[] record = new Object[cols.length];
                System.out.println("record.length = " + record.length);
                for (int i = 0; i < cols.length; ++i)
                    record[i] = r.getObject(cols[i].sqlname());
                q.addRecord(record);
            }
            s.close();
            
            return q;
        }
        catch (SQLException e)
        {
            System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
        }
        return null;
    }

    @Override
    public boolean execSQL(String sqlcommand)
    {
        try
        {
            Statement s = c.createStatement();
            s.executeUpdate(sqlcommand);
            s.close();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
            new SQLException().printStackTrace();
            // e.printStackTrace();
        }
        return false;
    }

    @Override
    public int lastInsertRowID()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
