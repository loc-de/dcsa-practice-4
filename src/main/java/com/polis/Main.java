package com.polis;

public class Main {

    public static void main(String[] args) {
        Database db = new Database(
                "org.postgresql.Driver",
                "jdbc:postgresql://my_server_ip:my_port/practice4",
                "postgres",
                "my_password");

//        db.insert(new Product(null, "name1", "c1", 7, 13.4));
//        db.insert(new Product(null, "name2", "c2", 5, 8.9));
//        db.insert(new Product(null, "name3", "c1", 6, 21.6));
//        db.insert(new Product(null, "name4", "c1", 10, 2.6));
//        db.insert(new Product(null, "name5", "c2", 8, 34.1));
//        db.insert(new Product(null, "name6", "c3", 9, 6.2));
//        db.insert(new Product(null, "name7", "c3", 11, 18.1));

        Product inserted = db.insert(new Product(null, "for delete", "d c", 5, 1942.19));
        db.update(new Product(inserted.id(), "updated", "sdsdsdc", 1002, 142.91));

        System.out.println("    all rows:");
        db.getAll().forEach(System.out::println);

        db.delete(inserted.id());

        System.out.println("    all rows:");
        db.getAll().forEach(System.out::println);

        System.out.println("    without filters:");
        db.searchProducts(new ProductSearchParams()).forEach(System.out::println);

        System.out.println("    with filters 1:");
        db.searchProducts(new ProductSearchParams(
                11.5, null, null, 9, null, "c1", null, null, null
        )).forEach(System.out::println);

        System.out.println("    with filters 2:");
        db.searchProducts(new ProductSearchParams(
                11.5, null, null, 9, null, "c1", null, 0, 1
        )).forEach(System.out::println);

        System.out.println("    with filters 3:");
        db.searchProducts(new ProductSearchParams(
                11.5, null, null, 9, null, "c1", null, 1, 1
        )).forEach(System.out::println);

        System.out.println("    with filters 4:");
        db.searchProducts(new ProductSearchParams(
                null, null, null, null, null, null, "category", null, null
        )).forEach(System.out::println);
    }

}