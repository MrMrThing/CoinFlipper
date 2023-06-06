package com.example.coinflipper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    private Button flipperButton;

    private TextView resultText, wonOrLostText, nameText, balanceText, gamePotText;
    private Switch headsOrTailsSwitch;
    private Spinner betAmountSpinner;
    public JSONObject player = new JSONObject();
    public JSONObject game = new JSONObject();
    public JSONObject gamePot = new JSONObject();
    String playerName = "";
    String potBalance = "";
    String playerBalance = "";

    Boolean headsOrTails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flipperButton = findViewById(R.id.FlipperButton);
        flipperButton.setOnClickListener(this);

        headsOrTailsSwitch = (Switch) findViewById(R.id.switch1);
        headsOrTailsSwitch.setOnClickListener(this);

        resultText = findViewById(R.id.ResultView);
        nameText = findViewById(R.id.textView);
        wonOrLostText = findViewById(R.id.WonOrLostView);
        balanceText = findViewById(R.id.balanceView);
        gamePotText = findViewById(R.id.GamePotText);


        betAmountSpinner = findViewById(R.id.BetAmount);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.betAmounts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        betAmountSpinner.setAdapter(adapter);

        Thread coinFlipper = new Thread (new CoinFlipper());
        coinFlipper.start();
    }

    @Override
    public void onClick(View view) {

        if(view == headsOrTailsSwitch){
            headsOrTails = !headsOrTails;
            System.out.println(headsOrTails);
        }

        if(view == flipperButton){
            Thread coinFlipper = new Thread (new CoinFlipper());
            coinFlipper.start();
        }



    }

    class CoinFlipper implements Runnable {

        Random rand = new Random();
        String result = "";

        public void run(){

            try{

                int resultNum = rand.nextInt(2);

                if(resultNum == 0){
                    result = "Heads";
                } else {result = "Tails";}

                setResultText(result);



                player = GetPlayer("George");
                game = GetGame("4");
                gamePot = GetMoneyPot("4");

                playerName = player.getString("playerName");
                playerBalance = player.getString("balance");
                potBalance = gamePot.getString("potAmount");

                setNameText("Hello " + playerName + ", You have: ");
                setBalanceText(playerBalance);
                setgamePotText(potBalance);

                //JSONObject newBet = MakeBet(5);
                UpdatePlayerBalance(200);

                if(result == "Heads" && headsOrTails == false){
                    System.out.println(result + " " + headsOrTails);
                    setWonOrLostText("You won");
                }else if(result == "Tails" && headsOrTails == true) {
                    System.out.println(result + " " + headsOrTails);
                    setWonOrLostText("You won");
                }else{
                    setWonOrLostText("You lost");
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public JSONObject GetPlayer (String playername){

            String response = "";

            try {
                URL url = new URL ("http://10.0.2.2:5001/casino/player/get/" + playername);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode: " + responseCode);

                if(responseCode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responseCode);

                } else{
                    StringBuilder responseString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()){
                        responseString.append(scanner.nextLine());
                    }

                    scanner.close();
                    System.out.println(responseString);
                    response = responseString.toString();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                JSONObject JSONresonse = new JSONObject(response);
                return JSONresonse;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public JSONObject UpdatePlayerBalance (double amount){
            String response = "";

            JSONObject header = new JSONObject();

            try {
                header.put("amount", amount);
                System.out.println("Here is the Header " + header.toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                URL url = new URL ("http://10.0.2.2:5001/casino/player/update/" + playerName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept", "application/header");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode: " + responseCode);

                if(responseCode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responseCode);

                } else{
                    StringBuilder responseString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()){
                        responseString.append(scanner.nextLine());
                    }

                    scanner.close();
                    System.out.println(responseString);
                    response = responseString.toString();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                JSONObject JSONresonse = new JSONObject(response);

                return JSONresonse;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public JSONObject MakeBet (double amount){

            String response = "";

            JSONObject header = new JSONObject();

            try {
                header.put("playername", playerName);
                header.put("amount", amount);
                System.out.println("Here is the Header " + header.toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                URL url = new URL ("http://10.0.2.2:5001/casino/bet/create/4");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept", "application/header");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode: " + responseCode);

                if(responseCode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responseCode);

                } else{
                    StringBuilder responseString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()){
                        responseString.append(scanner.nextLine());
                    }

                    scanner.close();
                    System.out.println(responseString);
                    response = responseString.toString();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                JSONObject JSONresonse = new JSONObject(response);
                return JSONresonse;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public JSONObject GetGame(String GID){

            String response = "";

            try {
                URL url = new URL ("http://10.0.2.2:5001/casino/game/get/" + GID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode: " + responseCode);

                if(responseCode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responseCode);

                } else{
                    StringBuilder responseString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()){
                        responseString.append(scanner.nextLine());
                    }

                    scanner.close();
                    System.out.println(responseString);
                    response = responseString.toString();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                JSONObject JSONresonse = new JSONObject(response);
                return JSONresonse;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public JSONObject GetMoneyPot (String GID){
            String response = "";

            try {
                URL url = new URL ("http://10.0.2.2:5001/casino/gamepot/get/" + GID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                System.out.println("responseCode: " + responseCode);

                if(responseCode != 200){
                    throw new RuntimeException("HttpResponseCode: " + responseCode);

                } else{
                    StringBuilder responseString = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while (scanner.hasNext()){
                        responseString.append(scanner.nextLine());
                    }

                    scanner.close();
                    System.out.println(responseString);
                    response = responseString.toString();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            try {

                JSONObject JSONresonse = new JSONObject(response);
                return JSONresonse;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

    }


    public void setResultText(String text) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                resultText.setText(text);
            }
        });

    }

    public void setNameText(String text){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                nameText.setText(text);
            }
        });
    }

    public void setBalanceText(String text){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                balanceText.setText(text);
            }
        });
    }

    public void setgamePotText(String text){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                gamePotText.setText(text);
            }
        });
    }

    public void setWonOrLostText(String text){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                wonOrLostText.setText(text);
            }
        });
    }
}

