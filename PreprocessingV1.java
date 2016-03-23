import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class PreprocessingV1 {
	/* Attributes */
	public String trainPath;
	public String validPath;
	public String testPath;
	
	/* Constructor */
	public PreprocessingV1 (String train, String valid, String test) throws Exception {
		this.trainPath = train;
		this.validPath = valid;
		this.testPath = test;
	}

	/* Methods */
	
	/** Reads and checks the files 
	 * @throws Exception **/ 
	public ArrayList<Instances> readFile() throws Exception{
		ArrayList<Instances> files = new ArrayList<Instances>(3);
		 
		DataSource dTrain = new DataSource(this.trainPath);
		DataSource dValid = new DataSource(this.validPath);
		DataSource dTest = new DataSource(this.testPath);
		
		Instances train = dTrain.getDataSet();
		Instances valid = dValid.getDataSet();
		Instances test = dTest.getDataSet();

		files.add(0, train); //l'ArrayListe contient l'instance train à l'indice 0
		files.add(1, valid);
		files.add(2, test);

		return files;
	}
	
	/** Saves the files as .zip **/
	public  void saveFile(String zipFile ,File valid, File test) throws IOException{
		
		FileOutputStream dest = new FileOutputStream(zipFile);
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(dest));
		ZipEntry validEntry = new ZipEntry(valid.getPath().substring(0, valid.getPath().length()-17)+zipFile+"ref_valid.predict"); //substring(beg,end);
		System.out.println(valid.getPath());
	    ZipEntry testEntry = new ZipEntry(test.getPath().substring(0, test.getPath().length()-16)+zipFile+"ref_test.predict");
	    System.out.println(test.getPath());
	    zipOut.putNextEntry(validEntry);
		/*
		byte[] vData = valid.toString().getBytes();
		zipOut.write(vData,0,vData.length);
		zipOut.closeEntry();
		zipOut.putNextEntry(testEntry);
		
		byte[]  tData = test.toString().getBytes();
		zipOut.write(tData,0,tData.length);
		zipOut.closeEntry(); */
		zipOut.close();
			
	}
}
