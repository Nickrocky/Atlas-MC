package com.dndcraft.atlas.io.sql;

import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.dndcraft.atlas.InstanceProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @Author Commissar_Voop, Nickrocky
 */

enum SQLType {
    SQLite,MySQL
}

@Accessors(fluent=true)
@FieldDefaults(level= AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class SQLHandler {

    @Getter private Database database;
    @Getter private final SQLType sqlType;
    @Getter private DatabaseOptions databaseOptions;
    @Nullable @Getter private File SQLiteFile;
    @Nullable protected final MySQLDetails mySQLDetails;

    @Nullable protected String SQL_END = null;

    /**
     * SQLHandler is a basic tool makes connecting to SQL based database with ease. SQLHandler uses Aikar DB.
     * @param sqlType Determines what kind of connection SQL is going to be made to a server or a local file (SQLite)
     * @param SQLiteFile This can be nullable this is used then sqlType is set to SQLite. This also should auto create the SQLite File
     * @param mySQLDetails This is only used when sqlType is set MySQL, this is used for connecting to SQL Server Database.
     */
    private SQLHandler(SQLType sqlType,@Nullable File SQLiteFile,@Nullable MySQLDetails mySQLDetails) {
        this.sqlType = sqlType;
        this.SQLiteFile = SQLiteFile;
        this.mySQLDetails = mySQLDetails;
        if (this.sqlType.equals(SQLType.MySQL)){
            this.databaseOptions = DatabaseOptions.builder().mysql(this.mySQLDetails.USERNAME(),
                    this.mySQLDetails.PASSWORD(),
                    this.mySQLDetails.DATABASE(),
                    this.mySQLDetails.HOSTNAME()+":"+this.mySQLDetails.PORT()).build();
            this.database = PooledDatabaseOptions.builder().options(this.databaseOptions).createHikariDatabase();
            return;
        }
        if (this.sqlType.equals(SQLType.SQLite)){

            this.databaseOptions = DatabaseOptions.builder().sqlite(SQLiteFile.getPath()).build();
            this.database = PooledDatabaseOptions.builder().options(this.databaseOptions).createHikariDatabase();
            return;
        }
    }

    /**
     * SQLite Specific Constructor
     * @param SQLiteFile This can be nullable this is used then sqlType is set to SQLite. This also should auto create the SQLite File
     * */
    public SQLHandler(File SQLiteFile){
        this(SQLType.SQLite, SQLiteFile, null);
    }

    /**
     * Super Lazy SQLite Specific Constructor
     * @param desiredSQLiteFileName Name desired for the file name of the SQLite Database
     * */
    public SQLHandler(String desiredSQLiteFileName){
        this(getSQLiteFile(desiredSQLiteFileName));
    }

    /**
     * MySQL Specific Constructor
     * @param mySQLDetails This is only used when sqlType is set MySQL, this is used for connecting to SQL Server Database.
     * */
    public SQLHandler(MySQLDetails mySQLDetails){
        this(SQLType.MySQL, null, mySQLDetails);
    }

    /**
     * Sets Custom SQL end
     * @param SQL_END input for the custom sql end
     */
    public void setCustomEnd(String SQL_END) {
        this.SQL_END = SQL_END;
    }

    /**
     * Clears SQL Custom End
     */
    public void clearCustomEnd(){
        this.SQL_END = null;
    }

    /**
     * Create SQLite File - If your honestly that lazy...
     * */
    public static File getSQLiteFile(String fileName){
        File sqlFile = new File(InstanceProvider.INSTANCE.getDataFolder(), fileName+".db");
        if (!sqlFile.exists()) {
            try {
                sqlFile.createNewFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return sqlFile;
    }

    /**
     * This used for creating databases or tables that need an end
     * For SQLite it always will be ;
     * If there is a custom SQL End present it will use the custom one;
     * But for MySQL (Server) will be ENGINE=InnoDB DEFAULT CHARSET=utf8;
     * @return
     */
    public String end(){
        if (this.SQL_END!=null) return this.SQL_END;
        if (this.sqlType.equals(SQLType.MySQL)) return "ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        if (this.sqlType.equals(SQLType.SQLite)) return ";";
        return null;
    }
}
