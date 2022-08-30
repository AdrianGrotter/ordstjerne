package com.example.s354378_ordstjerne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
TextView bokstaver;
TextView score;
TextView tilbakemelding;
TextView hintUt;
TextView funnedeOrd;
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putCharSequence("bokstaverLagret", bokstaver.getText());
        savedInstanceState.putCharSequence("tilbakemeldingLagret", tilbakemelding.getText());
        savedInstanceState.putCharSequence("scoreLagret", score.getText());
        savedInstanceState.putCharSequence("hintLagret", hintUt.getText());
        savedInstanceState.putCharSequence("funnedeOrdLagret", funnedeOrd.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bokstaver = (TextView)findViewById(R.id.textViewOutput);
        score = (TextView)findViewById(R.id.score);
        tilbakemelding = (TextView) findViewById(R.id.tilbakemelding);
        hintUt = (TextView)findViewById(R.id.textViewHint);
        funnedeOrd = (TextView)findViewById(R.id.funnedeOrd);

        if(savedInstanceState != null){
            bokstaver.setText(savedInstanceState.getCharSequence("bokstaverLagret"));
            score.setText(savedInstanceState.getCharSequence("scoreLagret"));
            tilbakemelding.setText(savedInstanceState.getCharSequence("tilbakemeldingLagret"));
            hintUt.setText(savedInstanceState.getCharSequence("hintLagret"));
            funnedeOrd.setText(savedInstanceState.getCharSequence("funnedeOrdLagret"));
        }


        Button bokstav1 = (Button)findViewById(R.id.button1);
        Button bokstav2 = (Button)findViewById(R.id.button2);
        Button bokstav3 = (Button)findViewById(R.id.button3);
        Button bokstav4 = (Button)findViewById(R.id.button4);
        Button bokstav5 = (Button)findViewById(R.id.button5);
        Button bokstav6 = (Button)findViewById(R.id.button6);
        Button bokstav7 = (Button)findViewById(R.id.button7);

        for (Button button : Arrays.asList(bokstav1, bokstav2, bokstav3, bokstav4, bokstav5, bokstav6, bokstav7)) {
            button.setOnClickListener(this);
        }

        bokstav1.setTag("B");
        bokstav2.setTag("D");
        bokstav3.setTag("I");
        bokstav4.setTag("E");
        bokstav5.setTag("O");
        bokstav6.setTag("U");
        bokstav7.setTag("Y");

        Button angre = (Button)findViewById(R.id.buttonRegret);
        Button sjekk = (Button)findViewById(R.id.buttonSubmit);
        Button hint = (Button)findViewById(R.id.buttonHint);

        angre.setOnClickListener(view -> {
            char[] cur = bokstaver.getText().toString().toCharArray();
            if(cur.length != 0){
                bokstaver.setText("");
                for (int i = 0; i<cur.length -1; i++){
                    if(cur[i] == 'E') bokstaver.append(Html.fromHtml("<font color=#ff0000>" + "E" + "</font>", Html.FROM_HTML_MODE_LEGACY));
                    else bokstaver.append(String.valueOf(cur[i]));
                }
            }

        });

        sjekk.setOnClickListener(view -> {
            String word = bokstaver.getText().toString();
            bokstaver.setText("");
            String[] targets = getResources().getStringArray(R.array.ordliste);
            boolean riktig = false;
            boolean sjekkMidt = false;

            //Sjekker lengden på ordet
            if(word.toCharArray().length < 3){
                String tilbake = "Ordet er for kort";
                tilbakemelding.setText(tilbake);
                return;
            }

            //Sjekker om midterste bokstaven er brukt
            for(char c : word.toCharArray()){ //Sjekker om E er med
                if (c == 'E') {
                    sjekkMidt = true;
                    break;
                }
            }

            //Gir tilbakemelding om E ikke er med
            if(!sjekkMidt){
                String tilbake = "Ordet må inneholde E!";
                tilbakemelding.setText(tilbake);
                return;
            }

            //Sjekker om ordet er riktig
            for(String s : targets){ //Sjekker om ordet er riktig
                if (s.equals(word)){
                    riktig=true;
                    break;
                }
            }

            //Hvis ordet er feil
            if (!riktig){
                String tilbake = "Ordet er feil";
                tilbakemelding.setText(tilbake);
                bokstaver.setText("");
                return;
            }

            // Sjekker om ordet er funnet fra før
            String funnede = funnedeOrd.getText().toString();
            for (String x : funnede.split(", ")){ //Sjekker om ordet er funnet fra før
                if (x.equals(word)){
                    String tilbake = "Denne er allerede funnet!";
                    tilbakemelding.setText(tilbake);
                    return;
                }
            }

            //Legger ordet til i oversikten over ord som er funnet
            if(!funnede.equals("")) funnedeOrd.append(", "+word);
            else funnedeOrd.append(word);

            int oldScore = Character.getNumericValue(score.getText().toString().toCharArray()[0]);
            String newScore = (oldScore+1)+"/5";
            score.setText(newScore);
            String tilbake = "Riktig!";
            tilbakemelding.setText(tilbake);
        });

        hint.setOnClickListener(view -> {
            String[] targets = getResources().getStringArray(R.array.ordliste);
            int numRandomWord = (int) (Math.random() * (targets.length-1)); //Finner tilfeldig ord
            char[] word = targets[numRandomWord].toCharArray();
            int numRandomPosition = (int) (Math.random() * (word.length - 1)); //Trekker tilfeldig sted i ordet som skal byttes ut med *
            word[numRandomPosition] = '*';
            word[numRandomPosition+1] = '*';
            String finalHint = String.valueOf(word);
            hintUt.setText(finalHint);
        });
    }

    @Override
    public void onClick(View v) {
        tilbakemelding.setText("");
        if(v.getTag() == "E") bokstaver.append(Html.fromHtml("<font color=#ff0000>" + "E" + "</font>", Html.FROM_HTML_MODE_LEGACY));
        else bokstaver.append((CharSequence) v.getTag());
    }
}