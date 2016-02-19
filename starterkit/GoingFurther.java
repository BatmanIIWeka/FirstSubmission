import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Going Further - a More Advanced Example
 * 
 * The following code implements the same code than Getting Started but give
 * the possibility to change the classifier and compute the error on training set
 * 
 * HOW TO MAKE IT WORK :
 * STEP 1 - Do not forget to add weka.jar in the project librairies
 * STEP 2 - Change trainPath, validPath and testPath to the correct paths
 * STEP 3 - Change the classifier, you can choose one among the ones proposed in the main method
 * STEP 4 - Run the code
 * STEP 5 - Zip the 2 files valid.predict and test.predict in a single archive
 * STEP 6 - Submit the zip in the "Submit/View Results" tab on the challenge website
 *          and see your score in the "Results" tab
 * 
 * @author MicroBES
 *
 */
public class GoingFurther {

	// Define used methods
	
	/**
	 * Import datasets as Weka Instances
	 * 
	 * @param trainPath - Path of the train dataset
	 * @param validPath - Path of the valid dataset
	 * @param testPath - Path of the test dataset
	 * @return The 3 datasets as Weka Instances objects
	 * @throws Exception raised if one path is incorrect
	 */
	public static Instances[] importData(String trainPath, String validPath, String testPath) throws Exception {
    	
		// Create instances
    	Instances trainData = new Instances(new FileReader(trainPath));
    	Instances validData = new Instances(new FileReader(validPath));
    	Instances testData = new Instances(new FileReader(testPath));

    	// Set the attribute to predict (the last one) in each dataset
    	int ind = trainData.numAttributes() - 1;
    	trainData.setClassIndex(ind);
    	validData.setClassIndex(ind);
    	testData.setClassIndex(ind);
    	
    	return new Instances[]{trainData, validData, testData};
    	
	}
	
	
	
	/**
	 * Predict from a model
	 * 
	 * @param model - The model created from the classifier
	 * @param dataToTrain - The train dataset
	 * @param dataToPredict - The dataset we want to predict
	 * @return A vector containing the prediction of the data we want to predict
	 * @throws Exception - raised if the model has not been correctly fitted
	 */
	public static FastVector predict(Classifier model, Instances dataToTrain, Instances dataToPredict) throws Exception {
		
		// Compute the predictions
    	Evaluation eval = new Evaluation(dataToTrain);
    	eval.evaluateModel(model, dataToPredict);
    	return eval.predictions();
		
	}
	
	
	
	/**
	 * Save the predicted values (stored in a FastVector) in a file
	 * 
	 * @param predictions - The FastVector we want to save
	 * @param filename - The name of file created
	 * @param timeInHospital - The Attribute which allow to convert the prediction indexes to prediction labels
	 * @throws Exception - If the file cannot be opened
	 */
	public static void savePredictions(FastVector predictions, String filename, Attribute timeInHospital) throws Exception {
    	
		// Define a PrintWriter et write the predictions in the file
		PrintWriter pw = new PrintWriter(filename, "UTF-8");
    	for (int i = 0; i < predictions.size(); i++) {
			double val = ((NominalPrediction) predictions.elementAt(i)).predicted();
			pw.print(timeInHospital.value((int) val) + "\n");
		}
    	pw.close();
	}
	
	
	
	public static double getScore(FastVector predictedValues, Instances trueValues, Attribute timeInHospital) {
		int ind = trueValues.numAttributes() - 1;
		double nbPositive = 0.;
		double nbNegative = 0.;
		double nbTruePositive = 0.;
		double nbTrueNegative = 0.;
		
		for (int i = 0; i < trueValues.numInstances(); i++) { 
			
			// Get the true label
			int trueValue = (int) trueValues.instance(i).value(ind);
			int trueLabel = Integer.parseInt(timeInHospital.value(trueValue));
			
			// Get the predicted label
			int predictedValue = (int) ((NominalPrediction) predictedValues.elementAt(i)).predicted();
			int predictedLabel = Integer.parseInt(timeInHospital.value(predictedValue));
			
			// Update counts
			if (trueLabel == 1) {
				nbPositive++;
				if (predictedLabel == 1) nbTruePositive++;
			} else {
				nbNegative++;
				if (predictedLabel == -1) nbTrueNegative++;
			}
			
			
		}
		
		return 1./2. * (nbTruePositive / nbPositive + nbTrueNegative / nbNegative);
		
	}
	
	
	
	/**
	 * The main method
	 * 
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
    	// Path for the 3 datasets
    	String trainPath = "C:/Users/Charles/Desktop/Travail/3A/UE Projet/data/train_set.arff";
    	String validPath = "C:/Users/Charles/Desktop/Travail/3A/UE Projet/data/valid_set.arff";
    	String testPath = "C:/Users/Charles/Desktop/Travail/3A/UE Projet/data/test_set.arff";

    	
    	// Import the datasets in an array containing {train, valid, test}
    	Instances[] data = importData(trainPath, validPath, testPath);
    	
    	
    	// Extract the attribute to predict 
    	// in order to convert predictions index to prediction label later
    	Attribute timeInHospital = data[0].attribute(data[0].numAttributes() - 1);
 
    	
    	/* Define a model
    	 * You can use different ones and different parameters:
    	 *     - J48 : a decision tree)
    	 *     - Naive Bayes
    	 *     - SMO : a SVM, takes lot of memory (more heap space can be needed for the JVM)
    	 *     - RandomForest : takes also lot of memory
    	 *     - AdaBoostM1
    	 * see http://weka.sourceforge.net/doc.dev/weka/classifiers/Classifier.html for a list of classifiers
    	 */
    	//Classifier model = new J48();
    	//Classifier model = new NaiveBayes();
    	//Classifier model = new SMO();
    	//Classifier model = new RandomForest();
    	//Classifier model = new AdaBoostM1();
    	Classifier model = new J48();
    	model.buildClassifier(data[0]);
    	
    	
    	// Compute the score of the predictoin model on the training dataset
    	FastVector trainPred = predict(model, data[0], data[0]);
    	double score = getScore(trainPred, data[0], timeInHospital);
    	System.out.println("Score on the training set : " + score);
    	
    	// Predict for the valid dataset and the test dataset
    	FastVector validPred = predict(model, data[0], data[1]);
    	FastVector testPred = predict(model, data[0], data[2]);
    	
    	
    	// Write the prediction values in files
    	// NOTE : submitted files on Codalab must have the name "valid.predict" and "test.predict"
    	savePredictions(validPred, "valid.predict", timeInHospital);
    	savePredictions(testPred, "test.predict", timeInHospital);

   }
	
}