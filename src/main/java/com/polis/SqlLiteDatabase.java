package ua.edu.ukma.clientserverjava;

public class SqlLiteDatabase extends Database {

    public SqlLiteDatabase(String dbFileName) {
        super("org.sqlite.JDBC", "jdbc:sqlite:" + dbFileName, null, null);
    }

    public static void main(String[] args) {
        System.out.println("----------------SQL LITE -----------------------------");
        Database sqlLite = new SqlLiteDatabase("HelloDB");

        System.out.println("new row: " + sqlLite.insertTestData(new TestRow(null, "SuperMAKAKA")));
        System.out.println("new row: " + sqlLite.insertTestData(new TestRow(null, "NewMAKAKA")));

        System.out.println("all rows");
        sqlLite.getAllData()
            .forEach(System.out::println);
    }
}
