package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPS_Positioning;
import larc.ludiconprod.Utils.Location.ActivitiesLocationListener;
import larc.ludiconprod.Utils.Sport;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.UniqueIDCreator;
import larc.ludiconprod.Utils.util.Utils;

public class CreateNewActivity extends Activity implements OnMapReadyCallback {

    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;

    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ActivitiesLocationListener locationListener;
    private LocationManager lm;
    private GoogleMap m_gmap;

    private double latitude = 0;
    private double longitude = 0;
    private int isOfficial = 0;

    private String addressName = ""; //default address

    private List<String> sports = new ArrayList<String>() {{
        add("football");
        add("volleyball");
        add("basketball");
        add("squash");
        add("pingpong");
        add("tennis");
        add("cycling");
        add("jogging");
        add("gym");
        add("other");
        ;
    }};

    @Override
    public void onMapReady(GoogleMap map) {
        m_gmap = map;

        locationListener = new ActivitiesLocationListener(getApplication());
        locationListener.BindMap(map);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = lm.getBestProvider(criteria, true);

        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), GMapsFullActivity.class); //AskPreferences.class);
                        startActivityForResult(goToNextActivity, ASK_COORDS);
                    }
                });
            }
        });


    }

    private final CreateNewActivity myAct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide App bar
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_new);

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        // User picture and name for HEADER MENU
        Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");

        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()));
        userName.setTypeface(segoeui);

        TextView userSportsNumber = (TextView)findViewById(R.id.userSportsNumber);
        userSportsNumber.setTypeface(segoeui);

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                CreateNewActivity.this.startActivity(mainIntent);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Clear auto scroll
        final EditText editTextDesc = (EditText) findViewById(R.id.DescriptionInput);

        ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (editTextDesc.hasFocus()) {
                    editTextDesc.clearFocus();
                }
                return false;
            }
        });

        // DropDown for the sports

        /*
        Spinner spinner = (Spinner) findViewById(R.id.sports_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setSelection(0, true);
        View v = spinner.getSelectedView();
        ((TextView)v).setTextColor(Color.parseColor("#000000"));
        */

        selectedSportButton = (Button) findViewById(R.id.selectedSportButton);
        selectedSportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGridSportsDialog();
            }
        });

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("");

        // Events on buttons of Max players capacity

        final EditText maxCapacityET = (EditText) findViewById(R.id.maxPlayersET);

        Button removePeople = (Button) findViewById(R.id.removePeople);
        removePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currValue = Integer.parseInt(maxCapacityET.getText().toString());
                if (currValue >= 3)
                    currValue--;
                maxCapacityET.setText(currValue + "");
            }
        });

        //// Create event in header menu
        createEvent = (ImageButton)findViewById(R.id.header_button);
        createEvent.setVisibility(View.VISIBLE);
        createEvent.setBackgroundResource(R.drawable.save_button_2);
        createEvent.getLayoutParams().height =100;
        createEvent.getLayoutParams().width = 200 ;

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCreateEvent();

                SharedPreferences sharedPref = myAct.getSharedPreferences("LocationPrefs", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sel_latitude", null);
                editor.putString("sel_longitude", null);
                editor.commit();
            }
        });

        Button addPeople = (Button) findViewById(R.id.addPeople);
        addPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currValue = Integer.parseInt(maxCapacityET.getText().toString());
                if (currValue <= 50)
                    currValue++;
                maxCapacityET.setText(currValue + "");
            }
        });


        /// Date and Time holders

        editTextDateHolder = (EditText) findViewById(R.id.calendarHolderET);
        editTextTimeHolder = (EditText) findViewById(R.id.timeHolderET);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        String myFormat = "dd-MMM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        editTextDateHolder.setText(sdf.format(myCalendar.getTime()));
        editTextDateHolder.setFocusable(false);
        editTextDateHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(CreateNewActivity.this, R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        // Time picker
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        editTextTimeHolder.setText(hour + ":" + minute);
        editTextTimeHolder.setFocusable(false);
        editTextTimeHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateNewActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTextTimeHolder.setText(selectedHour + ":" + selectedMinute);
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    private ImageButton createEvent;

    private Button selectedSportButton;
    private ArrayList<Sport> sportsList = new ArrayList<Sport>();
    private MyCustomAdapter dataAdapter = null;

    private void showGridSportsDialog() {
        if (alertDialog == null) {
            // Prepare grid view
            gridView = new GridView(this);

            int count = 0;
            for (String sport : sports) {
                String uri = "@drawable/" + sport;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res1 = getResources().getDrawable(imageResource);
                sportsList.add(new Sport(sport, Integer.toString(count),
                        false, ((BitmapDrawable) res1).getBitmap()));
                count++;
            }

            //create an ArrayAdaptor from the Sport Array
            dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sportsList);

            // Assign adapter to ListView
            gridView.setAdapter(dataAdapter);
            gridView.setNumColumns(2);

            // Set grid view to alertDialog
            builder = new AlertDialog.Builder(this);
            builder.setView(gridView);
            //builder.setTitle("Pick your favourite sport");
            alertDialog = builder.show();

            //dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
        }

        alertDialog.show();
    }

    GridView gridView;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    EditText editTextDateHolder;
    EditText editTextTimeHolder;
    final Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {

        String myFormat = "dd/MMM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        editTextDateHolder.setText(sdf.format(myCalendar.getTime()));
    }


    /*
        public void onPrivacyButtonsClicked(View view) {
            // Is the button now selected
            TextView selected,notselected;

            switch(view.getId()) {
                case R.id.publicBut:
                    selected = (TextView)findViewById(R.id.publicBut);
                    selected.setAlpha(1);

                    notselected = (TextView)findViewById(R.id.privateBut);
                    notselected.setAlpha((float)0.7);
                    break;
                case R.id.privateBut:
                    selected = (TextView)findViewById(R.id.privateBut);
                    selected.setAlpha(1);

                    notselected = (TextView)findViewById(R.id.publicBut);
                    notselected.setAlpha((float)0.7);
                    break;
            }
        }
        */
    public boolean checkEventDateIsNotInPast(Date creationDate) {
        Date now = new Date();
        if (creationDate.before(now)) {
            Toast.makeText(this.getApplicationContext(), "You can't create an event in the past",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void OnCreateEvent() {
        try {


            Calendar calendar = myCalendar;

                /*new GregorianCalendar(myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH), // dono fucking why ?!
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE));
                */

            UniqueIDCreator idCreator = new UniqueIDCreator();
            final String id = idCreator.nextSessionId();

            final Map<String, Object> map = new HashMap<String, Object>();


            // Set date it will be played
            // TODO GMT format
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("English"));
            df.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));

            String gmtTime = df.format(calendar.getTime());
            final Date creationDate = calendar.getTime();


            if (checkEventDateIsNotInPast(creationDate))
                return;


            createEvent.setEnabled(false);
            createEvent.setClickable(false);
            //createEvent.setText("Creating ..");
            Toast.makeText(getApplicationContext(),"Creating..",Toast.LENGTH_LONG).show();

            ProgressBar pb = (ProgressBar) findViewById(R.id.marker_progress);
            pb.setVisibility(View.VISIBLE);

            map.put("date", DateManager.convertFromTextToSeconds(gmtTime));
            map.put("createdBy", User.uid);
            map.put("active", true);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(User.uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.getKey().equalsIgnoreCase("name")) {
                            map.put("creatorName", data.getValue().toString());
                            Log.v("CreatorName", data.getValue().toString());
                        }
                        if (data.getKey().equalsIgnoreCase("profileImageURL")) {
                            map.put("creatorImage", data.getValue().toString());
                            Log.v("CreatorImage", data.getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

            try {
                Thread.sleep(200, 1);
            } catch (InterruptedException exc) {
            }

            Log.v("Name", User.firstName + User.lastName);
            //map.put("date",  java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()));
            //Log.v("date", java.text.DateFormat.getDateTimeInstance().format(calendar.getTime()) );

            // Set privacy
        /*
        Button privacy = (Button) findViewById(R.id.publicBut);
        if (privacy.getAlpha() == 1) {
            map.put("privacy", "public");
        } else {
            map.put("privacy", "private");
        }
*/
            map.put("privacy", "public");

            // Set location
            final Map<String, Object> mapAux = new HashMap<>();

            // location not provided
            if (latitude == 0 && longitude == 0) {
                mapAux.put("latitude", GPS_Positioning.getLatLng().latitude);
                mapAux.put("longitude", GPS_Positioning.getLatLng().longitude);

                latitude = GPS_Positioning.getLatLng().latitude;
                longitude = GPS_Positioning.getLatLng().longitude;

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        if (addressName == null || addressName.equals("")) {
                            addressName = addresses.get(0).getAddressLine(0);
                        }
                    }
                } catch (Exception exc) {
                    addressName = "Unknown";
                }

                mapAux.put("name", addressName);

            } else {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        if (addressName == null || addressName.equals("")) {
                            addressName = addresses.get(0).getAddressLine(0);
                        }
                    }
                } catch (Exception exc) {
                    addressName = "Unknown";
                }

                mapAux.put("latitude", latitude);
                mapAux.put("longitude", longitude);
                mapAux.put("name", addressName);
            }
            // If the user will get points or not for the event
            map.put("isOfficial", isOfficial);

            map.put("place", mapAux);

            EditText number = (EditText) findViewById(R.id.maxPlayersET);
            int maxPlayers = Integer.parseInt(number.getText().toString());

            EditText editTextDesc = (EditText) findViewById(R.id.DescriptionInput);
            String description = editTextDesc.getText().toString();

            // Event extra info:
            map.put("roomCapacity", maxPlayers);
            map.put("priority", 0);
            map.put("description", description);
            map.put("message", null);

            // Set sport
            // TODO Get sport key
            String sportName = sports.get(sportIndex);
            //spinner.getSelectedItem().toString().toLowerCase().replaceAll("\\s+", "");
            map.put("sport", sportName);

            // Set users - currently only the one enrolled
            Map<String, Boolean> usersAttending = new HashMap<String, Boolean>();
            usersAttending.put(User.uid, true);
            map.put("users", usersAttending);

            // Check Events exists
            DatabaseReference newEventRef = User.firebaseRef.child("events"); // check user
            newEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) { // flush the database everything is crazy
                        //User.firebaseRef.child("mesg").setValue("World is on fire");

                        // Script to refresh sports in the database
                    /*
                    User.firebaseRef.child("sports").child("Football").child("id").setValue("0");
                    User.firebaseRef.child("sports").child("volleyball").child("id").setValue("1");
                    User.firebaseRef.child("sports").child("Basketball").child("id").setValue("2");
                    User.firebaseRef.child("sports").child("Chess").child("id").setValue("3");
                    User.firebaseRef.child("sports").child("Ping Pong").child("id").setValue("4");
                    User.firebaseRef.child("sports").child("Tennis").child("id").setValue("5");
                    User.firebaseRef.child("sports").child("Cycling").child("id").setValue("6");
                    User.firebaseRef.child("sports").child("Jogging").child("id").setValue("7");
                    */
                    }

                    // Get the events from Shared Prefs
                    String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("myEvents", null);
                    Type type = new TypeToken<ArrayList<Event>>() {
                    }.getType();
                    ArrayList<Event> myCurrentEvents = new Gson().fromJson(connectionsJSONString, type);

                    int numberOfEvents = 0;
                    boolean isSameDate = false;
                    for (Event event : myCurrentEvents) {
                        // This is not quite correct!!!:
                        if (event.date.getDay() == creationDate.getDay() && Math.abs(event.date.getHours() - creationDate.getHours()) <= 2) {
                            isSameDate = true;
                            break;
                        }
                        if (event.date.getDay() == creationDate.getDay()) numberOfEvents++;
                    }

                    if (isSameDate) {
                        Toast.makeText(getApplicationContext(), "You have scheduled an event at this date already ! Please check your agenda !", Toast.LENGTH_LONG).show();
                    } else if (numberOfEvents >= 3) {
                        Toast.makeText(getApplicationContext(), "You already reached the limit of 3 events on this day. Please pick another day !", Toast.LENGTH_LONG).show();
                    } else {

                        User.firebaseRef.child("events").child(id).setValue(map);
                        // Each user has an "events" field which has a list of event ids
                        Map<String, Object> ev = new HashMap<String, Object>();
                        Map<String, Object> inEv = new HashMap<>();
                        inEv.put("participation", true);
                        inEv.put("points", 0);
                        ev.put(id, inEv);
                        User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);

                        try {
                            if (lm != null)
                                lm.removeUpdates(locationListener);
                        } catch (SecurityException exc) {
                            exc.printStackTrace();
                        }

                        // Sanity checks
                        lm = null;
                        locationListener = null;

                        jumpToMainActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception exc) {
            Utils.quit();
        }
    }

    /**
     * Method that jumps to the MainActivity
     */
    public void jumpToMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class); //AskPreferences.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 2000); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }

    @Override
    public void onBackPressed() {

        try {
            lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        } catch (Exception ex) {
            // the location is not any more initialized - lm is null
        }

        // Sanity activities
        lm = null;
        locationListener = null;

        SharedPreferences sharedPref = myAct.getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sel_latitude", null);
        editor.putString("sel_longitude", null);
        editor.commit();

        finish();

        Intent toMain = new Intent(this, MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            isOfficial = data.getIntExtra("isOfficial", 0);
            addressName = data.getStringExtra("address");

            String comment = data.getStringExtra("comment");
            if (comment != null && !comment.equalsIgnoreCase("")) {
                Toast.makeText(getApplication(), comment, Toast.LENGTH_LONG).show();
            }

            // Set also the location name selected

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    if (addressName == null || addressName.equals("")) {
                        addressName = addresses.get(0).getAddressLine(0);
                    }
                }
            } catch (Exception exc) {
                addressName = "Unknown";
            }

            TextView locationHint = (TextView) findViewById(R.id.locationHint);
            locationHint.setText("Will play at : " + addressName);
            locationHint.setTextColor(Color.parseColor("#4f2f4f"));
        }

        try {
            if (lm != null)
                lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity checks
        lm = null;
        locationListener = null;

        LatLng latLng = new LatLng(latitude, longitude);

        m_gmap.clear();

        m_gmap.addMarker(new MarkerOptions()
                .position(latLng)
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.football))
                .title("This is your selected area"));
        m_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPref = getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("curr_latitude", null);
        editor.putString("curr_longitude", null);
        editor.commit();

    }

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_createnew);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, CreateNewActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), CreateNewActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int sportIndex = 0;

    private class MyCustomAdapter extends ArrayAdapter<Sport> {

        private ArrayList<Sport> sportsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Sport> sList) {
            super(context, textViewResourceId, sList);
            this.sportsList = new ArrayList<>();
            this.sportsList.addAll(sList);
        }

        private class ViewHolder {
            RelativeLayout rl;
            ImageView image;
            TextView text;
            CheckBox box;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sport_info, null);

                holder = new ViewHolder();
                holder.rl = (RelativeLayout) convertView.findViewById(R.id.irLayout);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.image = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        RelativeLayout rl = (RelativeLayout) cb.getParent();
                        sport.setSelected(cb.isChecked());

                        //sportsList = new ArrayList<Sport>();
                        sportIndex = Integer.parseInt(sport.id);
                        selectedSportButton.setBackground(new BitmapDrawable(getResources(), sport.icon));

                        alertDialog.dismiss();
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            //holder.text.setText("");
            holder.text.setText(sport.name);
            holder.box.setText("");
            //holder.box.setText(sport.name);
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);
            holder.text.setTextColor(getResources().getColor(R.color.white));
            /*
            if (sportIndex==position) {
                holder.rl.setBackgroundColor(getResources().getColor(R.color.bg1));
                holder.image.setImageBitmap(sport.icon);
                holder.box.setTextColor(getResources().getColor(R.color.white));
                holder.box.setAlpha((float) 0.9);
            } else {
            */
            holder.box.setTextColor(getResources().getColor(R.color.white));
            holder.rl.setBackgroundColor(getResources().getColor(R.color.bg2));
            holder.image.setImageBitmap(sport.icon);
            holder.box.setAlpha((float) 0.7);
            //  }

            return convertView;

        }
    }
}

