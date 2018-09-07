package larc.ludiconprod.Utils.MyProfileUtils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;

import larc.ludiconprod.Activities.EditProfileActivity;
import larc.ludiconprod.Activities.IntroActivity;
import larc.ludiconprod.Controller.ImagePicker;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;

import static android.R.color.transparent;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileTab2 extends Fragment {
    private RadioButton male;
    private RadioButton female;
    private RadioGroup sexSwitch;

    private EditText ageTextView;
    private RelativeLayout passwordLayout;
    private TextWatcher textWatcher;

    private RadioGroup editEventReview;
    private RadioGroup editLocationReview;
    private RadioGroup editUsersReview;
    private RadioButton editWithEvent;
    private RadioButton editWithoutEvent;
    private RadioButton editWithLocation;
    private RadioButton editWithoutLocation;
    private RadioButton editWithUser;
    private RadioButton editWithoutUser;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.edittab2, container, false);

        try {
            final EditProfileActivity epa = (EditProfileActivity) super.getActivity();
            final Button save = (Button) v.findViewById(R.id.saveChangesButton);
            User u = Persistance.getInstance().getProfileInfo(super.getActivity());

            this.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            };

            AssetManager assets = inflater.getContext().getAssets();// Is this the right asset?
            Typeface typeFace= Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(assets,"fonts/Quicksand-Bold.ttf");

            EditText firstName = (EditText) v.findViewById(R.id.editFirstName);
            firstName.setTypeface(typeFace);
            EditText lastName = (EditText) v.findViewById(R.id.editLastName);
            lastName.setTypeface(typeFace);
            male = (RadioButton) v.findViewById(R.id.editMale);
            female = (RadioButton) v.findViewById(R.id.editFemale);
            sexSwitch = (RadioGroup) v.findViewById(R.id.editSexSwitch);
            ageTextView = (EditText) v.findViewById(R.id.editDate);
            ageTextView.setTypeface(typeFace);
            Button changePassword = (Button) v.findViewById(R.id.editPasswordButton);
            changePassword.setTypeface(typeFaceBold);
            save.setTypeface(typeFaceBold);
            passwordLayout = (RelativeLayout) v.findViewById(R.id.editPasswordLayout);
            final EditText oldPass = (EditText) v.findViewById(R.id.oldPassword);
            oldPass.setTypeface(typeFace);
            final EditText newPass = (EditText) v.findViewById(R.id.newPassword);
            newPass.setTypeface(typeFace);
            final EditText rePass = (EditText) v.findViewById(R.id.editPasswordRepeat);
            rePass.setTypeface(typeFace);
            TextView email = (TextView) v.findViewById(R.id.emailLabel);
            email.setText(u.email);
            email.setTypeface(typeFace);
            ImageView image = (ImageView) v.findViewById(R.id.editImage);
            editEventReview = (RadioGroup) v.findViewById(R.id.editEventReview);
            editLocationReview = (RadioGroup) v.findViewById(R.id.editLocationReview);
            editUsersReview = (RadioGroup) v.findViewById(R.id.editUsersReview);
            editWithEvent = (RadioButton) v.findViewById(R.id.editWithEvent);
            editWithoutEvent = (RadioButton) v.findViewById(R.id.editWithoutEvent);
            editWithLocation = (RadioButton) v.findViewById(R.id.editWithLocation);
            editWithoutLocation = (RadioButton) v.findViewById(R.id.editWithoutLocation);
            editWithUser = (RadioButton) v.findViewById(R.id.editWithUser);
            editWithoutUser = (RadioButton) v.findViewById(R.id.editWithoutUser);


            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            epa.setFirstName(firstName);
            epa.setLastName(lastName);
            epa.setDate(ageTextView);
            epa.setNewPassword(newPass);
            epa.setOldPassword(oldPass);
            epa.setRepeatPassword(rePass);
            save.setOnClickListener(epa);
            epa.setImage(image);

            male.setTypeface(typeFace);
            female.setTypeface(typeFace);

            firstName.setText(u.firstName);
            lastName.setText(u.lastName);
            this.ageTextView.setText("" + u.age);

            Log.d("Epa gender", u.firstName + u.gender);

            if(u.gender.equals("0")){
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
                epa.setSex(0);
            } else if(u.gender.equals("1")) {
                female.setChecked(true);
                female.setTextColor(Color.parseColor("#ffffff"));
                epa.setSex(1);
            }

            if(male.isChecked()) {
                male.setBackgroundResource(R.drawable.toggle_male);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else{
                female.setBackgroundResource(R.drawable.toggle_female);
                male.setTextColor(Color.parseColor("#ffffff"));
            }

            sexSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(male.isChecked()){
                        male.setBackgroundResource(R.drawable.toggle_male);
                        female.setBackgroundResource(transparent);
                        male.setTextColor(Color.parseColor("#ffffff"));
                        female.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setSex(0);;
                    }
                    else{
                        female.setBackgroundResource(R.drawable.toggle_female);
                        male.setBackgroundResource(transparent);
                        male.setTextColor(Color.parseColor("#1A0c3855"));
                        female.setTextColor(Color.parseColor("#ffffff"));
                        epa.setSex(1);
                    }

                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            });

            if (u.rateEvent.equals("true")){
                editWithEvent.setChecked(true);
                editWithEvent.setTextColor(Color.parseColor("#ffffff"));
                epa.setEventReview("true");
            }else if(u.rateEvent.equals("false")){
                editWithoutEvent.setChecked(true);
                editWithoutEvent.setTextColor(Color.parseColor("#ffffff"));
                epa.setEventReview("false");
            }

            if(editWithEvent.isChecked()) {
                editWithEvent.setBackgroundResource(R.drawable.pink_button_selector);
                editWithEvent.setTextColor(Color.parseColor("#ffffff"));
            } else{
                editWithoutEvent.setBackgroundResource(R.drawable.green_button_selected);
                editWithEvent.setTextColor(Color.parseColor("#ffffff"));
            }

            editEventReview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(editWithEvent.isChecked()){
                        editWithEvent.setBackgroundResource(R.drawable.pink_button_selector);
                        editWithoutEvent.setBackgroundResource(transparent);
                        editWithEvent.setTextColor(Color.parseColor("#ffffff"));
                        editWithoutEvent.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setEventReview("true");
                    }
                    else{
                        editWithoutEvent.setBackgroundResource(R.drawable.green_button_selected);
                        editWithEvent.setBackgroundResource(transparent);
                        editWithEvent.setTextColor(Color.parseColor("#1A0c3855"));
                        editWithoutEvent.setTextColor(Color.parseColor("#ffffff"));
                        epa.setEventReview("false");
                    }

                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }

                }
            });

            if (u.rateLocation.equals("true")){
                editWithLocation.setChecked(true);
                editWithLocation.setTextColor(Color.parseColor("#ffffff"));
                epa.setLocationReview("true");
            }else if (u.rateLocation.equals("false")){
                editWithoutLocation.setChecked(true);
                editWithoutLocation.setTextColor(Color.parseColor("#ffffff"));
                epa.setLocationReview("false");
            }

            if(editWithLocation.isChecked()) {
                editWithLocation.setBackgroundResource(R.drawable.pink_button_selector);
                editWithLocation.setTextColor(Color.parseColor("#ffffff"));
            } else{
                editWithoutLocation.setBackgroundResource(R.drawable.green_button_selected);
                editWithLocation.setTextColor(Color.parseColor("#ffffff"));
            }

            editLocationReview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(editWithLocation.isChecked()){
                        editWithLocation.setBackgroundResource(R.drawable.pink_button_selector);
                        editWithoutLocation.setBackgroundResource(transparent);
                        editWithLocation.setTextColor(Color.parseColor("#ffffff"));
                        editWithoutLocation.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setLocationReview("true");
                    }
                    else{
                        editWithoutLocation.setBackgroundResource(R.drawable.green_button_selected);
                        editWithLocation.setBackgroundResource(transparent);
                        editWithLocation.setTextColor(Color.parseColor("#1A0c3855"));
                        editWithoutLocation.setTextColor(Color.parseColor("#ffffff"));
                        epa.setLocationReview("false");
                    }

                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }

                }
            });

            if (u.rateUsers.equals("true")){
                editWithUser.setChecked(true);
                editWithUser.setTextColor(Color.parseColor("#ffffff"));
                epa.setUsersReview("true");
            }else if (u.rateUsers.equals("false")) {
                editWithoutUser.setChecked(true);
                editWithoutUser.setTextColor(Color.parseColor("#ffffff"));
                epa.setUsersReview("false");
            }

            if(editWithUser.isChecked()) {
                editWithUser.setBackgroundResource(R.drawable.pink_button_selector);
                editWithUser.setTextColor(Color.parseColor("#ffffff"));
            } else{
                editWithoutUser.setBackgroundResource(R.drawable.green_button_selected);
                editWithUser.setTextColor(Color.parseColor("#ffffff"));
            }

            editUsersReview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(editWithUser.isChecked()){
                        editWithUser.setBackgroundResource(R.drawable.pink_button_selector);
                        editWithoutUser.setBackgroundResource(transparent);
                        editWithUser.setTextColor(Color.parseColor("#ffffff"));
                        editWithoutUser.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setUsersReview("true");
                    }
                    else{
                        editWithoutUser.setBackgroundResource(R.drawable.green_button_selected);
                        editWithUser.setBackgroundResource(transparent);
                        editWithUser.setTextColor(Color.parseColor("#1A0c3855"));
                        editWithoutUser.setTextColor(Color.parseColor("#ffffff"));
                        epa.setUsersReview("false");
                    }

                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }

                }
            });

            if (u.facebookId == null || u.facebookId.isEmpty()) {
                changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewGroup.LayoutParams params = passwordLayout.getLayoutParams();
                        if (params.height == 0) {
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            passwordLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                @Override
                                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                                    passwordLayout.removeOnLayoutChangeListener(this);
                                    ScrollView sv = (ScrollView) v.findViewById(R.id.editScroll);
                                    sv.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        } else {
                            params.height = 0;
                            newPass.setText("");
                            oldPass.setText("");
                            rePass.setText("");
                        }
                        passwordLayout.setLayoutParams(params);
                    }
                });
            } else {
                changePassword.getLayoutParams().width = 0;
                changePassword.setLayoutParams(changePassword.getLayoutParams());
            }

            ImageView editChoosePhoto = (ImageView) v.findViewById(R.id.editChoosePhoto);
            editChoosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                    startActivityForResult(chooseImageIntent, EditProfileActivity.PICK_IMAGE_ID);
                }
            });

            firstName.addTextChangedListener(this.textWatcher);
            lastName.addTextChangedListener(this.textWatcher);
            ageTextView.addTextChangedListener(this.textWatcher);
            oldPass.addTextChangedListener(this.textWatcher);
            newPass.addTextChangedListener(this.textWatcher);
            rePass.addTextChangedListener(this.textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}