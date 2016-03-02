package batman;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.ZeroR;
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
 */
public class GettingStarted {

    public static void main(String[] args) throws Exception {
    	
    	String dataPath = "/Users/isabelleguyon/Documents/Projects/ParisSaclay/Enseignement/Spring2016/M2_AIC/homework/sites/data/";
    	String challengeName = "batman"; 
    	
    	System.out.println("Getting Started with " + challengeName + "!");
        
    	// Path for the 3 datasets
    	String trainPath = dataPath + challengeName + "/train.arff";
    	String validPath = dataPath + challengeName + "/valid.arff";
    	String testPath = dataPath + challengeName + "/test.arff";
   	
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
    	Attribute timeInHospital = trainData.attribute(ind);
 
    	
    	// Define a model and train it (ZeroR always outputs the same label, corresponding to the most abundant class)
    	Classifier model = new ZeroR();
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
			pw.print(timeInHospital.value((int) val) + "\n");
		}
    	pw.close();
    	
    	
    	// Save the predicted values from the test dataset
    	pw = new PrintWriter("ref_test.predict", "UTF-8");
    	for (int i = 0; i < testPred.size(); i++) {
			double val = ((NominalPrediction) testPred.elementAt(i)).predicted();
			pw.print(timeInHospital.value((int) val) + "\n");
		}
    	pw.close();
    	
    	System.out.println("Success!");

   }
	
}