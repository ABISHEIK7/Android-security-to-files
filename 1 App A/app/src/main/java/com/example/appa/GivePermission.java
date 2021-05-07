package com.example.appa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GivePermission extends ListActivity {
	
	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	String curDirPath = "", text = "";
	File folder;
	SharedPreference sp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.give_permission);
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

				if (text.contains("***" + file.getPath() + "")
						|| text.contains("***a*" + file.getPath() + "")
						|| text.contains("***d*" + file.getPath() + "")) {

        			path.add(file.getPath());
	        		if(file.isDirectory()){
	        			item.add(file.getName() + "/");
	        		}else{
	        			item.add(file.getName());
	        		}
        		}
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
			
			if(text.contains("-***d*"+file.getPath())){
				
				//toast("-***d*"+file.getPath());
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	
				// set title
				alertDialogBuilder.setTitle("Do you want to allow access permission to this file");
	
				// alertDialogBuilder.setIcon(R.drawable.wrong);
				// set dialog message
				alertDialogBuilder
						//.setMessage("Do you want access permission for this file")
						.setCancelable(true)
						.setPositiveButton("Yes", 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										//text = text.replace("-*"+file.getPath(), "-***a*"+file.getPath());
										text = text.replace("-***d*"+file.getPath(), "-***a*"+file.getPath());
										generateNoteOnSD("note.txt",text);
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
				
			} else if(text.contains("-***a*"+file.getPath())){
			
				//toast("-***a*"+file.getPath());
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	
				// set title
				alertDialogBuilder.setTitle("Do you want to remove access permission to this file");
	
				// alertDialogBuilder.setIcon(R.drawable.wrong);
				// set dialog message
				alertDialogBuilder
						//.setMessage("Do you want access permission for this file")
						.setCancelable(true)
						.setPositiveButton("Yes", 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										//text = text.replace("-*"+file.getPath(), "-***a*"+file.getPath());
										text = text.replace("-***a*"+file.getPath(), "-***d*"+file.getPath());
										generateNoteOnSD("note.txt",text);
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
				
			} else if(text.contains("-***" + path.get(position) +"+-")){
			
				//toast("-***"+file.getPath());
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	
				// set title
				alertDialogBuilder.setTitle("Do you want to allow access permission to this file");
	
				// alertDialogBuilder.setIcon(R.drawable.wrong);
				// set dialog message
				alertDialogBuilder
						//.setMessage("Do you want access permission for this file")
						.setCancelable(true)
						.setPositiveButton("Yes", 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										text = text.replace("-***"+file.getPath(), "-***a*"+file.getPath());
										generateNoteOnSD("note.txt",text);
									}
						})				
						.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									text = text.replace("-***"+file.getPath(), "-***d*"+file.getPath());
									generateNoteOnSD("note.txt",text);									
								}
						});
	
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
	
				// show it
				alertDialog.show();
				
			}
			
			
		  }
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

}
