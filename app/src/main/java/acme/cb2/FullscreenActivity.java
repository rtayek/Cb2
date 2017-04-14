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
    boolean menuItem(MenuItem item) {
        try {
            main.l.info("item: "+item);
            int id=item.getItemId();
            if(Enums.MenuItem.isItem(id))
                if(Enums.MenuItem.item(id).equals(Enums.MenuItem.Quit)) {
                    main.l.warning("quitting.");
                    //areWeQuitting=true;
                } else {
                    if(Enums.MenuItem.values()[id].equals(Enums.MenuItem.toggleExtraStatus))
                        ; //setStatusVisibility(status[0].getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
                    else
                        Enums.MenuItem.doItem(id,main);
                    return true;
                }
            else if(Enums.LevelSubMenuItem.isItem(id-Enums.MenuItem.values().length)) {
                Enums.LevelSubMenuItem.doItem(id-Enums.MenuItem.values().length); // hack!
                return true;
            } else
                main.l.severe(item+" is not a tablet meun item!");
        } catch(Exception e) {
            main.l.severe("menut item: "+item+", caught: "+e);
        }
        return false;
        /*
        else if(id==Enums.MenuItem.values().length) { // some hack for restarting tablet?
            // wtf was i doing here?
            Intent i=mainActivity.getBaseContext().getPackageManager().getLaunchIntentForPackage(mainActivity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainActivity.startActivity(i);
        }
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        main.l.info("on create options menu");
        super.onCreateOptionsMenu(menu);
        for(Enums.MenuItem menuItem : Enums.MenuItem.values())
            if(menuItem!=Enums.MenuItem.Level)
                menu.add(Menu.NONE,menuItem.ordinal(),Menu.NONE,menuItem.name());
        menu.add(Menu.NONE,Enums.MenuItem.values().length,Menu.NONE,"Restart");
        SubMenu subMenu=menu.addSubMenu(Menu.NONE,99,Menu.NONE,"Level");
        for(Enums.LevelSubMenuItem levelSubMenuItem : Enums.LevelSubMenuItem.values())
            subMenu.add(Menu.NONE,Enums.MenuItem.values().length+levelSubMenuItem.ordinal()/*hack!*/,Menu.NONE,levelSubMenuItem.name());
        return true;
    }
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        main.l.info("in options menu closed");
        super.onOptionsMenuClosed(menu);
        main.l.info("after super on options menu closed");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean rc=menuItem(item);
        if(!rc) {
            main.l.info("calling super on otions item selected");
            rc=super.onOptionsItemSelected(item);
        }
        return rc;
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
        final int rows=colors.rows, columns=colors.columns, n=colors.n;
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
            specialButtonIndex=i;
            p("special button index: "+specialButtonIndex);
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
            button.setOnClickListener(this);
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
        Integer index=v.getId()-1;
        p("click on: "+index);
        Integer id=index+1;
        p("id: "+id);
        if(1<=id&&id<=main.model.buttons) {
            p("it's a model button.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    main.instance().click(v.getId());
                }
            },"click #"+clicks++).start();
        } else if(index.equals(specialButtonIndex)) {
            p("we clicked on on the special button.");
            openOptionsMenu();
        } else {
            p("strange button index: "+index);
        }
    }
    public void update(Observable observable,Object hint) {
        p(observable+" "+hint);
        if(observable instanceof Model) {
            if(observable==main.model) {
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
                main.l.severe(observable+" is not our model!");
        } else
            main.l.severe(observable+" is not a model!");
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
        main=new Main(properties,logger,group,Model.mark1);
        setupAudio();
        main.sleep=20_000;
        mainThread=new Thread(main,"rabbit2 main");
        mainThread.start();
        Point point=new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        p("real window size is: "+point);
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        System.out.println(metrics);
        size=(int)Math.round(metrics.heightPixels*.25); // size of a large square button
        p("size: "+size);
        fontSize=(int)Math.round(metrics.heightPixels*.06);
        p("font size: "+fontSize);
        width=point.x;
        depth=point.y;
        View view=createButtons();
        setContentView(view);
        main.model.addObserver(this);
    }
    MediaPlayer mediaPlayer;
    Main main;
    Thread mainThread;
    int clicks;
    int width, depth, size;
    float fontSize;
    final Colors colors=new Colors();
    Button[] buttons;
    Integer specialButtonIndex=null;
    //boolean[] states;
}
