package com.ontariotechu.sofe3980U;

import com.opencsv.*;
import java.io.FileReader;
import java.util.*;

/**
 * Evaluate Single Variable Binary Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};

        String bestPerformance = "";
        double bestAUC = 0.0;

        for (String filePath : filePaths) {
            double auc = processCSV(filePath);

            if (auc > bestAUC) {
                bestAUC = auc;
                bestPerformance = filePath;
            }
        }
        System.out.println("\nThe model with the best performance is: " + bestPerformance);
        System.out.println("Best AUC-ROC: " + bestAUC);
    }

    public static double processCSV(String filePath) {
        FileReader filereader;
        List<String[]> allData;
        try{
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        }
        catch(Exception e){
            System.out.println("Error reading the CSV file: " + filePath);
            return 0.0;
        }

        int TP=0, TN=0, FP=0, FN=0;
        double bce = 0.0;
        int n = allData.size();
        List<Double[]> rocPoints = new ArrayList<>();

        for (String[] row : allData) { 
            int y_true = Integer.parseInt(row[0]);
            float y_predicted = Float.parseFloat(row[1]);

            // Calculate BCE
            if (y_true == 1) {
                bce += Math.log(y_predicted);
            } else {
                bce += Math.log(1 - y_predicted);
            }

            // Calculate Confusion Matrix
            if (y_true == 1 && y_predicted >= 0.5) {
                TP++;
            } else if (y_true == 0 && y_predicted < 0.5) {
                TN++;
            } else if (y_true == 0 && y_predicted >= 0.5) {
                FP++;
            } else if (y_true == 1 && y_predicted < 0.5) {
                FN++;
            }

            // Store points for area-under-curve
            rocPoints.add(new Double[]{(double)y_predicted, (double)y_true});
        }

        // Calculate Binary Cross Entropy
        bce = -bce / n;

        // Calculate Accuracy, Precision, Recall, F1-Score
        double accuracy = (double)(TP + TN) / (TP + TN + FP + FN);
        double precision = (double)TP / (TP + FP);
        double recall = (double)TP / (TP + FN);
        double f1Score = 2 * (precision * recall) / (precision + recall);
        rocPoints.sort((a, b) -> Double.compare(b[0], a[0]));

        // Calculate AUC-ROC using exact trapezoidal rule
        double auc = 0.0;
        double prevFPR = 0.0, prevTPR = 0.0;
        int totalPositive = TP + FN;
        int totalNegative = TN + FP;
        int runningTP = 0, runningFP = 0;

        for (int i = 0; i < rocPoints.size(); i++) {
            Double[] point = rocPoints.get(i);

            if (point[1] == 1.0) {
                runningTP++;
            } else {
                runningFP++;
            }

            double TPR = (double)runningTP / totalPositive;
            double FPR = (double)runningFP / totalNegative;

            if (i > 0) {
                auc += (TPR + prevTPR) * Math.abs(FPR - prevFPR) / 2;
            }
            prevTPR = TPR;
            prevFPR = FPR;
        }

        System.out.println("\nBinary Cross Entropy (BCE): " + String.format("%.5f", bce));
        System.out.println("Confusion Matrix:");
        System.out.println("True Positive: " + TP + ", False Positive: " + FP + ", True Negative: " + TN + ", False Negative: " + FN);
        System.out.println("Accuracy: " + String.format("%.5f", accuracy));
        System.out.println("Precision: " + String.format("%.5f", precision));
        System.out.println("Recall: " + recall);
        System.out.println("F1Score: " + String.format("%.5f", f1Score));
        System.out.println("AUC-ROC: " + String.format("%.5f", auc));
        return auc;
    }
}
