package org.youth.sadhana.activities.intro;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.youth.sadhana.R;
import org.youth.sadhana.core.preferences.Preferences;

import butterknife.BindView;

/**
 * Created by Kamlesh on 09-09-2017.
 */

public class LoginFragment  extends Fragment {


    @BindView(R.id.tvFirstName)
    public EditText tvFirstName;

    @BindView(R.id.tvLastName)
    public EditText tvLastName;

    @BindView(R.id.mba_center)
    public EditText mbaCenter;
    /*@BindView(R.id.mba_center)
    Spinner mbaCenter;*/

    @BindView(R.id.mid)
    public EditText mahatmaId;

    @BindView(R.id.updateProfile)
    public Button btnSaveProfile;

    @BindView(R.id.cancelProfile)
    public Button cancelProfile;

    protected static boolean isFirstTime;

    protected static Preferences preferences;

    public static Preferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(Preferences preferences) {
        LoginFragment.preferences = preferences;
    }

    public static boolean isFirstTime() {
        return isFirstTime;
    }

    public static void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public LoginFragment() {
        System.out.println("Inside LoginFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean validate()
    {
        boolean isValidate = true;
        if(isAdded()) {

            String validationMsg = "This field should not be blank.";
            String firstName = ""+ tvFirstName.getText();
            if (firstName == null || firstName.trim().isEmpty())
            {
                tvFirstName.setError(validationMsg);
                isValidate = false;
            }

            String lastName = ""+ tvLastName.getText();
            if (lastName == null || lastName.trim().isEmpty())
            {
                //tvLastName.setError(res.getString(R.string.validation_mandatory));
                tvLastName.setError(validationMsg);
                isValidate = false;
            }


            String mbaCenterText = "" + mbaCenter.getText();
            if (mbaCenterText == null || mbaCenterText.trim().isEmpty())
            {
                mbaCenter.setError(validationMsg);
                isValidate = false;
            }

            String mid = ""+ mahatmaId.getText();
            if (mid == null || mid.trim().isEmpty())
            {
                mahatmaId.setError(validationMsg);
                isValidate = false;
            }
        }

/*
        if(isValidate) {

            int centerPositon = mbaCenter.getSelectedItemPosition();
            if (centerPositon == 0)
            {
                setSpinnerError(mbaCenter,res.getString(R.string.validation_mandatory));
                isValidate = false;
            }
        }
*/

        return isValidate;
    }


    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            //spinner.performClick(); // to open the spinner list if error is found.

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        tvFirstName = (EditText) v.findViewById(R.id.tvFirstName);
        tvLastName = (EditText) v.findViewById(R.id.tvLastName);
        mbaCenter = (EditText) v.findViewById(R.id.mba_center);
        mahatmaId = (EditText) v.findViewById(R.id.mid);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btnSaveProfile = (Button) v.findViewById(R.id.updateProfile);
        cancelProfile = (Button) v.findViewById(R.id.cancelProfile);
        if(isFirstTime) {
            btnSaveProfile.setText("Let's GO");
            Resources r = getResources();
            int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
            btnSaveProfile.setWidth(px);
            cancelProfile.setVisibility(View.GONE);
        } else {
            btnSaveProfile.setText("Update");
            cancelProfile.setVisibility(View.VISIBLE);
        }
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    preferences.setFirstName(String.valueOf(tvFirstName.getText()));
                    preferences.setLastName(String.valueOf(tvLastName.getText()));
                    preferences.setMBACenter(String.valueOf(mbaCenter.getText()));
                    preferences.setMahatmaId(String.valueOf(mahatmaId.getText()));
                    preferences.setFirstRun(false);
                    String message = "";
                    if(isFirstTime)
                        message = "Profile saved Successfully.";
                    else
                        message = "Profile Updated Successfully.";
                    if(isFirstTime) {
                        getActivity().finish();
                    } else {
                        Snackbar snackbar = Snackbar.make(getView(),message,Snackbar.LENGTH_INDEFINITE).setActionTextColor(getResources().getColor(R.color.black));
                        View sbView = snackbar.getView();
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getActivity().finish();
                            }
                        });
                        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                        snackbar.show();
                    }
                }
            }
        });
        cancelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

                if(preferences != null) {
            tvFirstName.setText(preferences.getFirstName());
            tvLastName.setText(preferences.getLastName());
            mbaCenter.setText(preferences.getMBACenter());
            mahatmaId.setText(preferences.getMahatmaId());
        }

        return v;
    }

    private void setDbMBACenter(String compareValue,Spinner mSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.mba_center, android.R.layout.select_dialog_singlechoice);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        mSpinner.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }
    }

}
