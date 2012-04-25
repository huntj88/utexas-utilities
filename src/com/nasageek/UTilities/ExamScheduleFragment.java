package com.nasageek.UTilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;




public class ExamScheduleFragment extends ActionModeFragment {

	private boolean noExams;
	private TextView login_first;
	private ConnectionHelper ch;
	private DefaultHttpClient httpclient;
	private SharedPreferences settings;
	private ArrayList<String> examlist;
	private ListView examlistview;
	private LinearLayout linlay;
	private ExamAdapter ea;
	private LinearLayout pb_ll;
	private SherlockFragmentActivity parentAct;
	private View vg;
	public ActionMode mode;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{			
		vg = inflater.inflate(R.layout.exam_schedule_fragment_layout, container, false);
		
		login_first = (TextView) vg.findViewById(R.id.login_first_tv);
		pb_ll = (LinearLayout) vg.findViewById(R.id.examschedule_progressbar_ll);
		examlistview = (ListView) vg.findViewById(R.id.examschedule_listview);
		linlay = (LinearLayout) vg.findViewById(R.id.examschedule_linlay);
		
		if(!ConnectionHelper.cookieHasBeenSet())
		{	
			pb_ll.setVisibility(View.GONE);
			login_first.setVisibility(View.VISIBLE);
			return vg;
		}

		try
		{
			parser();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vg;
	}
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		parentAct = this.getSherlockActivity();

		examlist=  new ArrayList<String>();
		settings = PreferenceManager.getDefaultSharedPreferences(parentAct);
		ch = new ConnectionHelper(parentAct);
		
		
		
	}
	public void parser() throws Exception
	{

		httpclient = ConnectionHelper.getThreadSafeClient();
		httpclient.getCookieStore().clear();
		
		BasicClientCookie cookie = new BasicClientCookie("SC", ConnectionHelper.getAuthCookie(parentAct,httpclient));
    	cookie.setDomain(".utexas.edu");
    	httpclient.getCookieStore().addCookie(cookie);
    	
    	new fetchExamDataTask(httpclient).execute();
	}
	public ActionMode getActionMode()
	{
		return mode;
	}
	private class fetchExamDataTask extends AsyncTask<Object,Void,Character>
	{
		private DefaultHttpClient client;
		
		public fetchExamDataTask(DefaultHttpClient client)
		{
			this.client = client;
		}
		
		@Override
		protected Character doInBackground(Object... params)
		{
			HttpGet hget = new HttpGet("https://utdirect.utexas.edu/registrar/exam_schedule.WBX");
	    	String pagedata="";

	    	try
			{
				HttpResponse response = client.execute(hget);
		    	pagedata = EntityUtils.toString(response.getEntity());
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	if(pagedata.contains("will be available approximately three weeks"))
	    	{	
	    		noExams = true;
	    		return ' ';
	    	}
	    	else
	    		noExams = false;
	    	Pattern rowpattern = Pattern.compile("<tr >.*?</tr>",Pattern.DOTALL);
	    	Matcher rowmatcher = rowpattern.matcher(pagedata);
	    	
	    	while(rowmatcher.find())
	    	{
	    		String rowstring = "";
	    		String row = rowmatcher.group();
	    		if(row.contains("Unique") || row.contains("Home Page"))
	    			continue;
	    		
	    		Pattern fieldpattern = Pattern.compile("<td.*?>(.*?)</td>",Pattern.DOTALL);
	    		Matcher fieldmatcher = fieldpattern.matcher(row);
	    		while(fieldmatcher.find())
	    		{
	    			String field = fieldmatcher.group(1).replace("&nbsp;"," ").trim().replace("\t","");
	    			Spanned span = Html.fromHtml(field);
	    			String out = span.toString();
	    			rowstring +=out+"^";
	    			
	    		}
	    	
	    		examlist.add(rowstring);
	    		
	    	}
	    	
	    	return ' ';
		}
		@Override
		protected void onPostExecute(Character result)
		{
			if(!noExams)
			{
				ea = new ExamAdapter(parentAct, examlist);
				examlistview.setAdapter(ea);
				examlistview.setOnItemClickListener(ea);
			}
			else
			{
				TextView tv = new TextView(parentAct);
	    		tv.setText("      'Tis not the season for exams.\n                    Try back later!\n  (about 3 weeks before they begin)");
	    		tv.setTextSize(19);
	    		linlay.removeAllViews();
	    		linlay.addView(tv);
			}
			pb_ll.setVisibility(View.GONE);
			examlistview.setVisibility(View.VISIBLE);
		}
	}
	
	private class ExamAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener
	{
		
			private Context con;
			private ArrayList<String> exams;
			private LayoutInflater li;
			private String currentDate;
			boolean isSectionHeader;
			
			@SuppressWarnings("unchecked")
			public ExamAdapter(Context c, ArrayList<String> objects)
			{
				super(c,0,objects);
				con = c;
				exams = objects;
				li = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			public int getCount() {
				// TODO Auto-generated method stub
				return exams.size();
			}

			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return exams.get(position);
			}

			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public boolean areAllItemsEnabled()
			{
				return true;
			}
			@Override
			public boolean isEnabled(int i)
			{
				return true;
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
						
				String[] examdata = exams.get(position).split("\\^");
				String id = examdata[1];
				String name = examdata[2];
				String date = examdata[3];
				String location = examdata[4];
				
				String course = "";
				ViewGroup vg = (ViewGroup)convertView;
				vg =(ViewGroup)li.inflate(R.layout.exam_item_view,null,false);
				TextView courseview = (TextView) vg.findViewById(R.id.exam_item_header_text);
				
			/*	if(exams.get(position).contains("not the season for exam"))
				{
					TextView tv = new TextView(ExamScheduleActivity.this);
					tv.setTextSize(19);
					tv.setText(exams.get(position));
					vg.removeAllViews();
					vg.addView(tv);
					
					return (View)vg;
				}*/
				
				if(name.contains("The department"))
				{
					course = id;
					TextView left= (TextView) vg.findViewById(R.id.examdateview);
					left.setText(name);
				}
				else
				{
					course = id+" "+name;
					TextView left= (TextView) vg.findViewById(R.id.examdateview);
					left.setText(date);
					TextView right = (TextView) vg.findViewById(R.id.examlocview);
					right.setText(location);
				}
				
				courseview.setText(course);
				
				return (View)vg;
				
			}
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				mode = ExamScheduleFragment.this.parentAct.startActionMode(new ScheduleActionMode(position));
			}
			
			
			final class ScheduleActionMode implements ActionMode.Callback {
		        
				int position;
				
				public ScheduleActionMode(int pos)
				{
					position = pos;
				}
				
				@Override
		        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		            mode.setTitle("Exam Info");
		            MenuInflater inflater = getSherlockActivity().getSupportMenuInflater();
		            inflater.inflate(R.layout.schedule_menu, menu);
		            return true;
		        }

		        @Override
		        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		            return false;
		        }

		        @Override
		        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		            switch(item.getItemId())
		            {
		            	case R.id.locate_class:
		            		Intent map = new Intent(con.getString(R.string.building_intent), null, con, CampusMapActivity.class);
		    				
		    				String[] elements = exams.get(position).split("\\^");
		    				if(elements[2].contains("The department"))
		    				{
		    					return true;
		    				}
		    				else
		    				{	
		    					map.setData(Uri.parse(elements[4].split(" ")[0]));
		    					con.startActivity(map);
		    					return true;
		    				}
		            }
		            return true;
		        }

		        @Override
		        public void onDestroyActionMode(ActionMode mode) {
		        }
		    }
			
			
		}
}