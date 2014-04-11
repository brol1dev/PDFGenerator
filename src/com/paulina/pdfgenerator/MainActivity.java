package com.paulina.pdfgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends ActionBarActivity {

	private static final String LOG_TAG = "MainActivity";
	private static final String PDF_FILENAME = "example.pdf";
	
	private boolean readyToWrite = false;
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView = (TextView) findViewById(R.id.text_view);
		textView.setText("Getting PDF directory");
		
		new Thread() {
			
			public void run() {
				while (!isExternalStorageWritable()) {
					Log.v(LOG_TAG, "cant access external storage");
					readyToWrite = false;
				}
				
				if (readyToWrite) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// Tell user we're about to write
							textView.setText("Writing PDF...");
						}
					});
					
					try {
						File pdfFile = getStorageDir(PDF_FILENAME);
						createPDF(pdfFile);
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// Tell user we wrote the PDF.
								textView.setText("PDF Ready!");
								textView.setTextColor(Color.GREEN);
							}
						});
						
					} catch (FileNotFoundException e) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// Tell user we're about to write
								textView.setText("Cannot find file!");
								textView.setTextColor(Color.RED);
							}
						});
						e.printStackTrace();
					} catch (DocumentException e) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// Tell user we're about to write
								textView.setText("Something went wrong!");
								textView.setTextColor(Color.RED);
							}
						});
						e.printStackTrace();
					}
				}
			}
			
		}.start();
	}
	
	/**
	 * Tells if the external storage is available.
	 * 
	 * @return
	 */
	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			readyToWrite = true;
			return true;
		}
		return false;
	}

	/**
	 * Gets the path of the file.
	 * 
	 * @param pdfName
	 * @return
	 */
	private File getStorageDir(String pdfName) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName);
		
		return file;
	}
	
	/**
	 * Generates an iText PDF table.
	 * 
	 * @return
	 */
	private PdfPTable generatePDFTable() {
		// 3 columns table
		PdfPTable table = new PdfPTable(3);
		
		PdfPCell cell;
		
		cell = new PdfPCell(new Phrase("3 colspan"));
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("2 rowspan"));
		cell.setRowspan(2);
		table.addCell(cell);
		
		table.addCell("row 2, col 2");
		table.addCell("row 2, col 3");
		table.addCell("row 3, col 2");
		table.addCell("row 3, col 3");
		
		return table;
	}
	
	/**
	 * Creates the PDF in the specified file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	private void createPDF(File file) throws 
		FileNotFoundException, DocumentException {
		
		Document document = new Document();
		
		PdfWriter.getInstance(document, new FileOutputStream(file));
		
		document.open();
		document.add(generatePDFTable());
		document.close();
	}
}
