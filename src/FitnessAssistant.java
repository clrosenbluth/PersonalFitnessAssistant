/*A personal trainer assistant tool. This program takes files containing the number of steps walked each day
over a year-long period (in this file there are four, steps1.txt - steps4.txt) and produces a file (stats.txt)
which shows the average steps-per-month of each trainee and the name of the trainee who had the highest number of
steps in each month.*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FitnessAssistant {
    public static void main(String[] args) throws IOException{
        // Create scanner for keyboard
        Scanner keyboard = new Scanner(System.in);

        // Ask for and store trainee names
        String[] names = getNames(keyboard);

        // Create files for each trainee
        List<File> fileList = getFiles(names);

        // Create readers for each trainee
        List<Scanner> readerList = getReaders(fileList);

        // Calculate and store lists of average steps per month (2D array)
        double[][] stepsAvg = getStepsAvg(readerList);

        // Calculate and store average number of steps per month for each trainee
        double[] overallAvg = overallAverage(stepsAvg);

        // Create the file "stats.txt" (not using FileWriter because I don't want to have multiple entries)
        PrintWriter statsFile = new PrintWriter("stats.txt");

        // Add individual stats for each trainee
        addIndStats(statsFile, names, overallAvg);

        // Add comparison stats
        addCompStats(statsFile, names, stepsAvg);

        // Closures
        closeScanners(keyboard, readerList, statsFile);

    }

    private static String[] getNames(Scanner keyboard) {
        // Get number of trainees
        int numTrainees = 0;
        while (true)
        {
            System.out.println("How many trainees are you tracking?");
            String numTraineesStr = keyboard.nextLine();
            try
            {
                numTrainees = Integer.parseInt(numTraineesStr);
            }
            catch(Exception e)
            {
                System.out.println("Invalid input");
                continue;
            }
            break;
        }

        // Get trainee names
        String[] names = new String[numTrainees];

        // Ask for each trainee name
        for(int i = 0; i < numTrainees; i++)
        {
            System.out.println("Enter the name for trainee #" + (i+1));
            names[i] = keyboard.nextLine();
        }

        return names;
    }

    private static List<File> getFiles(String[] names) {
        ArrayList<File> fileList = new ArrayList<>();
        for(int i = 0; i < names.length; i++)
        {
            String fileName = "src/steps" + (i+1) + ".txt";
            File file = new File(fileName);
            fileList.add(file);
        }

        return fileList;
    }

    private static List<Scanner> getReaders(List<File> fileList) throws FileNotFoundException {
        ArrayList<Scanner> readerList = new ArrayList<>();
        for(File file : fileList)
        {
            if(!file.exists())
            {
                System.out.println("File " + file.getName() + " not found");
                System.exit(0);
            }

            Scanner traineeScanner = new Scanner(file);
            readerList.add(traineeScanner);
        }

        return readerList;
    }

    private static double[][] getStepsAvg(List<Scanner> readerList) {
        // Create 2D array with one row per trainee
        // Each row will be returned with 12 columns, one for each month
        double[][] stepsAvg = new double[readerList.size()][];

        // Cycle through each trainee, create an array of monthly averages, and store it
        for(int i = 0; i<readerList.size(); i++)
        {
            stepsAvg[i] = monthlyAverage(readerList.get(i));
        }

        return stepsAvg;
    }

    private static double[] monthlyAverage(Scanner scanner) {
        // Find monthly averages for the given trainee
        double[] average = new double[12];

        // Initializations
        int month = 0;
        int monthLength = 0;

        String inp;
        int inpInt = 0;

        // For loop and switch statement to get the number of days in the month and calculate its average days
        for(int i = 0; i < 12; i++)
        {
            switch(month)
            {
                case 1:
                    monthLength = 28;
                    break;
                case 3:
                case 5:
                case 8:
                case 10:
                    monthLength = 30;
                    break;
                default:
                    monthLength = 31;
            }

            // Make an array of numbers to be averaged
            double[] monthNumbers = new double[monthLength];
            for(int j = 0; j < monthLength; j++)
            {
                inp = scanner.nextLine();
                try
                {
                    inpInt = Integer.parseInt(inp);
                }
                catch(Exception e)
                {
                    System.out.println("Encountered an error in monthly average calculation: " + e.getMessage());
                    System.exit(0);
                }

                monthNumbers[j] = inpInt;
            }

            average[month] = averageMethod(monthNumbers);
            month++;
        }

        return average;
    }

    private static double averageMethod(double[] array) {
        double sum = 0.0;
        for(double number : array)
        {
            sum += number;
        }

        return sum/array.length;
    }

    private static double[] overallAverage(double[][] stepsAvg) {

        // Get the average steps per month value for each trainee
        double[] overallAverages = new double[stepsAvg.length];

        // Iterate through each row of the stepsAvg array, calculate its average, and store it
        for(int i = 0; i < stepsAvg.length; i++)
        {
            overallAverages[i] = averageMethod(stepsAvg[i]);
        }

        return overallAverages;
    }

    private static void addIndStats(PrintWriter statsFile, String[] names, double[] overallAvg) {
        // Add average steps per month of each trainee to file
        for(int i = 0; i < overallAvg.length; i++)
        {
            statsFile.printf("Average steps per month for %s: %.3f%n", names[i], overallAvg[i]);
        }
    }

    private static void addCompStats(PrintWriter statsFile, String[] names, double[][] stepsAvg) {
        // Add "winner" of average steps for each month
        statsFile.println("\nWinners for each month:");

        // Create reference array to hold month names
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};

        // 12 iterations for 12 months
        for(int i = 0; i < 12; i++)
        {
            // Find highest average for each month
            String winnerName = "";
            double winnerNum = 0.0;
            for(int j = 0; j < stepsAvg.length; j++)
            {
                if(stepsAvg[j][i] > winnerNum)
                {
                    winnerNum = stepsAvg[j][i];
                    winnerName = names[j];
                }
            }

            // Add information to stats file
            statsFile.printf("The winner for %s is %s, with an average of " +
                    "%.3f steps.%n", monthNames[i], winnerName, winnerNum);

        }
    }

    private static void closeScanners(Scanner keyboard, List<Scanner> readerList, PrintWriter statsFile) {
        keyboard.close();
        for(Scanner scanner : readerList)
        {
            scanner.close();
        }
        statsFile.close();
    }
}
