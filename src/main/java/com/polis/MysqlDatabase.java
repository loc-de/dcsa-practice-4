package ua.edu.ukma.clientserverjava;

public class MysqlDatabase extends Database {

    public MysqlDatabase(String host, String db, String username, String password) {
        super("com.mysql.cj.jdbc.Driver", "jdbc:mysql://" + host + "/" + db + "?createDatabaseIfNotExist=true", username, password);
    }

    public static void main(String[] args) {
        // https://hub.docker.com/_/mysql
        // docker run --rm -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 mysql:9.3.0

        Database mysql = new MysqlDatabase("localhost:3306", "testdb", "root", "password");

        System.out.println("new row: " + mysql.insertTestData(new TestRow(null, "SuperMAKAKA")));
        System.out.println("new row: " + mysql.insertTestData(new TestRow(null, "NewMAKAKA")));

        System.out.println("all rows");
        mysql.getAllData()
            .forEach(System.out::println);
    }
}
