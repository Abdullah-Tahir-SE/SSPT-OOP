package util;

public class RunMigration {
    public static void main(String[] args) {
        System.out.println("Running manual DB migration...");
        SchemaInitializer.initialize();
        System.out.println("Done.");
    }
}
