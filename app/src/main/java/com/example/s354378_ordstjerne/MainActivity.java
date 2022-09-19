package com.example.s354378_ordstjerne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
TextView letters;
TextView score;
TextView feedback;
TextView hintOut;
TextView discoveredWords;

    //Lagrer variabler når skjerm skal roteres
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putCharSequence("savedLetters", letters.getText());
        savedInstanceState.putCharSequence("savedFeedback", feedback.getText());
        savedInstanceState.putCharSequence("savedScore", score.getText());
        savedInstanceState.putCharSequence("savedHint", hintOut.getText());
        savedInstanceState.putCharSequence("discoveredWordsLagret", discoveredWords.getText());
    }
    
    //De fire neste metodene er hentet fra nett for å midlertidig lage knappen som endrer språk
    @Override
    public void onResume() {
        super.onResume();
        Locale locale = Locale.ENGLISH;
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("My_Language", lang);
        editor.apply();
    }

    public void getLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Language", "");
        setLocal(language);
    }

    private void showLanguageDialoge() {
        final String[] listItems = {"English", "Norsk"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if (i == 0) {
                setLocal("en");
                recreate();
            } else if (i == 1) {
                setLocal("nb");
                recreate();
            }
            dialogInterface.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Everything related to language functionality
        getLocal();
        setContentView(R.layout.activity_main);
        
        //henter tekstfelter fra activity_main.xml
        letters = (TextView)findViewById(R.id.textViewLetters);
        score = (TextView)findViewById(R.id.score);
        feedback = (TextView) findViewById(R.id.feedback);
        hintOut = (TextView)findViewById(R.id.textViewHint);
        discoveredWords = (TextView)findViewById(R.id.discoveredWords);
        
        //Henter frem variabler om det ikke er første instansiering av appen
        if(savedInstanceState != null){
            letters.setText(savedInstanceState.getCharSequence("savedLetters"));
            score.setText(savedInstanceState.getCharSequence("savedScore"));
            feedback.setText(savedInstanceState.getCharSequence("savedFeedback"));
            hintOut.setText(savedInstanceState.getCharSequence("savedHint"));
            discoveredWords.setText(savedInstanceState.getCharSequence("discoveredWordsLagret"));
        }

        //Initializing buttons
        Button letter1 = (Button)findViewById(R.id.button1);
        Button letter2 = (Button)findViewById(R.id.button2);
        Button letter3 = (Button)findViewById(R.id.button3);
        Button letter4 = (Button)findViewById(R.id.button4);
        Button letter5 = (Button)findViewById(R.id.button5);
        Button letter6 = (Button)findViewById(R.id.button6);
        Button letter7 = (Button)findViewById(R.id.button7);

        String[] letterList = {getResources().getString(R.string.button1),
                getResources().getString(R.string.button2),
                getResources().getString(R.string.button3),
                getResources().getString(R.string.button4),
                getResources().getString(R.string.button5),
                getResources().getString(R.string.button6),
                getResources().getString(R.string.button7)};
        int teller = 0;

        for (Button button : Arrays.asList(letter1, letter2, letter3, letter4, letter5, letter6, letter7)) {
            button.setOnClickListener(this);
            button.setTag(letterList[teller++]);
        }

        Button regret = (Button)findViewById(R.id.buttonRegret);
        Button checkWord = (Button)findViewById(R.id.buttonSubmit);
        Button hint = (Button)findViewById(R.id.buttonHint);
        Button showAllWords = (Button)findViewById(R.id.buttonShowAllWords);
        Button btnLanguage = (Button)findViewById(R.id.language);

        btnLanguage.setOnClickListener(v -> showLanguageDialoge());


        //Fjerner bakerste bokstav om den finnes
        regret.setOnClickListener(view -> {

            char[] currentLetters = letters.getText().toString().toCharArray();
            if(currentLetters.length != 0){
                letters.setText("");

                //Bygger output
                for (int i = 0; i<currentLetters.length -1; i++){
                    appendLetter(currentLetters[i]);
                }
            }

        });

        //Viser alle ord som er riktig
        showAllWords.setOnClickListener((view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Fasit");

            //Henter ordliste og gjør om til String
            String[] wordlist = getResources().getStringArray(R.array.ordliste);
            String toReturn = Arrays.toString(wordlist);

            builder.setMessage(toReturn);


            builder.setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.show();
        }));

        //Sjekker om ordet er riktig, om det er langt nok, inneholder det E og er det funnet fra før?
        checkWord.setOnClickListener(view -> {
            String word = letters.getText().toString();
            letters.setText("");
            String[] wordlist = getResources().getStringArray(R.array.ordliste);
            int wordCount = wordlist.length;
            boolean correct = false;
            boolean checkRedLetter = false;

            //Sjekker lengden på ordet
            if(word.toCharArray().length < 3){
                String toReturn = "Ordet er for kort";
                feedback.setText(toReturn);
                return;
            }

            //Sjekker om midterste bokstaven er brukt
            for(char c : word.toCharArray()){ //Sjekker om E er med
                if (c == 'T') {
                    checkRedLetter = true;
                    break;
                }
            }

            //Gir feedback om E ikke er med
            if(!checkRedLetter){
                String toReturn = "Ordet må inneholde E!";
                feedback.setText(toReturn);
                return;
            }

            //Sjekker om ordet er riktig
            for(String s : wordlist){
                if (s.equals(word)){
                    correct=true;
                    break;
                }
            }

            //Hvis ordet er feil
            if (!correct){
                String toReturn = "Ordet er feil";
                feedback.setText(toReturn);
                letters.setText("");
                return;
            }

            // Sjekker om ordet er funnet fra før
            String discovered = discoveredWords.getText().toString();
            for (String x : discovered.split(", ")){ //Sjekker om ordet er funnet fra før
                if (x.equals(word)){
                    String toReturn = "Denne er allerede funnet!";
                    feedback.setText(toReturn);
                    return;
                }
            }

            //Legger ordet til i oversikten over ord som er funnet
            if(!discovered.equals("")) discoveredWords.append(", "+word);
            else discoveredWords.append(word);

            //Oppdaterer score
            int oldScore = Character.getNumericValue(score.getText().toString().toCharArray()[0]);
            String newScore = (oldScore+1)+"/"+wordCount;
            score.setText(newScore);

            //Gir feedback
            String toReturn = "Riktig!";
            feedback.setText(toReturn);
        });

        //Velger et tilfeldig ord og skjuler to bokstaver fra ordet.
        //Vil i fremtiden kun velge fra ord som ikke er funnet, og hvor
        //mange bokstaver som skjules vil variere etter lengden på ordet
        hint.setOnClickListener(view -> {

            //Velger tilfeldig ord
            String[] wordlist = getResources().getStringArray(R.array.ordliste);
            int randomWord = (int) (Math.random() * (wordlist.length-1));

            //Trekker tilfeldig sted i ordet som skal skjules
            char[] word = wordlist[randomWord].toCharArray();
            int randomPosition = (int) (Math.random() * (word.length - 1));

            //Skjuler 2 bokstaver
            word[randomPosition] = '*';
            word[randomPosition+1] = '*';

            //Viser hintet på skjermen
            String hintToReturn = String.valueOf(word);
            hintOut.setText(hintToReturn);
        });
    }

    //Felles metode for alle knapper
    @Override
    public void onClick(View v) {
        feedback.setText("");
        String letter = (String)v.getTag();
        appendLetter(letter.charAt(0));
    }

    public void appendLetter(char letterToAppend){
        if(letterToAppend == 'T') letters.append(Html.fromHtml("<font color=#ff0000>" + "T" + "</font>", Html.FROM_HTML_MODE_LEGACY)); //Brukes for å gi T rød farge
        else letters.append(String.valueOf(letterToAppend)); //Alle andre bokstaver
    }
}