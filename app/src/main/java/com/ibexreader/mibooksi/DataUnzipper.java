package com.ibexreader.mibooksi;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataUnzipper {
	boolean zipError = false;

	private static void createDirectory(File paramFile) {
		if (!paramFile.exists()) {
			if (!paramFile.mkdirs()){
				
			}
//				throw new RuntimeException("Can't create directory "
//						+ paramFile);
		} else
			Log.d("oxford", "DataUnzipper.createDir() - Exists directory: "
					+ paramFile.getName());
	}

	public static void unzipEntry(ZipFile paramZipFile, ZipEntry paramZipEntry,
								  File paramFile) throws IOException {
//		if (paramZipEntry.isDirectory())
//			createDirectory(new File(paramFile, paramZipEntry.getName()));

//		File localFile = new File(paramFile, paramZipEntry.getName());
//		if (!localFile.getParentFile().exists())
//			createDirectory(localFile.getParentFile());
		Log.d("oxford", "DataUnzipper.unzipEntry() - Extracting: "
				+ paramZipEntry);
		BufferedInputStream localBufferedInputStream = new BufferedInputStream(
				paramZipFile.getInputStream(paramZipEntry));
		BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(paramFile));
		try {
			IOUtils.copy(localBufferedInputStream, localBufferedOutputStream);
			localBufferedOutputStream.close();
			localBufferedInputStream.close();
			// continue;
		} catch (Exception localException) {
			Log.d("oxford", "DataUnzipper.unzipEntry() - Error: "
					+ localException);
//			setZipError(true);
			localBufferedOutputStream.close();
			localBufferedInputStream.close();
			// continue;
		} finally {
			localBufferedOutputStream.close();
			localBufferedInputStream.close();
		}

	}

	public boolean isZipError() {
		return this.zipError;
	}

	public void setZipError(boolean paramBoolean) {
		this.zipError = paramBoolean;
	}

	@SuppressWarnings("rawtypes")
	public void unzip(String paramString, File paramFile) {
		try {
			Log.d("oxford", "DataUnzipper.unzip() - File: " + paramString);
			ZipFile localZipFile = new ZipFile(paramString);
			
			Enumeration localEnumeration = localZipFile.entries();
			while (localEnumeration.hasMoreElements())
				unzipEntry(localZipFile,
						(ZipEntry) localEnumeration.nextElement(), paramFile);
		} catch (Exception localException) {
			Log.d("oxford", "DataUnzipper.unzip() - Error extracting file "
					+ paramString + ": " + localException);
			setZipError(true);
		}
	}



	public static  void searchForSimilar(String zipUri, String keyword, String extratcPath) throws IOException {
		ZipFile zip  =  new ZipFile(zipUri);

		Enumeration<? extends ZipEntry> e = zip.entries();

		while (e.hasMoreElements()){
			ZipEntry en = e.nextElement();
			if(en!=null && !en.isDirectory()){
				if(en.getName().toLowerCase().contains(keyword.toLowerCase())){
					if(extratcPath!=null){
						unzipEntry(zip,en, new File(extratcPath));
					}
					break;
				}
			}
		}

		zip.close();

	}
}
