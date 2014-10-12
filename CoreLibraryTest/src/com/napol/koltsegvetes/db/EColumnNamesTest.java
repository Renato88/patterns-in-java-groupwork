package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class EColumnNamesTest
{

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testSqlname()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqltypeall()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqltype()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqlnameString()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testJavatype()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testTable()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testRef()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testIsDateType()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testToDate()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = "2014-03-20";
        Object obj = TR_DATE.toDate(str);
        assertTrue(obj instanceof Date);
        Date date = (Date) obj;
        System.out.println(EColumnNames.fancyDateFormat.format(date));
        assertEquals(str, df.format(date));
    }

    @Test
    public void testToString()
    {
        String str = "2014-03-20";
        assertEquals(str, TR_DATE.toDisplayString(str));
        assertEquals("2014-03-20", str);
    }

    @Test
    public void testToQuoteString()
    {
        fail("Not yet implemented");
    }

}
