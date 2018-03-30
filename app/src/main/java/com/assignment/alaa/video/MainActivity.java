package com.assignment.alaa.video;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.MediaController;
import android.net.Uri;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaMetadataRetriever mediaMetadataRetriever;
    MediaController myMediaController;
    VideoView myVideoView;
    String viewSource="/sdcard/videoplayback.mp4";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(viewSource);

        myVideoView = (VideoView) findViewById(R.id.videoview);
        myVideoView.setVideoURI(Uri.parse(viewSource));
        myMediaController = new MediaController(this);
        myVideoView.setMediaController(myMediaController);
        myMediaController.setAnchorView(myVideoView);
        myVideoView.setOnCompletionListener(myVideoViewCompletionListener);
        myVideoView.setOnPreparedListener(MyVideoViewPreparedListener);
        myVideoView.setOnErrorListener(myVideoViewErrorListener);

        myVideoView.requestFocus();
        myVideoView.start();


            Button buttonCapture = (Button)findViewById(R.id.capture);
        buttonCapture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                int currentPosition = myVideoView.getCurrentPosition();
                Toast.makeText(MainActivity.this,
                        "Current Position: " + currentPosition + " (ms)",
                        Toast.LENGTH_LONG).show();

                Bitmap bmFrame = mediaMetadataRetriever
                        .getFrameAtTime(currentPosition * 1000);

                if(bmFrame == null){
                    Toast.makeText(MainActivity.this,
                            "bmFrame == null!",
                            Toast.LENGTH_LONG).show();
                }else{
                    AlertDialog.Builder myCaptureDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    ImageView capturedImageView = new ImageView(MainActivity.this);
                    capturedImageView.setImageBitmap(bmFrame);
                    LayoutParams capturedImageViewLayoutParams =
                            new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                    capturedImageView.setLayoutParams(capturedImageViewLayoutParams);

                    myCaptureDialog.setView(capturedImageView);
                    myCaptureDialog.show();




                }

            }});
    }

    MediaPlayer.OnCompletionListener myVideoViewCompletionListener =
            new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    Toast.makeText(MainActivity.this, "End of Video",
                            Toast.LENGTH_LONG).show();
                }
            };

    MediaPlayer.OnPreparedListener MyVideoViewPreparedListener =
            new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    long duration = myVideoView.getDuration();
                    Toast.makeText(MainActivity.this,
                            "Duration: " + duration + " (ms)",
                            Toast.LENGTH_LONG).show();

                    myMediaController.setAnchorView(myVideoView);

                }
            };

    MediaPlayer.OnErrorListener myVideoViewErrorListener =
            new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    Toast.makeText(MainActivity.this,
                            "Error!!!",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            };

    public void buDispalyFrame(View v){

        int currentPosition = myVideoView.getCurrentPosition();
        Toast.makeText(MainActivity.this,
                "Current Position: " + currentPosition + " (ms)",
                Toast.LENGTH_LONG).show();

        Bitmap bmFrame = mediaMetadataRetriever
                .getFrameAtTime(currentPosition * 1000);
        if(bmFrame == null){
            Toast.makeText(MainActivity.this,
                    "bmFrame == null!",
                    Toast.LENGTH_LONG).show();
        }else {


            try {
                String filename = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bmFrame.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                bmFrame.recycle();

                Intent in1 = new Intent(this, FrameActivity.class);
                in1.putExtra("image", filename);
                startActivity(in1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }



}