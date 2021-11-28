package com.example.timetosleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
/*
 *@author TAO Yiduo
 *@version 1.0
 * ESIGELEC-ISE-OC
 */
public class Setting extends AppCompatActivity {
    private Button confirm;
    private TextView number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        confirm = findViewById(R.id.confirmbutton);
        confirm.setOnClickListener(this::onClick);
        number = findViewById(R.id.TimeNumber);
    }

    private void onClick(View view){
        int numberInput = Integer.parseInt(number.getText().toString());
        if(numberInput > 1 && numberInput <60){
            saveDataToFile(this, number.getText().toString());
            String string = String.valueOf(numberInput*60);//the number's unit is minute, not second
            Intent intent = new Intent(Setting.this, MainActivity.class);
            intent.putExtra("Number", string);
            setResult(1, intent);
            Setting.this.finish();
        }
    }
/*
*Write the number in data.txt
 */
    public void saveDataToFile(Context context, String number){
        try {
            OutputStream stream = context.openFileOutput(
                    "data.txt",
                    Context.MODE_PRIVATE
            );
            OutputStreamWriter writer = new OutputStreamWriter(stream);

            writer.write(number);
            writer.flush();
            writer.close();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}