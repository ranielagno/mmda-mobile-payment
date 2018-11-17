package mmda.mmdamobilepayment.violation;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;

import mmda.mmdamobilepayment.data.model.Violation;
import mmda.mmdamobilepayment.drawer.NavigationDrawerActivity;
import mmda.mmdamobilepayment.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViolationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViolationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViolationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static Fragment newInstance() {
        ViolationFragment fragment = new ViolationFragment();
        return fragment;
    }

    public ViolationFragment() {
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
        return inflater.inflate(R.layout.fragment_violation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.setVerticalScrollBarEnabled(false);

        ListView listView = view.findViewById(R.id.violationListView);

        ArrayList<Violation> violations =
                ((NavigationDrawerActivity) view.getContext()).getUnPaidViolations();

        if (violations != null && !violations.isEmpty()) {
            Collections.sort(violations);
            ViolationAdapter adapter = new ViolationAdapter(view.getContext(), violations);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.GONE);
            view.findViewById(R.id.message).setVisibility(View.VISIBLE);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        /*else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
