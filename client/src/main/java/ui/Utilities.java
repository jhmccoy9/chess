package ui;

import java.util.Collection;
import java.util.Scanner;

/*
 * This class contains common helper functions to be used frequently in the other UI classes
 */
public class Utilities
{
    public static String getInput(Collection<String> validEntries)
    {
        System.out.printf(">>> ");
        Scanner scanner = new Scanner(System.in);
        StringBuilder entry = new StringBuilder(scanner.nextLine());

        // if you don't pass a collection of valid entries, it just takes that isn't null
        if (validEntries != null)
            while (!validEntries.contains(entry.toString()))
            {
                System.out.println("Invalid Entry. Options:");
                for (String option : validEntries)
                    System.out.println(option);
                System.out.printf(">>> ");
                entry = new StringBuilder(scanner.nextLine());
            }
        while (entry.toString().isEmpty())
        {
            System.out.println("Invalid Entry. Try again.");
            System.out.printf(">>> ");
            entry = new StringBuilder(scanner.nextLine());
        }
        return entry.toString();
    }



}
