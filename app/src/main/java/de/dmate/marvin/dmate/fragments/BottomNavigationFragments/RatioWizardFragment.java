package de.dmate.marvin.dmate.fragments.BottomNavigationFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dmate.marvin.dmate.R;

public class RatioWizardFragment extends Fragment {
    private OnRatioWizardFragmentInteractionListener mListener;

    public RatioWizardFragment() {
        // Required empty public constructor
    }

    public static RatioWizardFragment newInstance() {
        RatioWizardFragment fragment = new RatioWizardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ratio_wizard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRatioWizardFragmentInteractionListener) {
            mListener = (OnRatioWizardFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRatioWizardFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRatioWizardFragmentInteractionListener {

    }
}
