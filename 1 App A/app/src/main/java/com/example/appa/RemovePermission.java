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
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RemovePermission extends ListActivity {
	
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
        setContentView(R.layout.remove_permission);
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

        		if(text.contains(""+file.getPath()+"")){

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
		File file = new File(path.get(position));
		
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
//			new AlertDialog.Builder(this)
//					.setIcon(R.drawable.ic_launcher)
//					.setTitle("[" + file.getName() + "]")
//					.setPositiveButton("OK", null).show();

//			tv.setVisibility(View.GONE);
			text = text.replace("\n-***a*" + path.get(position) +"+-","");
			text = text.replace("\n-***d*" + path.get(position) +"+-","");
			text = text.replace("\n-***" + path.get(position) +"+-","");
			text = text.replace("\n-*" + path.get(position) +"+-","");
			item.remove(position);
			path.remove(position);
			generateNoteOnSD("note.txt",text);
			fileList.notifyDataSetChanged();
//			generateNoteOnSD("note.txt",text);
//			//Toast.makeText(getApplicationContext(), "Added to secured list", Toast.LENGTH_SHORT).show();
//			toast("Added to secured list");
			toast("Removed from Secured list");
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
