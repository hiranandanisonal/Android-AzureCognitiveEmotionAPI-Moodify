package com.example.a4.letsbrowse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.rapidapi.rapidconnect.Argument;
import com.rapidapi.rapidconnect.RapidApiConnect;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ///********************************************Spotify***************
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "4463bc082f7f4de0a712e4d8ea63fa56";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "mooDify://callback";


    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI);
    AuthenticationRequest request = builder.build();
    //******************************************************************

    CuboidButton btn1;
    ImageView img1;
    CuboidButton btn2;
    CuboidButton btn3;
    public EmotionServiceClient emotionServiceClient=new EmotionServiceRestClient("7edf1a5fa4fd42f8acee8b41d73989f4");
    Bitmap bitmap;
    ByteArrayInputStream inputStream;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.happy2);
        tv=(TextView)findViewById(R.id.textView);


//        btn1=(Button)findViewById(R.id.btn1);
//        btn2=(Button)findViewById(R.id.btn2);
//
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                browse();
//            }
//
//            private void browse() {
//                Intent intent=new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://www.google.com"));
//                startActivity(intent);
//            }
//        });
//
//
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                call();
//            }
//
//            private void call() {
//                Intent intent=new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:456789678"));
//                startActivity(intent);
//            }
//        });


        //--------------------------------------------code for moodify------------------------------
        btn1=(CuboidButton) findViewById(R.id.btn1);
        img1=(ImageView)findViewById(R.id.img);
        btn2=(CuboidButton) findViewById(R.id.btn2);
        btn3=(CuboidButton)findViewById(R.id.btn3);
//        final Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.surprise);
        img1.setImageBitmap(bitmap);

        //-----------conversion of image to stream



        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                call();
            }







        });








        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                inputStream=new ByteArrayInputStream(outputStream.toByteArray());
                AsyncTask<InputStream,String,List<RecognizeResult>> emotionTask = new AsyncTask<InputStream, String, List<RecognizeResult>>() {


                    ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);



                    @Override
                    protected List<RecognizeResult> doInBackground(InputStream... inputStreams) {


                        try {
                            publishProgress("Recognising");
//                            Toast.makeText(getApplicationContext(),String.valueOf(RecognizeResult),Toast.LENGTH_LONG).show();
                            List<RecognizeResult> results=emotionServiceClient.recognizeImage(inputStreams[0]);
                            return results;
                        } catch (EmotionServiceException e) {
                            e.printStackTrace();
                            return null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }

                    @Override
                    protected void onPreExecute() {

                        progressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {

                        progressDialog.setMessage(values[0]);
                    }

                    @Override
                    protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                        progressDialog.dismiss();
                        for (RecognizeResult res:recognizeResults)
                        {
                                            String status=getEmo(res);
                                            img1.setImageBitmap(imageHelper.drawRectOnBitmap(bitmap,res.faceRectangle,status));
                            //**************************************Spotify code goes here***********************



                            RapidApiConnect connect = new RapidApiConnect("default-application_5a117c41e4b0218e3e35c86b", "196cbec1-a4f8-408c-82e3-5006f6c0ed93");

                            Map<String, Argument> body = new HashMap<String, Argument>();

//        body.put("accessToken", new Argument("data", ""));
                            body.put("query", new Argument("data", status));


                            try {
                                Map<String, Object> response = connect.call("SpotifyPublicAPI", "searchPlaylists", body);
                                if(response.get("success") != null) {
                                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();

                                } else{
                                    Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception e){

                                Toast.makeText(MainActivity.this, "EEEEEEEEEEE", Toast.LENGTH_SHORT).show();
                            }

                            //***********************************************************************
                                            tv.setText(status);
                        }


                    }
                };

                emotionTask.execute(inputStream);



            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Share.class);
                startActivity(intent);
            }
        });





    }

    private String getEmo(RecognizeResult res) {

        List<Double> list=new ArrayList<>();
        Scores scores=res.scores;

        list.add(scores.anger);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.fear);
        list.add(scores.happiness);
        list.add(scores.neutral);
        list.add(scores.surprise);
        list.add(scores.sadness);


        Collections.sort(list);

        double maxNum=list.get(list.size() - 1);


        if(maxNum==scores.anger)
            return "Anger";


        else if (maxNum==scores.contempt)
            return  "Contempt";

        else if (maxNum==scores.disgust)
            return  "Disgust";

        else if (maxNum==scores.fear)
            return  "Fear";

        else if (maxNum==scores.happiness)
            return  "happiness";

        else if (maxNum==scores.surprise)
            return  "Surprise";

        else if (maxNum==scores.sadness)
            return  "Sadness";

        else
            return  "Neutral";





    }

    public void call() {
        //If permission isn't granted
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)+ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            String[] permission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(MainActivity.this,permission,999);

        }
        //If permission is granted
        else
        {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            startActivityForResult(intent,999);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 999:
                if(grantResults.length>0 && grantResults[0]+grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();
                    call();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Permission not granted",Toast.LENGTH_SHORT).show();

                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Did the user choose OK?  If so, the code inside these curly braces will execute.
        if (resultCode == RESULT_OK) {
            if (requestCode == 999) {
                // we are hearing back from the camera.
                bitmap = (Bitmap) data.getExtras().get("data");
                // at this point, we have the image from the camera.
                img1.setImageBitmap(bitmap);
            }

}}}
