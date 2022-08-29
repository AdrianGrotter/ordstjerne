package com.example.s354378_ordstjerne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
TextView bokstaver;
TextView score;
TextView tilbakemelding;
TextView hintUt;
TextView funnedeOrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bokstaver = (TextView)findViewById(R.id.textViewOutput);
        score = (TextView)findViewById(R.id.score);
        tilbakemelding = (TextView) findViewById(R.id.tilbakemelding);
        hintUt = (TextView)findViewById(R.id.textViewHint);
        funnedeOrd = (TextView)findViewById(R.id.funnedeOrd);



        Button bokstav1 = (Button)findViewById(R.id.button1);
        Button bokstav2 = (Button)findViewById(R.id.button2);
        Button bokstav3 = (Button)findViewById(R.id.button3);
        Button bokstav4 = (Button)findViewById(R.id.button4);
        Button bokstav5 = (Button)findViewById(R.id.button5);
        Button bokstav6 = (Button)findViewById(R.id.button6);
        Button bokstav7 = (Button)findViewById(R.id.button7);

        Button angre = (Button)findViewById(R.id.buttonRegret);
        Button sjekk = (Button)findViewById(R.id.buttonSubmit);
        Button hint = (Button)findViewById(R.id.buttonHint);

        bokstav1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                bokstaver.append("B");
                tilbakemelding.setText("");
            }
        });

        bokstav2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                bokstaver.append("D");
                tilbakemelding.setText("");
            }
        });

        bokstav3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                bokstaver.append("I");
                tilbakemelding.setText("");
            }
        });

        bokstav4.setOnClickListener(view -> {
            String letter = "E";
            bokstaver.append(Html.fromHtml("<font color=#ff0000>" + letter + "</font>", Html.FROM_HTML_MODE_LEGACY));
            tilbakemelding.setText("");
        });

        bokstav5.setOnClickListener(view -> {
            bokstaver.append("O");
            tilbakemelding.setText("");
        });

        bokstav6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                bokstaver.append("U");
                tilbakemelding.setText("");
            }
        });

        bokstav7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                bokstaver.append("Y");
                tilbakemelding.setText("");
            }
        });

        angre.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                char[] cur = bokstaver.getText().toString().toCharArray();
                if(cur.length != 0){
                    StringBuilder newString = new StringBuilder();
                    for (int i = 0; i<cur.length -1; i++){
                        newString.append(cur[i]);
                    }
                    bokstaver.setText(newString);
                }

            }
        });

        sjekk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String word = bokstaver.getText().toString();
                String[] targets = getResources().getStringArray(R.array.ordliste);
                boolean riktig = false;
                boolean sjekkMidt = false;
                if(word.toCharArray().length < 3){
                    String tilbake = "Ordet er for kort";
                    tilbakemelding.setText(tilbake);
                    return;
                }
                for(char c : word.toCharArray()){ //Sjekker om E er med
                    if (c == 'E') {
                        sjekkMidt = true;
                        break;
                    }
                }
                if(!sjekkMidt){
                        String tilbake = "Ordet må inneholde E!";
                        tilbakemelding.setText(tilbake);
                        return;
                }
                for(String s : targets){ //Sjekker om ordet er riktig
                    if (s.equals(word)){
                        riktig=true;
                        String funnede = funnedeOrd.getText().toString();
                        for (String x : funnede.split(",")){ //Sjekker om ordet er funnet fra før
                            if (x.equals(s)){
                                System.out.println(x + " "+ s);
                                String tilbake = "Denne er allerede funnet";
                                tilbakemelding.setText(tilbake);
                                return;
                            }
                        }
                        if(!funnede.equals("")) {funnede +=", "+word;}else{funnede = word;}
                        funnedeOrd.setText(funnede);
                        break;
                    }
                }
                if (riktig){
                    char[] currentScore = score.getText().toString().toCharArray();
                    int oldScore = Character.getNumericValue(currentScore[0]);
                    if(oldScore < 4){
                        String newScore = (oldScore+1)+"/4";
                        score.setText(newScore);
                        String tilbake = "Riktig!";
                        tilbakemelding.setText(tilbake);
                    }else{
                        System.out.println("Riktig");
                    }
                }else{
                    String tilbake = "Feil svar!";
                    tilbakemelding.setText(tilbake);
                    System.out.println("Feil");
                }
                bokstaver.setText("");
            }
        });

        hint.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String[] targets = getResources().getStringArray(R.array.ordliste);
                int num = (int) (Math.random() * (targets.length-1)); //Finner tilfeldig ord
                char[] word = targets[num].toCharArray();
                int num2 = (int) (Math.random() * (word.length - 1)); //Trekker tilfeldig sted i ordet som skal byttes ut med *
                word[num2] = '*';
                word[num2+1] = '*';
                String finalHint = String.valueOf(word);
                hintUt.setText(finalHint);
            }
        });
    }
}