package submission_one;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.Id3;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Getting Started - a Minimal Working Example
 * 
 * A short code for starting with the dataset and understanding Weka
 * 
 * HOW TO MAKE IT WORK :
 * STEP 1 - Do not forget to add weka.jar in the project librairies
 * STEP 2 - Change trainPath, validPath and testPath to the correct paths
 * STEP 3 - Run the code
 * STEP 4 - Zip the 2 files valid.predict and test.predict in a single archive
 * STEP 5 - Submit the zip in the "Submit/View Results" tab on the challenge website
 *          and see your score in the "Results" tab
 * 
 * @author MicroBES
 * 
 * Modified by Cyriaque Couenon
 * Group : Batman 2
 *
 */
public class BatmanSubOne {

    public static void main(String[] args) throws Exception {
    	
    	String ext = ".arff";
    	String startPath = "/home/cyriaque/ProjetInfo/";
    	String trainPath = startPath + "/train/train" + ext;
    	String validPath = startPath + "/valid/valid" + ext;
    	String testPath = startPath + "/test/test" + ext;
    	String challengeName = "Batman II"; 
    	
    	System.out.println("Getting Started with " + challengeName + "...");
        
    	// Create instances
    	Instances trainData = new Instances(new FileReader(trainPath));
    	Instances validData = new Instances(new FileReader(validPath));
    	Instances testData = new Instances(new FileReader(testPath));

    	
    	// Set the attribute to predict (the last one) in each dataset
    	int ind = trainData.numAttributes() - 1;
    	trainData.setClassIndex(ind);
    	validData.setClassIndex(ind);
    	testData.setClassIndex(ind);

    	
    	// Extract the attribute to predict 
    	// in order to convert predictions index to prediction label later
    	Attribute crimeSolved = trainData.attribute(ind);
 
    	
    	// Define a model and train it (ZeroR always outputs the same label, corresponding to the most abundant class)
    	Classifier model = new Id3();
    	model.buildClassifier(trainData);
 
    	
    	// Define an Evaluation object to predict and FastVector to store the results for the valid dataset
    	Evaluation validEval = new Evaluation(trainData);
    	validEval.evaluateModel(model, validData);
    	FastVector validPred = validEval.predictions();
    	
    	
    	// The same for the test dataset
    	Evaluation testEval = new Evaluation(trainData);
    	testEval.evaluateModel(model, testData);
    	FastVector testPred = testEval.predictions();
    	
    	
    	// Define a PrintWriter to save predicted value in files
    	// NOTE : submitted files on Codalab must have the name "valid.predict" and "test.predict" 	
    	PrintWriter pw;
    	
    	
    	// Save the predicted values from the valid dataset
    	pw = new PrintWriter("ref_valid.predict", "UTF-8");
    	for (int i = 0; i < validPred.size(); i++) {
			double val = ((NominalPrediction) validPred.elementAt(i)).predicted();
			pw.print(crimeSolved.value((int) val) + "\n");
		}
    	pw.close();
    	
    	
    	// Save the predicted values from the test dataset
    	pw = new PrintWriter("ref_test.predict", "UTF-8");
    	for (int i = 0; i < testPred.size(); i++) {
			double val = ((NominalPrediction) testPred.elementAt(i)).predicted();
			pw.print(crimeSolved.value((int) val) + "\n");
		}
    	pw.close();
    	
    	System.out.println("Files successfully created !");

   }
	
}