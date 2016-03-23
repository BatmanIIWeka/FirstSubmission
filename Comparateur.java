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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Comparateur {
Classifier A= new J48();
Classifier B= new RandomForest();
Classifier C= new NaiveBayes();
PrintWriter pw;
private ArrayList<Classifier> Tab= new ArrayList<Classifier>();

public void main(String[] args) throws Exception{
	String dataPath = "/home/mounir.boudali/Bureau/data";
	String challengeName = "batman"; 
	
	System.out.println("Getting Started with " + challengeName + "!");
    
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
	
	
	Tab.add(A);
	Tab.add(B);
	Tab.add(C);
	for (int i =0; i< Tab.size();i++){
		Tab.get(i).buildClassifier(trainData);
		
		
		Evaluation validEval = new Evaluation(trainData);
    	validEval.evaluateModel(Tab.get(i), validData);
    	FastVector validPred = validEval.predictions();
    	
    	

    	Evaluation testEval = new Evaluation(trainData);
    	testEval.evaluateModel(Tab.get(i), testData);
    	FastVector testPred = testEval.predictions();
    	 pw = new PrintWriter("ref_valid"+i+".predict", "UTF-8");
    	for (int j = 0; j < validPred.size(); j++) {
			double val = ((NominalPrediction) validPred.elementAt(j)).predicted();
			pw.print(crimeSolved.value((int) val) + "\n");
		}
    	pw.close();
    	
    	
 
    	
    	System.out.println("Files successfully created !");

   }
	}
	
}

