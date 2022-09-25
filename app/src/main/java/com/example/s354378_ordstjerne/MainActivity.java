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
Boolean[] list; // liste over hvilke ord som er funnet

    //Lagrer variabler når skjerm skal roteres
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putCharSequence("savedLetters", letters.getText());
        savedInstanceState.putCharSequence("savedFeedback", feedback.getText());
        savedInstanceState.putCharSequence("savedScore", score.getText());
        savedInstanceState.putCharSequence("savedHint", hintOut.getText());
        savedInstanceState.putCharSequence("discoveredWordsLagret", discoveredWords.getText());
        savedInstanceState.putSerializable("booleanList", list);
    }
    
    //De to neste metodene er hentet fra nett for å lage knappen som endrer språk
    public void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("savedLanguage", lang);
        editor.apply();
    }

    public void getLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("savedLanguage", "");
        setLocal(language);
    }

    private void showLanguageDialoge() {
        final String[] languages = {"English", "Norsk"};
        AlertDialog.Builder alertDialogLanguage = new AlertDialog.Builder(MainActivity.this);
        alertDialogLanguage.setSingleChoiceItems(languages, -1, (dialogInterface, item) -> {
            if (item == 0) {
                setLocal("en");
                recreate();
            } else if (item == 1) {
                setLocal("nb");
                recreate();
            }
            dialogInterface.dismiss();
        });
        AlertDialog dialog = alertDialogLanguage.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocal();
        setContentView(R.layout.activity_main);
        
        //henter tekstfelter fra activity_main.xml
        letters = findViewById(R.id.textViewLetters);
        score = findViewById(R.id.score);
        feedback =  findViewById(R.id.feedback);
        hintOut = findViewById(R.id.textViewHint);
        discoveredWords = findViewById(R.id.discoveredWords);
        
        //Henter frem variabler om det ikke er første instansiering av appen
        if(savedInstanceState != null){
            letters.setText(savedInstanceState.getCharSequence("savedLetters"));
            score.setText(savedInstanceState.getCharSequence("savedScore"));
            feedback.setText(savedInstanceState.getCharSequence("savedFeedback"));
            hintOut.setText(savedInstanceState.getCharSequence("savedHint"));
            discoveredWords.setText(savedInstanceState.getCharSequence("discoveredWordsLagret"));
            list = (Boolean[]) savedInstanceState.getSerializable("booleanList");
        }
        //oppretter ny liste om det er første gang appen startes
        else{
            list = new Boolean[36];
            Arrays.fill(list, Boolean.FALSE);
        }

        //Initialiserer knapper
        Button letter1 = findViewById(R.id.button1);
        Button letter2 = findViewById(R.id.button2);
        Button letter3 = findViewById(R.id.button3);
        Button letter4 = findViewById(R.id.button4);
        Button letter5 = findViewById(R.id.button5);
        Button letter6 = findViewById(R.id.button6);
        Button letter7 = findViewById(R.id.button7);

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

        Button regret = findViewById(R.id.buttonRegret);
        Button checkWord = findViewById(R.id.buttonSubmit);
        Button hint = findViewById(R.id.buttonHint);
        Button showAllWords = findViewById(R.id.buttonShowAllWords);
        Button btnLanguage = findViewById(R.id.language);

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

        //Viser fasit
        showAllWords.setOnClickListener((view -> {
            AlertDialog.Builder alertDialogSolution = new AlertDialog.Builder(MainActivity.this);
            alertDialogSolution.setTitle(getResources().getString(R.string.solution));

            //Henter fasit og gjør om til String[]
            String[] solution = getResources().getStringArray(R.array.ordliste);
            String toReturn = Arrays.toString(solution);

            alertDialogSolution.setMessage(toReturn);

            alertDialogSolution.setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            alertDialogSolution.show();
        }));

        //Sjekker om ordet er riktig, om det er langt nok, inneholder det T og er det funnet fra før?
        checkWord.setOnClickListener(view -> {
            String word = letters.getText().toString();
            letters.setText("");
            String[] wordlist = getResources().getStringArray(R.array.ordliste);
            boolean correct = false;
            boolean checkRedLetter = false;

            //Sjekker lengden på ordet
            if(word.toCharArray().length < 4){
                String toReturn = getResources().getString(R.string.tooShortWord);
                feedback.setText(toReturn);
                return;
            }

            //Sjekker om midterste bokstaven er brukt
            for(char c : word.toCharArray()){ //Sjekker om T er med
                if (c == getResources().getString(R.string.button4).charAt(0)) {
                    checkRedLetter = true;
                    break;
                }
            }

            //Gir feedback om T ikke er med
            if(!checkRedLetter){
                String toReturn = getResources().getString(R.string.missingRedLetter);
                feedback.setText(toReturn);
                return;
            }

            //Sjekker om ordet er riktig
            for(String s : wordlist){
                if (s.equals(word)){
                    correct=true;
                    list[Arrays.asList(wordlist).indexOf(word)] = true;
                    break;
                }
            }

            //Gir feedback om ordet er riktig
            if (!correct){
                String toReturn = getResources().getString(R.string.notCorrect);
                feedback.setText(toReturn);
                letters.setText("");
                return;
            }

            // Sjekker om ordet er funnet fra før
            String discovered = discoveredWords.getText().toString();
            for (String x : discovered.split(", ")){ //Sjekker om ordet er funnet fra før
                if (x.equals(word)){
                    String toReturn = getResources().getString(R.string.alreadyFound);
                    feedback.setText(toReturn);
                    return;
                }
            }

            //Legger ordet til i oversikten over ord som er funnet
            if(!discovered.equals("")) discoveredWords.append(", "+word);
            else discoveredWords.append(word);

            //Oppdaterer score
            String scoreString = score.getText().toString();
            int oldScore = Integer.parseInt(scoreString.split("/")[0]);
            String newScore = (oldScore+1)+"/"+wordlist.length;
            score.setText(newScore);

            //Gir feedback
            if(oldScore == 35) feedback.setText(getResources().getString(R.string.win));
            else feedback.setText(getResources().getString(R.string.correct));
        });

        //Velger et tilfeldig ord og skjuler to bokstaver fra ordet
        hint.setOnClickListener(view -> {

            //Velger tilfeldig ord
            String[] wordlist = getResources().getStringArray(R.array.ordliste);

            //Avslutter om alle ord er funnet
            if(!Arrays.asList(list).contains(false)) return;

            //Løkke som finner en tilfeldig plass i listen som har verdien false
            int randomIndex = (int) (Math.random() * (wordlist.length-1));
            while(list[randomIndex]){
                randomIndex = (int) (Math.random() * (wordlist.length-1));
            }

            //Trekker tilfeldig sted i ordet som skal skjules
            char[] word = wordlist[randomIndex].toCharArray();
            int randomPosition = (int) (Math.random() * (word.length - 1));

            //Skjuler 2 bokstaver
            word[randomPosition] = '*';
            word[randomPosition+1] = '*';

            //Viser hintet på skjermen
            String hintToReturn = String.valueOf(word);
            hintOut.setText(hintToReturn);
        });
    }

    //Felles metode for alle bokstav-knappene
    @Override
    public void onClick(View v) {
        feedback.setText("");
        String letter = (String)v.getTag();
        appendLetter(letter.charAt(0));
    }

    //Metode som legger 1 bokstav inn i textView'et som holder bokstavene
    public void appendLetter(char letterToAppend){
        //Brukes for å gi T rød farge
        if(letterToAppend == getResources().getString(R.string.button4).charAt(0)){
            letters.append(Html.fromHtml("<font color=#D2042D>" + getResources().getString(R.string.button4).charAt(0) + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }
        //Alle andre bokstaver
        else letters.append(String.valueOf(letterToAppend)); //Alle andre bokstaver
    }
}