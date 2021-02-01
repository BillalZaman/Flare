package com.infotech4it.flare.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.infotech4it.flare.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private ProgressDialog pDialog;
    private Context context;
    private String reportType;

    private String fileName;

    private messageDownloadFile _messageDownloadFile;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFileName(String fileName) {
        this.fileName=fileName;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public interface messageDownloadFile {

        void showMessageDownloadFile(String message);

    }

    public void initializeListeners(messageDownloadFile _messageDownloadFile) {
        this._messageDownloadFile=_messageDownloadFile;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showDialog(progress_bar_type);
        pDialog = new ProgressDialog(context,  R.style.ProgressBar);
        pDialog.setMessage("Opening file. Please wait...");
        //pDialog.setProgressStyle();
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;

        String fullPath="";

        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
//            OutputStream output = new FileOutputStream(Environment
//                    .getExternalStorageDirectory().toString()
////                    + "/Butler/Reports/"+reportType+"_"+ Calendar.getInstance().getTimeInMillis()+".xlsx");
//                    + "/2011.xlsx");

//            File file = new File("/Butler/Reports/"+reportType+"_"+ Calendar.getInstance().getTimeInMillis()+".xlsx");
//            OutputStream output = new FileOutputStream(file);

            String path = "/sdcard/";
// Create the parent path
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fullName = null;

            fullName=path + fileName;

//            if (url.toString().toLowerCase().contains(".xls")) {
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".xls";
//            }else if (url.toString().toLowerCase().contains("pdf")) {
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".pdf";
//            }
//
//            if (url.toString().toLowerCase().equals("doc") || url.toString().toLowerCase().equals("docx")) {
//                // Word document
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".pdf";
//
//            } else if(url.toString().equalsIgnoreCase("pdf")) {
//                // PDF file
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".pdf";
//            } else if(url.toString().equalsIgnoreCase("ppt") || url.toString().equalsIgnoreCase("pptx")) {
//                // Powerpoint file
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".pdf";
//            } else if(url.toString().equalsIgnoreCase("xls") || url.toString().equalsIgnoreCase("xlsx")) {
//                // Excel file
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".xls";
//            }
//            else if(url.toString().equalsIgnoreCase("txt")) {
//                // Text file
//                fullName=path + Calendar.getInstance().getTimeInMillis() +".pdf";
//            }  else {
//                intent.setDataAndType(uri, "*/*");
//            }


            File file=null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                // Do something for lollipop and above versions
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File myDir = new File(root + "/orbis");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                file = new File (myDir, fileName);
                if (file.exists ())
                    file.delete ();
            } else{
                // do something for phones running an SDK before lollipop
                file = new File (fullName);
            }


            OutputStream output = new FileOutputStream(file);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                // Do something for lollipop and above versions
                fullPath=file.getAbsolutePath();
            } else{
                // do something for phones running an SDK before lollipop
                fullPath=fullName;
            }

            //Log.d("file_url_____",fullName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return fullPath;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded

        Log.d("file_url_____",file_url);
        _messageDownloadFile.showMessageDownloadFile(file_url);

//        File file = new File(file_url);
//        Uri path=null;
//
//        if (Build.VERSION .SDK_INT>=Build.VERSION_CODES.N) {
//            path =getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
//        }else {
//            path = Uri.fromFile(file);
//        }
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(path, "application/vnd.ms-excel");
//        context.startActivity(intent);



        pDialog.dismiss();

    }

}
