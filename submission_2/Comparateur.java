package batman;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.classifiers.evaluation.NominalPrediction;
import java.io.*;
import java.util.*;

public class Comparateur {
	
//static Classifier A= new J48();
//static Classifier B= new RandomForest();
//static Classifier C= new NaiveBayes();

static PrintWriter pw;
static PrintWriter pw1;

static int validSize;
static int testSize;

private static ArrayList<Classifier> Tab= new ArrayList<Classifier>();



public Comparateur(ArrayList<Classifier> classifiers){
	this.Tab = classifiers;
}

public static void Comp() throws Exception{
	String dataPath = "/home/Alexandrina/Documents/batman_arff_data";
	String challengeName = "batman"; 
	
	System.out.println("Classifying data for " + challengeName + "!");
    
	// Path for the 3 datasets
	String trainPath = dataPath + "/train.arff";
	String validPath = dataPath +  "/valid.arff";
	String testPath = dataPath +  "/test.arff";
	
	// Create instances
	Instances trainData = new Instances(new FileReader(trainPath));
	Instances validData = new Instances(new FileReader(validPath));
	Instances testData = new Instances(new FileReader(testPath));
	


	
	// Set the attribute to predict (the last one) in each dataset
	int ind = trainData.numAttributes() - 1;
	trainData.setClassIndex(ind);
	validData.setClassIndex(ind);
	testData.setClassIndex(ind);
	Attribute crimeSolved = trainData.attribute(ind);
	
	
	//Tab.add(A);
	//Tab.add(B);
	//Tab.add(C);

		for (int i = 0; i < Tab.size(); i++) {
			Tab.get(i).buildClassifier(trainData);

			Evaluation validEval = new Evaluation(trainData);
			validEval.evaluateModel(Tab.get(i), validData);
			FastVector validPred = validEval.predictions();

			Evaluation testEval = new Evaluation(trainData);
			testEval.evaluateModel(Tab.get(i), testData);
			FastVector testPred = testEval.predictions();

			//System.out.println(validPred.size() + " " + testPred.size());
			
			validSize = validPred.size();
			testSize = testPred.size();

			pw = new PrintWriter("ref_valid" + i + ".predict", "UTF-8");
			pw1 = new PrintWriter("ref_test" + i + ".predict", "UTF-8");

			for (int j = 0; j < validPred.size(); j++) {
				double val = ((NominalPrediction) validPred.elementAt(j)).predicted();
				pw.print(crimeSolved.value((int) val) + "\n");
			}
			pw.close();

			for (int k = 0; k < testPred.size(); k++) {
				double val = ((NominalPrediction) testPred.elementAt(k)).predicted();
				pw1.print(crimeSolved.value((int) val) + "\n");
			}
			pw1.close();

			System.out.println("Files successfully created !");

		}
		
		System.out.println("File creation successful");
		
		System.out.println("Comparing results...");
		
		// declare the files where the results for each classifier were written 
		
		File f1 = new File("/home/Alexandrina/workspace/BatmanProject/ref_valid0.predict");
		File f2 = new File("/home/Alexandrina/workspace/BatmanProject/ref_valid1.predict");
		File f3 = new File("/home/Alexandrina/workspace/BatmanProject/ref_valid2.predict");
		
		File f4 = new File("/home/Alexandrina/workspace/BatmanProject/ref_test0.predict");
		File f5 = new File("/home/Alexandrina/workspace/BatmanProject/ref_test1.predict");
		File f6 = new File("/home/Alexandrina/workspace/BatmanProject/ref_test2.predict");
		
		// writer for final file 
		PrintWriter writer = new PrintWriter("ref_valid.predict", "UTF-8");


		//valid file readers
		FileReader fR1 = new FileReader(f1);
		FileReader fR2 = new FileReader(f2);
		FileReader fR3 = new FileReader(f3);
		//test file readers
		FileReader fR4 = new FileReader(f4);
		FileReader fR5 = new FileReader(f5);
		FileReader fR6 = new FileReader(f6);
		
		//valid readers
        BufferedReader reader1 = new BufferedReader(fR1);
        BufferedReader reader2 = new BufferedReader(fR2);
        BufferedReader reader3 = new BufferedReader(fR3);
        //test readers
        BufferedReader reader4 = new BufferedReader(fR4);
        BufferedReader reader5 = new BufferedReader(fR5);
        BufferedReader reader6 = new BufferedReader(fR6);
        
        for(int i = 1; i<=validSize; i++){
        	int a1 = Integer.parseInt(reader1.readLine());
        	int a2 = Integer.parseInt(reader2.readLine());
        	int a3 = Integer.parseInt(reader3.readLine());
        	int myInt = (a1 != 0 || a2 !=0 || a3 !=0) ? 1 : 0;
        	writer.println(myInt);
        }
        writer.close();
        
        System.out.println(("Valid comparison complete"));
        
        writer = new PrintWriter("ref_test.predict", "UTF-8");
        
        for(int i = 1; i<=testSize; i++){
        	int a1 = Integer.parseInt(reader4.readLine());
        	int a2 = Integer.parseInt(reader5.readLine());
        	int a3 = Integer.parseInt(reader6.readLine());
        	int myInt = (a1 != 0 || a2 !=0 || a3 !=0) ? 1 : 0;
        	writer.println(myInt);
        }
        writer.close();
        System.out.println("Test comparison complete");
        
        System.out.println("Comparing complete!");        
	}
}
