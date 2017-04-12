package acme.cb2;
import android.annotation.SuppressLint;
import android.content.pm.*;
import android.graphics.*;
import android.media.*;
import android.provider.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import p.*;
import p.Main.*;
import q.Colors;

import static java.lang.Math.min;
import static p.Main.*;
import static p.IO.*;
public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener, Observer {
    void setupAudio() {
        ((Audio.Factory.FactoryImpl.AndroidAudio)Audio.audio).setCallback(new Consumer<Audio.Sound>() {
            @Override
            public void accept(Audio.Sound sound) {
                Integer id=id(sound);
                main.l.info("playing sound: "+sound+", id: "+id);
                if(id!=null) {
                    mediaPlayer=MediaPlayer.create(FullscreenActivity.this,id);
                    mediaPlayer.start();
                } else
                    main.l.warning("id for sound: "+sound+" is null!");
            }
            Integer id(Audio.Sound sound) {
                switch(sound) {

                    case electronic_chime_kevangc_495939803:
                        return R.raw.electronic_chime_kevangc_495939803;
                    case glass_ping_go445_1207030150:
                        return R.raw.glass_ping_go445_1207030150;
                    case store_door_chime_mike_koenig_570742973:
                        return R.raw.store_door_chime_mike_koenig_570742973;
                    default:
                        main.l.warning(""+" "+"default where!");
                        return null;
                }
            }
        });
    }
    private Button getButton(int size,String string,float fontsize,int rows,int columns,int i,int x,int y) {
        return getButton(size,size,string,fontsize,rows,columns,i,x,y);
    }
    private Button getButton(int width,int depth,String string,float fontsize,int rows,int columns,int i,int x,int y) {
        Button button;
        RelativeLayout.LayoutParams params;
        button=new Button(this);
        //button.setId(model.buttons+i); // id is index!
        button.setTextSize(fontsize/4);
        button.setGravity(Gravity.CENTER);
        params=new RelativeLayout.LayoutParams(width,depth);
        params.leftMargin=x;
        params.topMargin=y;
        //p("other: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
        button.setLayoutParams(params);
        button.setText(string);
        //button.setBackgroundColor(colors.aColor(colors.whiteOn));
        button.setOnClickListener(this);
        return button;
    }
    View createButtons() {
        final int rows=colors.rows,columns=colors.columns,n=colors.n;
        RelativeLayout relativeLayout=new RelativeLayout(this);
        RelativeLayout.LayoutParams params=null;
        buttons=new Button[n+/*for ip address*/1]; // colors is intimatley tied to the mark 1 model!
        final int x0=size/4, y0=size/4;
        for(int i=0;i<rows*columns;i++) {
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize(fontSize);
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            if(i/columns%2==0)
                button.setText(""+(char)('0'+(i+1)));
            button.setBackgroundColor(colors.aColor(i,false));
            button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
        }
        int i=rows*columns;
        { // reset
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize(fontSize);
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            button.setText("R");
            button.setBackgroundColor(colors.aColor(i,false));
            button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
        }
        i++;
        { // extra buton for inet address
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize((int)Math.round(fontSize/4.));
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            if(i/columns%2==0)
                button.setText(""+(char)('0'+(i+1)));
            button.setBackgroundColor(colors.aColor(i,false));
            //button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[n].setText(main.myInetAddress!=null?main.myInetAddress.toString():"null");
                        }
                    });
                }
            },0,1_000);
        }
        relativeLayout.setBackgroundColor(Color.BLACK);
        return relativeLayout;
    }
    @Override
    public void onClick(final View v) {
        p("click on: "+v);
        new Thread(new Runnable() {
            @Override
            public void run() {
                p("enter run() for click on: "+v);
                Et et=new Et();
                main.instance().click(v.getId());
                p("exit run() for click on: "+v+" after: "+et);
            }
        },"click #"+clicks++).start();
    }
    @Override
    public void update(Observable observable,Object hint) {
        p(observable+" "+hint);
        if(observable instanceof Model) {
            if(observable==main.model) {
                p("received update from observable"+observable+" with hint: "+hint);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(Integer buttonId=1;buttonId<=main.model.buttons;buttonId++) {
                            //setButtonState(buttonId,main.model.state(buttonId));
                            buttons[buttonId-1].setBackgroundColor(colors.aColor(buttonId-1,main.model.state(buttonId)));
                        }
                    }
                });
            } else
                p(observable+" is not our model!");
        } else
            p(observable+" is not a model!");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // nexus 5 wants to be in ptp mode
        p("on create thread: "+Thread.currentThread().getName());
        String androidId=Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        p("androidId: "+androidId);
        Properties properties=properties(new File(getExternalFilesDir(null),propertiesFilename));
        p("properties: "+properties);
        Logger logger=Logger.getLogger("xyzzy");
        logger.setLevel(Level.ALL);
        File logFileDirectory=new File(getExternalFilesDir(null),IO.logFileDirectory);
        addFileHandler(logger,logFileDirectory,androidId);
        try {
            String captivePortalDetectionEnabled="captive_portal_detection_enabled";
            int result=Settings.Global.getInt(getContentResolver(),captivePortalDetectionEnabled);
            p("captivePortalDetectionEnabled="+result);
        } catch(Exception e) {
            p("caught: "+e);
        }
        // one id is" fa37f2329a84e09d
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fullscreen);
        mVisible=true;
        mControlsView=findViewById(R.id.fullscreen_content_controls);
        mContentView=findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        try {
            Enumeration<NetworkInterface> netowrkInterfaces=NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface networkInterface : Collections.list(netowrkInterfaces))
                p("interface: "+networkInterface.getName()+" "+networkInterface.getInterfaceAddresses());
        } catch(SocketException e) {
            p("getNetworkInterfaces() caught: "+e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //int first=101, tabletsInGroup=32;
        int first=100, tabletsInGroup=20; // make compatible with old static ip addresses
        Main.Group group=new Group(first,first+tabletsInGroup-1,false);
        /*
        Logger global=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        LoggingHandler.init();
        LoggingHandler.setLevel(Level.WARNING);
        File logFileDirectory=getFilesDir();
        LoggingHandler.addFileHandler(l,logFileDirectory,"tablet");
        pl("android id: "+androidId);
        //LoggingHandler.toggleSockethandlers(); // looks like i need to wait for this?
        // yes, whould wait until wifi is up
        */
        main=new Main(properties,logger,group,Model.mark1);
        // sublcass and override loop adding call to try to connect wifi
        setupAudio();
        main.sleep=20_000;
        mainThread=new Thread(main,"rabbit2 main");
        mainThread.start();
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        System.out.println(metrics);
        size=(int)Math.round(metrics.heightPixels*.25); // size of a large square button
        p("size: "+size);
        fontSize=(int)Math.round(metrics.heightPixels*.06);
        p("font size: "+fontSize);
        Point point=new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        p("real size is: "+point);
        width=point.x;
        depth=point.y;
        View view=createButtons();
        setContentView(view);
        main.model.addObserver(this);
        if(false) {
            view=mContentView;
            p("view is: "+view);
            p("view is: "+view.getWidth()+'x'+view.getHeight());
            p("view is: "+view.getClipBounds());
            Button button=new Button(this);
            button.setText("foo");
            int size=100;
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(size,size);
            //layoutParams.leftMargin=(int)(x0+i%columns*1.2*size);
            //layoutParams.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(layoutParams);
            ((LinearLayout)view).addView(button);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }
    private void toggle() {
        if(mVisible) {
            hide();
        } else {
            show();
        }
    }
    private void hide() {
        // Hide UI first
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible=false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable,UI_ANIMATION_DELAY);
    }
    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible=true;
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable,UI_ANIMATION_DELAY);
    }
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable,delayMillis);
    }
    MediaPlayer mediaPlayer;
    Main main;
    Thread mainThread;
    int clicks;
    int width, depth, size;
    float fontSize;
    final Colors colors=new Colors();
    Button[] buttons;
    //boolean[] states;
    private static final boolean AUTO_HIDE=true;
    private static final int AUTO_HIDE_DELAY_MILLIS=3000;
    private static final int UI_ANIMATION_DELAY=300;
    private final Handler mHideHandler=new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable=new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable=new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar=getSupportActionBar();
            if(actionBar!=null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable=new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view,MotionEvent motionEvent) {
            if(AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
}
