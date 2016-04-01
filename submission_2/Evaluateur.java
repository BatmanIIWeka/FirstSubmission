package submission_2;

import weka.core.Attribute;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.bayes.*;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;


public class Evaluateur {

	// Number of cross validation and runs to perform
	private int folds;
	private int runs;
	// Number of classifiers to return (best one, then second best, and so on)
	private int NB_RESULTS;
	private String startPath;
	
	public Evaluateur() {
		this.folds = 10;
		this.runs = 0;
		this.NB_RESULTS = 3;
	}
	
	public Evaluateur(int f, int r, int nb) {
		this.folds = f;
		this.runs = r;
		this.NB_RESULTS = nb;
	}
	
	public Evaluateur(String path) {
		this.folds = 10;
		this.runs = 0;
		this.NB_RESULTS = 3;
		this.startPath = path;
	}
	
	/*
	 * getClassifiers method
	 *    trains and cross-validate a representative sample of classifiers to get the
	 *    best of them, in order to perform a comparison of their results
	 * params :
	 *    - train, valid and test Data (Instances) : dataset to perform tests on
	 *    - ind (Integer) : index of the attribute we're trying to guess
	 * returns :
	 *    - an ArrayList of Classifiers, with a fixed size (3 by default)
	 *      containing by descending order the best ones
	 */
	public ArrayList<Classifier> getClassifiers(Instances trainData, Instances testData, Instances validData, int ind) throws Exception {
		
		/*
		 * We're creating a list of classifiers with their options 
		 * The printwriter is here to write results on files, for further comparison
		 * The crimeSolved attribute is the one we're trying to guess
		 */
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		ArrayList<Classifier> results = new ArrayList<Classifier>();
		PrintWriter pw;
		Attribute crimeSolved = trainData.attribute(ind);
		
		
		// we populate the list
		classifiers.add(new J48());
		classifiers.add(new NaiveBayes());
		classifiers.add(new REPTree());
		classifiers.add(new RandomTree());
		classifiers.add(new OneR());
		classifiers.add(new DecisionTable());
		classifiers.add(new BayesNet());
		//classifiers.add(new RandomForest());	// error heap space..
		//classifiers.add(new SimpleCart());	// takes minutes to run..
		//classifiers.add(new LibSVM());		// error heap space..

		/*
		 * We cross validate the data with all classifiers, writing results to file.
		 */
		for (Classifier cls : classifiers) {
		//	Random rand = new Random(++runs);
			cls.buildClassifier(trainData);
			
			Evaluation ev = new Evaluation(trainData);
			ev.evaluateModel(cls, testData);
			FastVector pred = ev.predictions();
			
	    	ThresholdCurve tc = new ThresholdCurve();
	    	Instances tcurve = tc.getCurve(pred);
	    	System.out.println("ROC : " + this.getAUC(ind, pred, crimeSolved));
	    	//System.out.println("Curve : " + /*ThresholdCurve.getROCArea(*/tcurve);
	    	
			pw = new PrintWriter(startPath + cls.getClass().getName() + "_valid.pred", "UTF-8");
			for (int i = 0; i < pred.size(); i++) {
				double val = ((NominalPrediction) pred.elementAt(i)).predicted();
				pw.print((crimeSolved.value((int) val)) + "\n");
			}
			pw.close();
		}
		
		/*
		 * Then, we sort the classifiers based on performance, and return the top NB_RESULTS
		 */
		// Hashtable : we pair the Classifier with its score, for comparison
		Hashtable<Classifier, Integer> h = new Hashtable<Classifier, Integer>();
		
		// We iterate through classifiers, getting good guesses for each one as score
		for (Classifier cls : classifiers) {
			int score = 0;
			BufferedReader f = new BufferedReader(new FileReader(startPath + cls.getClass().getName() + "_valid.pred"));
			String l = f.readLine();
			while(l != null && !(l.isEmpty())) {
				score += Integer.parseInt(l);
				l = f.readLine();
			}
			h.put(cls, score);
			f.close();
			System.out.println("Comparateur : " + cls.getClass().getName() + " -> " + score + " points");
		}
		// Then, we simply populate the result with the top NB_RESULTS best, and return it
		for(int i = 0 ; i < NB_RESULTS ; i++) {
			Classifier maxKey = null;
			int maxValue = Integer.MIN_VALUE;
			if (i > h.size())
				break;
			for(Map.Entry<Classifier, Integer> e : h.entrySet()) {
			     if(e.getValue() > maxValue) {
			         maxValue = e.getValue();
			         maxKey = e.getKey();
			     }
			}
			results.add(i, maxKey);
			h.remove(maxKey);
		}
		return (results);
	}

	private double getAUC(int labelIndex, FastVector pred, Attribute cs)
	{
		if (pred.size() == 0)
			return Double.NaN;
		ThresholdCurve tc = new ThresholdCurve();
		Instances result = tc.getCurve(pred);
		double d = ThresholdCurve.getROCArea(result);
		if (!Double.isNaN(d))
			return d;
		else
		{
			int tP = 0, tN = 0, fP = 0, fN = 0;
			double epsilon = 0.1;
			//			System.err.println("\nauc problem solving");
			for (int i = 0; i < pred.size(); i++)
			{
				NominalPrediction p = ((NominalPrediction) pred.elementAt(i));
				double actual = Double.parseDouble(cs.value((int) p.predicted())) == 0.0 ? 1.0 : 0.0;
				//				System.err.println(actual + " " + p.predicted());
				if (p.predicted() == 1.0)
				{
					if (actual == p.predicted())
						tP++;
					else
						fP++;
				}
				else
				{
					if (actual == p.predicted())
						tN++;
					else
						fN++;
				}
			}
						if (tP == 0 && tN == 0) // no correct predictions
							return 0.0;
						else if (fP == 0 && fP == 0) // no incorrect predictions
							return 1.0;
						else 
			if (tP == 0 && fN == 0) // no positive instances
				return Double.NaN;
			else if (tN == 0 && fP == 0) // no negative instances
				return Double.NaN;
			else
				throw new IllegalStateException("WTF TP:" + tP + " FP:" + fP + " TN:" + tN + " FN:" + fN);
		}
	}
	
	public double abs(double d) {
		return (d >= 0.0 ? d : -d);
	}

}