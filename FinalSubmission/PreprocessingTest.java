package batman;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.File;//import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import weka.core.Instances;


public class PreprocessingTest {

	String ext = ".arff";
	String startPath = "/Users/Yang/Documents/MPI_2015-2016/S2/Mini-Projets/Projet_Siqi";
	String trainPath = startPath + "/train" + ext;
	String validPath = startPath + "/valid" + ext;
	String testPath = startPath + "/test" + ext;
	Preprocessing preproc;
	
	
	@Before 
	public void initPreproc() throws Exception {
	preproc = new Preprocessing (trainPath, validPath, testPath);
	
	}
	
	/* 
	 * Test1: si le fichier nouveau cree existe avec le bon nom 
	 */
	@Test
	public void CreationSuccess() throws Exception {
		if (preproc == null){
			fail("test1:Objet du type Preprocessing n'est pas cree"
					+ "revoir constructeur Preprocessing");
		}
		/*assert(preproc, not(null)): "test1:Objet du type Preprocessing n'est pas cree"*/
	}
	
	/*
	 * Test2: generer fichier arff avec saveFile et comparer ensuite avec diff dans la commande LINUX
	 */
	@Test
	public void TestreadFile() throws Exception {
		 //Recuperer fichier train.arff
		 Instances dataSet = preproc.readFile().get(0);
		 BufferedWriter writer = new BufferedWriter(new FileWriter(
			"/Users/Yang/Documents/MPI_2015-2016/S2/Mini-Projets/Projet_Siqi/TestReadFile/train.arff"));
		 writer.write(dataSet.toString());
		 writer.flush();
		 writer.close();
	}
	
	/*
	 * Test3: pour eviter que le fichier trouve soit un ancien creation
	 */
	@Test
	public void TestsaveFile() throws IOException {

    	File validRes = new File("/Users/Yang/Documents/workspace/batman/ref_valid.predict");    
    	File testRes = new File("/Users/Yang/Documents/workspace/batman/ref_test.predict");    	
    	String s = "bat2_pred.zip";
    	preproc.saveFile(s, validRes, testRes);
    	
    	File f = new File(s);
    	
    	if (!(f.exists() && f.isFile())) {
    	   fail("File doesn\'t exist");
    	   if (!(f.length() > 0)) {
    		   fail("File est vide");
    	   }
    	}
    	Date d = new Date(f.lastModified());
    	Date current = new Date();
    	long epsilon = 5000;
    	
    	if (current.getTime() - d.getTime() > epsilon){
    		fail("Temps de creation depasse 5 secondes");
    	} 
    	
	}

}
