package com.example.hp.vnlt;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by HP on 4/29/2017.
 */

public class Screen1 extends Fragment {

    private String[] itemSpinner;
    private Button btn_date1, btn_see;
    private Spinner spn1;
    private TextView txt_date1;
    private TextView txt_scr1_g1, txt_scr1_g2,txt_scr1_g3,txt_scr1_g4,txt_scr1_g5,txt_scr1_g6,txt_scr1_g7,txt_scr1_g8,txt_scr1_gdb;
    Calendar cal;
    private String res="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen1,container,false);
        addControlls(view);
        addEvents();
        return view;
    }

    private void addEvents(){
        //btn_show date click event
        btn_date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        cal = Calendar.getInstance();
        SimpleDateFormat dft = null;
        //Định dạng ngày / tháng /năm
        dft=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate=dft.format(cal.getTime());
        //hiển thị lên giao diện
         txt_date1.setText(strDate);

        //Read JSON event
        try{
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Reader().execute("http://thanhhungqb.tk:8080/kqxsmn");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        this.itemSpinner = new String[]{"An Giang","Cần Thơ", "Bình Dương"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item,itemSpinner);
        spn1.setAdapter(adapter);
    }

    public void processValue(String response){
        try{
            String[] provinces = null;
            JSONObject obj1 = new JSONObject(response);
            provinces = getItem(obj1);
            for (int i = 0; i < provinces.length; i++){

            }
            JSONObject obj2 = obj1.getJSONObject(provinces[0]);
            JSONObject obj3 = obj2.getJSONObject("20-04");
            JSONArray arr1 = obj3.getJSONArray(obj3.names().get(2).toString());
            System.out.println(arr1.get(1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getItem(JSONObject object){
        try {
            String[] result = new String[100];
            Integer size = object.length();
            for (int i = 0; i < size; i++) {
                result[i] = object.names().get(i).toString();
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    class Reader extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
            String chuoi = readFromUrl(strings[0]);
            return chuoi;
        }

        @Override
        protected void onPostExecute(String s) {
            processValue(s);
        }
    }

    public String readFromUrl(String urlInput){
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlInput);

            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            while((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public void showDatePickerDialog()
    {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                txt_date1.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
                cal.set(year, monthOfYear, dayOfMonth);
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=txt_date1.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                super.getActivity(),
                callback, nam, thang, ngay);
        pic.setTitle("Chọn ngày hoàn thành");
        pic.show();
    }

    private void addControlls(View view){
        btn_date1 = (Button) view.findViewById(R.id.btn_scr1_date);
        btn_see = (Button) view.findViewById(R.id.btn_scr1_see);
        spn1 = (Spinner) view.findViewById(R.id.spn_scr1_province);
        txt_date1 = (TextView) view.findViewById(R.id.txt_scr1_date);
        txt_scr1_g1 = (TextView) view.findViewById(R.id.txt_scr1_g1);
    }
}
