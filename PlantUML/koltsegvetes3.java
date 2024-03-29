@startuml

package CoreLibrary {
    package CoreLibrary.db {
        abstract class AbstractDataStore << AbstractFactory >>
        class AbstractQuery << extends java.util.LinkedList >>
        enum EColumnNames
        enum ETableNames 
    }

    package CoreLibrary.dbdriver {
        interface ISQLCommands
        interface ISQLiteHelper
    }

    package CoreLibrary.net {
        class UpdaterClient << implements java.lang.Runnable >>
        class MainServer << implements java.lang.Runnable >>
        class SocketHandler << implements java.lang.Runnable >>

        MainServer "1" <--> "n" UpdaterClient : send receive news
        MainServer "1" --> "n" SocketHandler : handle client thread
    }
}

class UpdaterClient {
    + putClientNews (AbstractQuery)
    + getServerNews () : AbstractQuery
}

package AndroidApplication {
    package AndroidApplication.ui {
        class MainActivity << (A,DarkSalmon) Activity >> 
        class TrFormActivity << (A,DarkSalmon) Activity >> 
        class TransactionListAdapter << extends android.ArrayAdapter >>
    }

    package AndroidApplication.db {
        class DataStore << (S,#FF7700) Singleton >> 
        class ParcelableQuery << implements android.Parcelable >> 
    }

    package AndroidApplication.dbdriver {
        class MySQLiteHelper << (S,#FF7700) Singleton >> 
        class MySQLiteOpenHelper << extends android.SQLiteOpenHelper >>
    }
}

package DesktopApplication {
    package DesktopApplication.dbdriver {
        class SQLiteDriverJDBC
    }

    package DesktopApplication.ui {
        class Main
    }

    package DesktopApplication.net {
        class NetworkInterface << implements java.lang.Runnable >> << DEPRECATED >>
    }
}

AbstractDataStore *-- ISQLCommands
AbstractDataStore *-- ISQLiteHelper
EColumnNames ..> ETableNames

TransactionListAdapter --* MainActivity
AbstractQuery --* MainActivity
MainActivity o-- DataStore
DataStore --|> AbstractDataStore
ParcelableQuery --|> AbstractQuery
MySQLiteHelper ..|> ISQLiteHelper
ISQLiteHelper "setSqlInterface" o-- ISQLCommands

MySQLiteHelper *-- MySQLiteOpenHelper
MainActivity <--> TrFormActivity : new, modify (ParcelableQuery)

SQLiteDriverJDBC --|> ISQLiteHelper 

''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

abstract class AbstractDataStore {
    Ez az osztaly kezeli az adatbazist, automatikusan 
    megnyitja ezt, letrehozza a tablakat, es iniciali-
    zalja oket
    --
    - {static} DBNAME : String
    - helper : ISQLiteHelper
    - created : boolean
    - sqlInitHelper : ISQLCommands
    --
    # {abstract} getHelperInstance() : ISQLiteHelper
    + onCreate() : void
    + onDestroy() : void
    + onUpgrade() : void
    - sqlcolsdecl(String) : String
    - sqlcreatetable(ETableNames) : String
    + select(EColumnNames[]) : AbstractQuery
    + select(String, EColumnNames[]) : AbstractQuery
    + select(ETableNames, String) : AbstractQuery
    + delete(ETableNames, String) : boolean
    + insert(Map<EColumnNames, Object>) : int
    + insert(ETableNames, Object[]) : boolean
    + insert(EColumnNames[], Object[]) : int
}

class AbstractQuery {
    - {static} serialVersionUID : long
    # types : EColumnNames;
    # lut : HashMap
    + AbstractQuery(List<EColumnNames>)
    + AbstractQuery(EColumnNames[])
    + setTypes(EColumnNames[]) : AbstractQuery
    + getTypes() : EColumnNames;
    + getRecordLength() : int
    + checkRecord(Object[]) : void
    + addRecord(Object[]) : AbstractQuery
    + appendQuery(AbstractQuery) : void
    + getPosition(EColumnNames) : Integer
    + addLast(Object) : void
    + addLast(Object[]) : void
}

enum EColumnNames {
    TR_ID
    TR_DATE
    ...
    --
    + {static} opLess : String
    + {static} opMore : String
    + {static} opEqual : String
    + {static} opLike : String
    + {static} isoDateFormat : SimpleDateFormat
    + {static} fancyDateFormat : SimpleDateFormat
    + {static} simpleDateFormat : SimpleDateFormat
    - sqltype : String
    - javatype : Class
    - table : ETableNames
    - ref : EColumnNames
    --
    + sqlname(String) : String
    + sqlname() : String
    + sqltypeall() : String
    + toSqlString(Object) : String
    + {static} getColumns(ETableNames) : EColumnNames>
    + sqlwhere(Object, String) : String
    + sqltype() : String
    + javatype() : Class<?>
    + isDateType() : boolean
    + isInsertDate() : boolean
    - toQuote(String) : String
    + toDate(String) : Object
    + toDisplayString(Object) : String
    - toString(Object, SimpleDateFormat) : String
    + {static} values() : EColumnNames;
    + {static} valueOf(String) : EColumnNames
    + ref() : EColumnNames
    + table() : ETableNames
}

enum ETableNames {
    ACCOUNTS
    CHARGE_ACCOUNTS
    TRANZACTIONS
    CLUSTERS
    MARKETS
    PRODUCT_INFO
    --
    + {static} COL_INSERT_DATE : String
    + {static} SQL_TYPE_AUTOIDKEY : String
    + {static} SQL_TYPE_8BYTEKEY : String
    + {static} SQL_TYPE_DATE : String
    + {static} SQL_TYPE_INTEGER : String
    - pref : String
    --
    + sqlname() : String
    + sqlname(String) : String
    + getInsertDateColumn() : EColumnNames
    + isSimpleTable() : boolean
    + isNone() : boolean
    + prefix() : String
    + {static} getTable(EColumnNames) : ETableNames
}

interface ISQLCommands {
    + {abstract} initAfterCreate() : boolean
    + {abstract} sqlCreateTableCommands() : String>
    + {abstract} getFilename() : String
    + {abstract} getVersion() : int
}

interface ISQLiteHelper {
    + {abstract} onClose() : void
    + {abstract} setSqlInterface(ISQLCommands) : ISQLiteHelper
    + {abstract} onCreate() : void
    + {abstract} onDestroy() : void
    + {abstract} onUpgrade(int) : void
    + {abstract} execSQL(String, EColumnNames[]) : AbstractQuery
    + {abstract} execSQL(String) : boolean
    + {abstract} lastInsertRowID() : int
    + {abstract} onOpen() : void
}

class MainActivity {
  {static} KEY_ABSQR : String
  {static} REQUEST_NEWTR : int
  {static} REQUEST_UPDATETR : int
  - db : DataStore
  - itemLongClickListener : AdapterView$OnItemLongClickListener
  - ladapter : TransactionListAdapter
  - lw : ListView
  - q : AbstractQuery
  # onActivityResult(int, int, Intent) : void
  # onCreate(Bundle) : void
  + onCreateOptionsMenu(Menu) : boolean
  + onOptionsItemSelected(MenuItem) : boolean
  - insertDummyDate() : void
}

class TrFormActivity {
  - {static} F_DAY : SimpleDateFormat
  - {static} F_MONTH : SimpleDateFormat
  - {static} F_YEAR : SimpleDateFormat
  - {static} locale : Locale
  - caid : Spinner
  - cl : Spinner
  - db : DataStore
  - dp : DatePicker
  - eta : EditText
  - etr : EditText
  - initialCa : String
  - initialCl : String
  - q : ParcelableQuery
  - r : Object;
  # onCreate(Bundle) : void
  - finishAndReturn() : void
  - getDateFromDatePicker() : String
  - getInt(EditText) : int
}

class TransactionListAdapter {
  - {static} TW_PATTERN : String
  - context : Context
  - query : AbstractQuery
  - twIndices : ArrayList
  - twIds : ArrayList
  - resId : int
  + getCount() : int
  + getQuery() : AbstractQuery
  + getView(int, View, ViewGroup) : View
  - getFieldValue(Field) : Object
  - calcTwCount() : void
}
        
class DataStore {
  - {static} INSTANCE : DataStore
  # getHelperInstance() : ISQLiteHelper
  + setContext(Context) : void
  + {static} instance() : DataStore
}

class ParcelableQuery {
  + {static} CREATOR : Parcelable$Creator
  - {static} serialVersionUID : long
  + describeContents() : int
  + writeToParcel(Parcel, int) : void
}

class MySQLiteHelper {
  - {static} INSTANCE : MySQLiteHelper
  - context : Context
  - db : SQLiteDatabase
  - helper : MySQLiteOpenHelper
  - sql : ISQLCommands
  + execSQL(String, EColumnNames[]) : AbstractQuery
  + execSQL(String) : boolean
  + init() : void
  + lastInsertRowID() : int
  + onClose() : void
  + onCreate() : void
  + onDestroy() : void
  + onOpen() : void
  + onUpgrade(int) : void
  + reset() : void
  + setContext(Context) : MySQLiteHelper
  + setSqlInterface(ISQLCommands) : ISQLiteHelper
  + setSqlInterface(ISQLCommands) : MySQLiteHelper
  + {static} instance() : MySQLiteHelper
}

class MySQLiteOpenHelper {
  - sql : ISQLCommands
  + onCreate(SQLiteDatabase) : void
  + onUpgrade(SQLiteDatabase, int, int) : void
}

class SQLiteDriverJDBC {
    - {static} instance : SQLiteDriverJDBC
    - sql : ISQLCommands
    - c : Connection
    --
    + SQLiteDriverJDBC()
    + onCreate() : void
    + setSqlInterface(ISQLCommands) : ISQLiteHelper
    + onDestroy() : void
    + onUpgrade(int) : void
    + execSQL(String, EColumnNames[]) : AbstractQuery
    + execSQL(String) : boolean
    + lastInsertRowID() : int
    + onOpen() : void
    + onClose() : void
    + {static} instance() : ISQLiteHelper
}

class NetworkInterface {
    --
    + NetworkInterface()
    + run() : void
}

@enduml