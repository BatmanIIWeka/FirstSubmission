package submission_2;

import weka.classifiers.*;
import java.util.*;

public class Comparateur {

	public void Comp(ArrayList<Classifier> classifiers) {
    	for (int i = 0; i < classifiers.size(); i++) {
    		System.out.println((classifiers.get(i)).getClass().getName());
    	}
	}
}
