package pigeyedata;
import java.util.*;
import java.io.*;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  
public class AlphaBetaAnalysis {
	static boolean hasRaw = false;
	static boolean hasSmoothed = false;
	static String outFile;
	static String inFile;
	static XSSFSheet sheet;
	static XSSFWorkbook wb;
	static String sheetName;
	static String position;
	static LinkedList<WaveValue> pigList = new LinkedList<WaveValue>();
	
	public static void main(String[]args) throws InterruptedException{
		DragDropWindow importFileWindow = new DragDropWindow("Import Excel Files", "Drag and Drop input excel file");
        
        // Wait for the input file to be dropped
        while (importFileWindow.getInputFilePath() == null) {
            // Delay here to avoid busy waiting
        	Thread.sleep(100);
        }
        importFileWindow.myDragDropListener.output = true;
        // Change window text for output file
        importFileWindow.setWindowLabel("Drag and Drop output excel file");

        // Wait for the output file to be dropped
        while (importFileWindow.getOutputFilePath() == null) {
            // Delay here to avoid busy waiting
        	Thread.sleep(100);
        }

        // At this point, you have both input and output file paths
        inFile = importFileWindow.getInputFilePath();
        outFile = importFileWindow.getOutputFilePath();

		LinkedList<WaveValue> waves = new LinkedList<WaveValue>();
		try {
		File file = new File(inFile);   //creating a new file instance  
		FileInputStream fis = new FileInputStream(file);
		wb = new XSSFWorkbook(fis);
		/* 
		 * Iterating through the source excel sheet to find data that needs to be parsed
		 */
		for(int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheet = wb.getSheetAt(i);
			sheetName = wb.getSheetName(i);
			Iterator<Row> itr = sheet.iterator();
			if(sheet.getRow(0) == null) {
				continue;
			}
			Row row = itr.next();
			Iterator<Cell> cellItr = row.cellIterator();
			while(cellItr.hasNext()) {
				Cell cell = cellItr.next();
				String val = cell.getStringCellValue();
				if(val.length() > 0) {		
					if(Character.toUpperCase(val.charAt(0)) == 'L'|| Character.toUpperCase(val.charAt(0)) == 'R') {		//Determines if the cell contains the character "L" or "R" to save the position of the eye
						position = val;		
					}
				}
				if(cell.getStringCellValue().equals("Time/ms")) {		//Determines whether the cell has the title "Time/ms" in order to find the columns in the row that have data
					waves = waveList(position, cell);		//Performs waveList method to iterate through and store all of the ERG data for the specific pig eye
					alphaBeta(waves);		//Performs alphaBeta method with the list of all data in a given pig eye		
				}
			}
		}
		newBook(pigList); // creates a new workbook and copies analyzed data into the output file		
		} catch(Exception e) {
			e.printStackTrace();
		}
		importFileWindow.dispose(); //Closes window when program is complete
	}
	/*
	 * Iterates through the columns with data and saves the Latency, Raw amplitude, and Smoothed Amplitude to individual objects, then saves them to a list and returns the list of wave values.
	 */
	public static LinkedList<WaveValue> waveList(String position, Cell cell) {
		int index = cell.getColumnIndex();
		Row row = null;
		LinkedList<WaveValue> temp = new LinkedList<WaveValue>();
		Iterator<Row> rowItr = sheet.iterator();
		rowItr.next();
		while(rowItr.hasNext()) { //Creates a new WaveValue Object for each measurement in the data set.
			WaveValue piggy = new WaveValue(position, sheetName, rowItr.next().getCell(index).getNumericCellValue(), 0, 0);
			temp.add(piggy);
		}
		while(sheet.getRow(0).getCell(index) != null){ 
			System.out.println(sheet.getRow(0).getCell(index).toString());
			Cell current = sheet.getRow(0).getCell(index);
			rowItr = sheet.iterator();
			Iterator<WaveValue> pigItr = temp.iterator();
			if(Character.toUpperCase(current.toString().charAt(0)) == 'S') { //Checks if there are "Smoothed" measurements in the excel sheet, then copies smoothed values.
				hasSmoothed = true;
				rowItr.next();
				while(rowItr.hasNext()) {
					row = rowItr.next();
					WaveValue pigPart = pigItr.next();
					pigPart.smoothed = row.getCell(index).getNumericCellValue();
				}
			}else if(Character.toUpperCase(current.toString().charAt(0)) == 'W') { //Checks is there are "Raw" values in the excel sheet, then copies raw values.
				hasRaw = true;
				rowItr.next();
				while(rowItr.hasNext()) {
					row = rowItr.next();
					WaveValue pigPart = pigItr.next();
					pigPart.waveForm = row.getCell(index).getNumericCellValue();
				}
			}
			index++;
		}
		return temp;
	}
	/** 
	* 	Parses through raw and smoothed values in WaveValue object
	* 	Finds highest and lowest value of both smoothed and raw variables  in the list of WaveValue objects
	* 	Adds both highest and lowest of raw and smoothed values into a lists depending on the eye being measured 
	**/
	public static void alphaBeta(LinkedList<WaveValue> vals) {
		Iterator<WaveValue> iter = vals.iterator();
		// defines lowest and highest of each value to independent objects
		WaveValue lowestRaw = null;
		WaveValue highestRaw = null;
		WaveValue lowestSmoothed = null;
		WaveValue highestSmoothed = null;
		// Iterates through the list of WaveValues to define a base lowest and highest WaveValue within the restraints of the latency
		// Latency must be less than 25.0 to prevent false alpha or beta values
		while(iter.hasNext()) {
			WaveValue next = iter.next();
			if(next.getLatency() < 25.0 && next.getLatency() > 0) { 
				if(hasRaw) {
					lowestRaw = next;
					highestRaw = next;
				}
				if(hasSmoothed) {
					lowestSmoothed = next;
					highestSmoothed = next;
				}
				break;
			}
		}
		// Finds the lowest and highest WaveForm and smoothed value within a set of parameters for latency.
		// Latency is constrained to avoid false alpha or beta values
		while(iter.hasNext()) {
			WaveValue next = iter.next();
			if(hasRaw) {
				if(next.getWaveForm() < lowestRaw.getWaveForm() && next.getLatency() < 15.0 && next.getLatency() > 0) {
					lowestRaw = next;
				}
				if(next.getWaveForm() > highestRaw.getWaveForm() && next.getLatency() < 55.0 && next.getLatency() > 15) {
					highestRaw = next;
				}
			}
			if(hasSmoothed) {
				if(next.getSmoothed() < lowestSmoothed.getSmoothed() && next.getLatency() < 15.0 && next.getLatency() > 0) {
					lowestSmoothed = next;
				}
				if(next.getSmoothed() > highestSmoothed.getSmoothed() && next.getLatency() < 55.0 && next.getLatency() > 15) {
					highestSmoothed = next;
				}
			}
		}
		if(hasRaw) {
			lowestRaw.alpha = true;
			highestRaw.beta = true;
		}
		if(hasSmoothed) {
			lowestSmoothed.alpha = true;
			highestSmoothed.beta = true;
			lowestSmoothed.smoothAnalysis = true;
			highestSmoothed.smoothAnalysis = true;
		}
		if(hasRaw) {
			pigList.add(lowestRaw);
			pigList.add(highestRaw);
		}
		if(hasSmoothed) {
			pigList.add(lowestSmoothed);
			pigList.add(highestSmoothed);
		}
	}
	//Creates and formats the output sheet to copy the alpha and beta values.
	public static void newBook(LinkedList<WaveValue> fin) {
		wb = new XSSFWorkbook();
		sheet = wb.createSheet("PigeyeO");
		Iterator<WaveValue> waveIter = fin.iterator();
		for(int i = 0; i < fin.size(); i++) {
			Row cr = sheet.createRow(i);
			for(int j = 0; j < 13; j++) {
				cr.createCell(j);
			}
		}
		/*
		 * Setting up sheet to have easy to read format for the data
		 */
		if(hasRaw && hasSmoothed) {
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 6));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 11));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 6, 6));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 11, 11));
		}else {
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 6));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 6, 6));
		}
		Iterator<Row> rIter = sheet.iterator();
		while(rIter.hasNext() && waveIter.hasNext()) {
			Row row = rIter.next();
			Iterator<Cell> cIter = row.iterator();
			if(row.getRowNum() == 0) {
					cIter.next();
					cIter.next();
				if(hasRaw && hasSmoothed) {
					cIter.next().setCellValue("Raw");
					cIter.next();
					cIter.next();
					cIter.next();
					cIter.next();
					cIter.next().setCellValue("Smoothed");
				} else if(hasRaw){
					cIter.next().setCellValue("Raw");
				}else {
					cIter.next().setCellValue("Smoothed");
				}
			}else if(row.getRowNum() == 1) {
				cIter.next();
				cIter.next();
				cIter.next().setCellValue("a-wave(valley)");
				cIter.next();
				cIter.next().setCellValue("b-wave(peak)");
				cIter.next();
				cIter.next().setCellValue("Ratio b/a");
				if(hasRaw && hasSmoothed ) {
					cIter.next().setCellValue("a-wave(valley)");
					cIter.next();
					cIter.next().setCellValue("b-wave(peak)");
					cIter.next();
					cIter.next().setCellValue("Ratio b/a");
				}
			}else if(row.getRowNum() == 2) {
				cIter.next().setCellValue("PigID");
				cIter.next().setCellValue("Eye");
				cIter.next().setCellValue("Latency(ms)");
				cIter.next().setCellValue("Amplitude(uV)");
				cIter.next().setCellValue("Latency(ms)");
				cIter.next().setCellValue("Amplitude(uV)");
				if(hasRaw && hasSmoothed) {
					cIter.next();
					cIter.next().setCellValue("Latency(ms)");
					cIter.next().setCellValue("Amplitude(uV)");
					cIter.next().setCellValue("Latency(ms)");
					cIter.next().setCellValue("Amplitude(uV)");
				}
			}else {
				/*
				 * Adding the data into the sheet, organized by pig type
				 */
				if(waveIter.hasNext()) {
					WaveValue Wav = (WaveValue)waveIter.next();
					cIter.next().setCellValue(Wav.type);
					cIter.next().setCellValue(Wav.leftOrRight);
					cIter.next().setCellValue(Wav.latency);
					cIter.next().setCellValue(Wav.waveForm);
					double alpha = Wav.waveForm;
					if(hasRaw) {
						Wav = (WaveValue)waveIter.next();
						cIter.next().setCellValue(Wav.latency);
						cIter.next().setCellValue(Wav.waveForm);
						cIter.next().setCellValue(Wav.waveForm/alpha);
					}
					if(hasSmoothed) {
						Wav = (WaveValue)waveIter.next();
						cIter.next().setCellValue(Wav.latency);
						cIter.next().setCellValue(Wav.smoothed);
						alpha = Wav.waveForm;
						Wav = (WaveValue)waveIter.next();
						cIter.next().setCellValue(Wav.latency);
						cIter.next().setCellValue(Wav.smoothed);
						cIter.next().setCellValue(Wav.smoothed/alpha);
					}
				}
			}
		}
		try {
		FileOutputStream out = new FileOutputStream(new File(outFile));
        wb.write(out);
        out.close();
		}catch(IOException e) {
			System.out.println("Couldn't find the file! If the excel files you are using is open on your computer, please close it and try again.");
			e.printStackTrace();
		}
	}
}