package com.example.admin.quran;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Bundle bundle;
    int currentAyah;
    TextView surahname;
    ImageView play;
    ImageView replay;
    ImageView next;
    ImageView back;
    Utils utils=new Utils();
    private List<Surah> datalist= new GetDatabase().getdata();
    boolean isexistt=true;
    SeekBar seekBar;
    TextView currentTime;
    TextView lenghtTime;
    Thread thread;
    boolean isreplay = false;
    int isfromfavorite;
    ImageView favorite;
    SQL_Lite_DB sql_lite_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        sql_lite_db = new SQL_Lite_DB(this);
        bundle= getIntent().getExtras();
        currentAyah =bundle.getInt("index");
        mediaPlayer = new MediaPlayer();


        surahname = (TextView)findViewById(R.id.surahname);
        play = (ImageView) findViewById(R.id.play);
        next = (ImageView) findViewById(R.id.next);
        back = (ImageView) findViewById(R.id.back);
        seekBar = (SeekBar) findViewById(R.id.seekb);
        currentTime = (TextView) findViewById(R.id.currenttime);
        lenghtTime = (TextView) findViewById(R.id.lenghttime);
        replay = (ImageView) findViewById(R.id.replaysurah);
        favorite = (ImageView) findViewById(R.id.favoritesurah);

        seekBar.setMax(mediaPlayer.getDuration());

        isfromfavorite = sql_lite_db.get_check_List_Favorite(datalist.get(currentAyah).getName());

        if(isfromfavorite>0){
            favorite.setImageResource(R.drawable.favorite1);
        }else {
            favorite.setImageResource(R.drawable.favorite);
        }

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isfromfavorite>0){
                    sql_lite_db.deleteData(datalist.get(currentAyah).getName());
                    favorite.setImageResource(R.drawable.favorite);
                }
                else {
                    sql_lite_db.insert_Data(datalist.get(currentAyah).getName(),datalist.get(currentAyah).getFilename(),
                            datalist.get(currentAyah).getUrl(),datalist.get(currentAyah).getStand());
                    favorite.setImageResource(R.drawable.favorite1);
                }
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(utils.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if(isreplay){
            replay.setImageResource(R.drawable.replay);
        }else {
            replay.setImageResource(R.drawable.replay1);
        }

        playSong(currentAyah);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isreplay){
                    isreplay= false;
                    replay.setImageResource(R.drawable.replay);
                }else {
                    isreplay= true;
                    replay.setImageResource(R.drawable.replay1);
                }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                    updateSeekThread();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentAyah<datalist.size()-1){
                    currentAyah++;
                    playSong(currentAyah);
                }
                else if(currentAyah==datalist.size()-1){
                    currentAyah=0;
                    playSong(currentAyah);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentAyah>0){
                    currentAyah--;
                    playSong(currentAyah);

                }
                else if (currentAyah==0){
                    currentAyah=datalist.size()-1;
                    playSong(currentAyah);

                }
            }
        });
        oncompletion();

    }

    public boolean isIsexistt() {
        isexistt=checkFileIsExist(datalist.get(currentAyah).getFilename()+".mp3");
        return isexistt;
    }

    public void playSong(int current)  {
        mediaPlayer.reset();
            try {
                if(isIsexistt()){
                    String root=Environment.getExternalStorageDirectory()+ "/quran-saad-elghamidi/"+datalist.get(0).getFilename()+".mp3";
                    mediaPlayer.setDataSource(root);
                }else {
                    mediaPlayer.setDataSource(String.valueOf(Uri.parse(datalist.get(current).getUrl())));
            }
            } catch (IOException e) {
                e.printStackTrace();
            }


        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause);
        surahname.setText(datalist.get(current).getName());
        updateSeekThread();

    }

    public boolean checkFileIsExist(String path){
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "quran-saad-elghamidi");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file=new File(folder,path);
        if(file.exists()){
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
        thread.interrupt();
        thread=null;
    }
    public void updateSeekThread(){
        thread = new Thread(){
            @Override
            public void run() {
                super.run();

                while (mediaPlayer!=null && mediaPlayer.isPlaying() && mediaPlayer!=null){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setMax(mediaPlayer.getDuration());
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            currentTime.setText(utils.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                            lenghtTime.setText(utils.milliSecondsToTimer(mediaPlayer.getDuration()));
                        }
                    });
                }
            }
        };
        thread.start();
    }
    public void oncompletion(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isreplay){
                    playSong(currentAyah);
                }else {
                    if(currentAyah<datalist.size()-1){
                        currentAyah++;
                        playSong(currentAyah);
                    }else {
                        currentAyah=0;
                        playSong(currentAyah);
                    }
                }
            }
        });
    }
}
