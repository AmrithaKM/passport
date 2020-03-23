package com.pcc.utility;

import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowException;
import net.corda.core.messaging.CordaRPCOps;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class PoliceCommonUtils {

    public SecureHash createSecureHash(CordaRPCOps rpc, String fileName, String filePath) throws Exception {
        InputStream inputStreamAddress = null;
        String imageType = getImageType(filePath);
        File zippedAddressFile = convertFileToZip(fileName + "." + imageType, filePath);
        URL newFileURL = zippedAddressFile.toURI().toURL();
        inputStreamAddress = newFileURL.openStream();

        return rpc.uploadAttachment(inputStreamAddress);
    }

    /**
     * Private methods start
     **/
    private File convertFileToZip(String fileName,
                                         String filePath) {

        File file = new File(filePath);
        File zippedFile = new File(file.getPath() + ".zip");
        try {
            FileOutputStream fos = new FileOutputStream(zippedFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(fileName);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(file);

            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zippedFile;
    }

    /**
     * Downloads a file from a URL
     *
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public String downloadFile(String fileURL, String saveDir)
            throws IOException {
        int BUFFER_SIZE = 4096;
        String saveFilePath = "";
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        return saveFilePath;
    }

    private String getImageType(String filePath) {
        String extensionSeparator = ".";
        int dot = filePath.lastIndexOf(extensionSeparator);
        return filePath.substring(dot + 1);
    }
    /**Private methods end**/


}
