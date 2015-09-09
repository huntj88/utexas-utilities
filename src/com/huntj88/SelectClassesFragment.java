package com.huntj88;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nasageek.utexasutilities.R;

public class SelectClassesFragment extends Fragment implements View.OnClickListener{

    private SelectClassesListener mListener;
    private EditText searchBoxDepartment,searchBoxCourseNum;
    private TextView[] typedClasses = new TextView[6];
    private Button select;

    public SelectClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_select_classes, container, false);

        searchBoxDepartment = (EditText) v.findViewById(R.id.search_box_department);
        searchBoxCourseNum = (EditText) v.findViewById(R.id.search_box_course_num);
        typedClasses[0] = (TextView) v.findViewById(R.id.textView);
        typedClasses[1] = (TextView) v.findViewById(R.id.textView2);
        typedClasses[2] = (TextView) v.findViewById(R.id.textView3);
        typedClasses[3] = (TextView) v.findViewById(R.id.textView4);
        typedClasses[4] = (TextView) v.findViewById(R.id.textView5);
        typedClasses[5] = (TextView) v.findViewById(R.id.textView6);
        select = (Button) v.findViewById(R.id.button);
        select.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SelectClassesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void test(int textViewId,String string)
    {
        typedClasses[textViewId].setText(string);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        mListener.selectedAClass(searchBoxDepartment.getText().toString(),searchBoxCourseNum.getText().toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SelectClassesListener {
        void selectedAClass(String department,String course);
    }

}
