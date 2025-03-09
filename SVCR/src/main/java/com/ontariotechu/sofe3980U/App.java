package com.ontariotechu.sofe3980U;

import com.opencsv.*;
import java.io.FileReader;
import java.util.List;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App 
{
    public static void main( String[] args )
    {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};

        String model = "";
        double lowestError = Double.MAX_VALUE;
        
        for (String filePath : filePaths) {
            double mse = processCSV(filePath);
            
            // Figure out the model with the lower error
            if (mse < lowestError) {
                lowestError = mse;
                model = filePath;
            }
        }
        
        System.out.println("\nRecommended Model: " + model + " (Error: " + String.format("%.5f", lowestError) + ")");
    }

    private static double processCSV(String filePath) {
        FileReader filereader;
        List<String[]> allData;
        try {
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return Double.MAX_VALUE;
        }
        
        int count = 0;
        double mse = 0;
        double mae = 0;
        double mare = 0;
        double epsilon = 1e-10; // small, positive number
        int n = allData.size();

        if (n == 0) {
            System.out.println("No data in file: " + filePath);
            return Double.MAX_VALUE;
        }

        for (String[] row : allData) { 
            float y_true = Float.parseFloat(row[0]);
            float y_predicted = Float.parseFloat(row[1]);

            double error = y_true - y_predicted;
            mse += error * error;
            mae += Math.abs(error);
            mare += (Math.abs(error) / (Math.abs(y_true) + epsilon)) * 100;

            System.out.print(y_true + "  \t  " + y_predicted); 
            System.out.println(); 
            count++;
            if (count == 10) {
                break;
            }
        }

        // Compute with ALL data
        mse /= n;
        mae /= n;
        mare /= n;

        // Print results
        System.out.println("\nMean Square Error (MSE): " + String.format("%.5f", mse));
        System.out.println("Mean Absolute Error (MAE): " + String.format("%.5f", mae));
        System.out.println("Mean Absolute Relative Error (MARE): " + String.format("%.5f", mare) + " %\n");

        return mse;
    }
}
