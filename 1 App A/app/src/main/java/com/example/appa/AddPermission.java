package com.example.appa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddPermission extends ListActivity {
	
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
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_permission);
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
                
        if(!sp.getString(getApplicationContext(), "status").equals("success")){
        	commonRequestThread();
        }
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

        		if(!text.contains("-*"+file.getPath()+"+-")){

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
			text += "\n-*" + path.get(position) +"+-";
			//toast(path.get(position)+"");
			item.remove(position);
			path.remove(position);
			generateNoteOnSD("note.txt",text);
			toast("Added to Secured list");
			fileList.notifyDataSetChanged();
		  }
	}	
	
	public void commonRequestThread() {
		status = "Please try again later";

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Runnable runnable = new Runnable() {
					public void run() {
						
					}
				};
				try {
					runOnUiThread(runnable);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
		Thread checkUpdate = new Thread() {
			public void run() {
				try {
					commonRequest();
				} catch (Exception e) {
					System.out.println("Error in fetching json : "
							+ e.toString());
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();
	}	

	public void commonRequest()
    {
		System.out.println("Common request sent : ");
		// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://cloudganesan.000webhostapp.com/0/add_data.php");
        InputStream is = null;
        String result = "";
		try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            
	        nameValuePairs.add(new BasicNameValuePair("from", "add_user_details"));
	        nameValuePairs.add(new BasicNameValuePair("app_name", "1 Security To files and folder in SD card"));
	        nameValuePairs.add(new BasicNameValuePair("email", possibleEmail(this)));
			
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	System.out.println("Error 1 : "+e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	System.out.println("Error 2 : "+e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
              sb.append(line + "n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
          	System.out.println("Error 2 : "+e.toString());
        }
        System.out.println("result : "+result);
        res = result;
        JSONObject food_object;
        
//        TextView txtFirstName = (TextView) rootView.findViewById(R.id.txtFirstName);
//        txtFirstName.setText(""+res);
        
		try {
			//food_object = new JSONObject(result);
			food_object = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
			JSONArray foo_array = food_object.getJSONArray("add_user_details");
			
			for (int i = 0; i < foo_array.length(); i++) {
				JSONObject js = foo_array.getJSONObject(i);

				try {
					status = js.getString("status");
					sp.putString(getApplicationContext(), "status", "success");
				} catch (Exception e) {
					// TODO: handle exception
				}				
			}
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
          	System.out.println("Error 3 : "+e.toString());
			e.printStackTrace();
		}
    }

	String res = "", status = "";
	
	public static String possibleEmail(Context context) {
		String possibleEmail = "";
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(context).getAccounts();
		if(possibleEmail.equals(""))
			for (Account account : accounts) {
			    if (emailPattern.matcher(account.name).matches()) {
			        possibleEmail = account.name;	
			        break;
			    }
			}
		return possibleEmail; 
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connec.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
