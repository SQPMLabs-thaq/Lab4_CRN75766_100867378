package com.ontariotechu.sofe3980U;

import com.opencsv.*;
import java.io.FileReader;
import java.util.List;

/**
 * Multiclass Classification
 */
public class App 
{
    public static void main( String[] args )
    {
        String filePath="model.csv";
        FileReader filereader;
        List<String[]> allData;

        try{
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        }
        catch(Exception e){
            System.out.println( "Error reading the CSV file" );
            return;
        }

        int[][] confusionMatrix = new int[5][5];
        double crossEntropy = 0.0;
        int n = allData.size();

        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]) - 1;

            float[] y_predicted = new float[5];
            for(int i = 0; i < 5; i++) {
                y_predicted[i] = Float.parseFloat(row[i+1]);
            }

            // Add to confusion matrix (predict the class with max probability)
            int y_pred = getMaxIndex(y_predicted);
            confusionMatrix[y_true][y_pred]++;

            // Calculate Cross-Entropy
            crossEntropy += Math.log(y_predicted[y_true]);
        }

        // Final Cross-Entropy calculation
        crossEntropy = -crossEntropy / n;

        // Print Cross-Entropy
        System.out.println("Cross-Entropy: " + String.format("%.5f", crossEntropy));

        // Print Confusion Matrix with headings
        System.out.println("Confusion Matrix:");
        System.out.print("\t\t  Predicted Value\n\t");
        for (int i = 0; i < 5; i++) {
            System.out.print("y'" + (i+1) + "\t");
        }
        System.out.println();

        for (int i = 0; i < 5; i++) {
            System.out.print("y" + (i+1) + "\t");
            for (int j = 0; j < 5; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    // Helper function to find index with max value
    private static int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
