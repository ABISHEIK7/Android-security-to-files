package com.example.appb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileList extends ListActivity {
	
	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	String curDirPath = "", text = "";
	File folder;
	SharedPreference sp;
			
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(!isNetworkAvailable(this)){
			toast("Please connect internet and try again");
			finish();
		}
		text = readFile();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);
        myPath = (TextView)findViewById(R.id.path);
        

        curDirPath = root = Environment.getExternalStorageDirectory().getPath();
        
        folder = new File(curDirPath + "/AppA");
		if(!folder.exists())
			folder.mkdir();		
		
		

        sp = new SharedPreference();		
		if(!sp.getBoolean(this, "first_time")){
			generateNoteOnSD("note.txt","");			
		}
		
		text = readFile();
		
		sp.putBoolean(this, "first_time", true);
				
        getDir(root);
    }
    
    private void getDir(String dirPath)
    {
    	myPath.setText("Location: " + dirPath);
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{    			
    		item.add(root);
    		path.add(root);
    		item.add("../");
    		path.add(f.getParent());	
    	}
    	
    	System.out.println("text : "+text);
    	for(int i=0; i < files.length; i++)
    	{
    		File file = files[i];
    		
    		if(!file.isHidden() && file.canRead()){
    			
    			System.out.println(root+"/"+file.getName());

        		//if(!text.contains("-*"+file.getPath()+"+-")){

        			path.add(file.getPath());
	        		if(file.isDirectory()){
	        			item.add(file.getName() + "/");
	        		}else{
	        			item.add(file.getName());
	        		}
        		//}
    		}	
    	}

    	fileList =
    			new ArrayAdapter<String>(this, R.layout.row, item);
    	setListAdapter(fileList);	
    }
    
    ArrayAdapter<String> fileList;

	public void generateNoteOnSD(String sFileName, String sBody){
	    try
	    {
	        File gpxfile = new File(folder, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	        //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	    }
	    catch(IOException e)
	    {

	    }
	} 
	
	public String readFile(){

		//Get the text file
        File gpxfile = new File(folder, "note.txt");
        
		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(gpxfile));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}
		return text.toString();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		final File file = new File(path.get(position));
		
		if (file.isDirectory())
		{
			if(file.canRead()){
				curDirPath = path.get(position);
				getDir(curDirPath);
			}else{
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] folder can't be read!")
					.setPositiveButton("OK", null).show();	
			}	
		}else {
			
			if(text.contains("-***d*"+file.getPath()+"+-")){

				new AlertDialog.Builder(this)
						.setIcon(R.drawable.wrong)
						.setTitle("Permission denied")
						.setPositiveButton("OK", null).show();
				
			}else if(text.contains("-***a*"+file.getPath()+"+-")){

				new AlertDialog.Builder(this)
						.setIcon(R.drawable.right)
						.setTitle("Permission granted")
						.setPositiveButton("OK", null).show();
				
			}else if(text.contains("-***"+file.getPath()+"+-")){

				new AlertDialog.Builder(this)
						.setIcon(R.drawable.question)
						.setTitle("Waiting for access permission")
						.setPositiveButton("OK", null).show();
				
			}else if(text.contains("-*"+file.getPath()+"+-")){

				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

				// set title
				alertDialogBuilder.setTitle("Can not Readable");

				alertDialogBuilder.setIcon(R.drawable.wrong);
				// set dialog message
				alertDialogBuilder
						.setMessage("Do you want access permission for this file")
						.setCancelable(true)
						.setPositiveButton("Yes", 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										text = text.replace("-*"+file.getPath(), "-***"+file.getPath());
										generateNoteOnSD("note.txt",text);
										generateNotification();
									}
						})				
						.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									
								}
						});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
								
			}else{

				new AlertDialog.Builder(this)
						.setIcon(R.drawable.right)
						.setTitle("Readable")
						.setPositiveButton("OK", null).show();
			}
			
		  }
	}	
	
	public void generateNotification(){
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.appa_icon;        
		CharSequence tickerText = "Hello 1"; // ticker-text
		long when = System.currentTimeMillis();         
		Context context = getApplicationContext();     
		CharSequence contentTitle = "App B request File access permission";  
		CharSequence contentText = "Do you want give permission?";      
		Intent notificationIntent = new Intent(this, CallActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		// and this
		long time= System.currentTimeMillis();
		final int HELLO_ID = 1;
		mNotificationManager.notify(HELLO_ID, notification);
	}
	
	public static int randInt(int min, int max) {

	    Random rand = null;
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	Toast toast;
	public void toast(String str) {
		try {
			toast.cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}
		toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	 	
    	if(curDirPath.equals(root))
    	{
    		super.onBackPressed();    		
    	}else{
			curDirPath = path.get(1);
			getDir(curDirPath);    		
    	}
	}
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connec.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
