package submission_2;

import java.io.FileReader;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		// Getting data files
    	String ext = ".arff";
    	String startPath = "/home/cyriaque/ProjetInfo/";
    	String trainPath = startPath + "/train/train" + ext;
    	String validPath = startPath + "/valid/valid" + ext;
    	String testPath = startPath + "/test/test" + ext;
    	
    	String challengeName = "Batman II"; 
    	
    	System.out.println("Starting solver for " + challengeName + "...");
        
    	Preprocesseur preproc = new Preprocesseur();
    	
    	// Create instances
    	Instances trainData = new Instances(new FileReader(trainPath));
    	Instances validData = new Instances(new FileReader(validPath));
    	Instances testData = new Instances(new FileReader(testPath));

    	
    	// Set the attribute to predict (the last one) in each dataset
    	int ind = trainData.numAttributes() - 1;
    	trainData.setClassIndex(ind);
    	validData.setClassIndex(ind);
    	testData.setClassIndex(ind);

    	System.out.println("Data loaded.\nStarting evaluating classifiers...");
    	// Get the best classifiers for data
    	ArrayList<Classifier> classifiers = (new Evaluateur()).getClassifiers(
    			trainData, testData, validData, ind);

    	System.out.println("Classifiers evaluation finished.");
    	System.out.println("Starting comparison...");
    	
    	(new Comparateur()).Comp(classifiers);
    	
    	System.out.println("Done solving " + challengeName + ".\nCreating and zipping files...");
    	
    	preproc.saveFiles();
    	
    	System.out.println("\nFiles written and zipped at bat2_pred.zip.");
	}
}
