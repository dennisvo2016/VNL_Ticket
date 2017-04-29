package com.example.hp.vnlt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by HP on 4/29/2017.
 */

public class Screen1 extends Fragment {

    private String[] itemSpinner_pro;
    List<String> itemSpinner_date = new ArrayList<String>();
    private Button btn_see;
    private Spinner spn_pro, spn_date;
    private TextView txt_scr1_g1, txt_scr1_g2,txt_scr1_g3,txt_scr1_g4,txt_scr1_g5,txt_scr1_g6,txt_scr1_g7,txt_scr1_g8,txt_scr1_gdb;
    Calendar cal;
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
//        btn_date1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDatePickerDialog();
//            }
//        });
//        cal = Calendar.getInstance();
//        SimpleDateFormat dft = null;
//        //Định dạng ngày / tháng /năm
//        dft=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        String strDate=dft.format(cal.getTime());
//        //hiển thị lên giao diện
////         txt_date1.setText(strDate);

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

        this.itemSpinner_pro = new String[]{"An Giang","Bình Dương", "Bạc Liêu", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Hồ Chí Minh"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item,itemSpinner_pro);
        spn_pro.setAdapter(adapter);
    }

    public void processValue(String response){
        try{
            ArrayList<String> provinces = null, realdate = null;
            String provname = spn_pro.getSelectedItem().toString(), dates = "";
            JSONObject obj1 = new JSONObject(response);
            //tim danh sach tinh
            provinces = getItem(obj1);
            //tim danh sach ngay trong tinh
            dates = getDate(provname);
            final JSONObject objdate = obj1.getJSONObject(dates);
            realdate = getItem(objdate);
            //cho date vao spinner
            for (int i = 0; i < realdate.size(); i++){
                Log.d("date",realdate.get(i));
                itemSpinner_date.add(realdate.get(i));
            }
            ArrayAdapter<String> dateadap = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemSpinner_date);
            spn_date.setAdapter(dateadap);
            //tac vu click cua button see
            this.btn_see.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String choosedate = spn_date.getSelectedItem().toString();
                    ArrayList<String> pricelist = getPrices(choosedate, objdate);
                    txt_scr1_g1.setText(pricelist.get(0).toString());
                    txt_scr1_g2.setText(pricelist.get(1).toString());
                    txt_scr1_g3.setText(pricelist.get(2).toString());
                    txt_scr1_g4.setText(pricelist.get(3).toString());
                    txt_scr1_g5.setText(pricelist.get(4).toString());
                    txt_scr1_g6.setText(pricelist.get(5).toString());
                    txt_scr1_g7.setText(pricelist.get(6).toString());
                    txt_scr1_g8.setText(pricelist.get(7).toString());
                    txt_scr1_gdb.setText(pricelist.get(8).toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getPrices(String date, JSONObject datejson){
        try{
            JSONObject objprice = datejson.getJSONObject(date);
            ArrayList<String> pricelist = new ArrayList<String>();
            for (int i = 0; i < objprice.length(); i++){
                JSONArray pricearr = objprice.getJSONArray(objprice.names().get(i).toString());
                for (int j = 0; j < pricearr.length(); j++){
                    pricelist.add(pricearr.get(j).toString());
                }
            }
            return pricelist;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //return date[]
    public String getDate(String provname){
        String codeprov = "";
        //convert provname to provlist-item
        switch (provname){
            case "An Giang":
                codeprov = "AG";
                break;
            case "Bình Dương":
                codeprov = "BD";
                break;
            case "Bạc Liêu":
                codeprov = "BL";
                break;
            case "Bình Phước":
                codeprov = "BP";
                break;
            case "Bình Thuận":
                codeprov = "BTH";
                break;
            case "Cà Mau":
                codeprov = "CM";
                break;
            case "Cần Thơ":
                codeprov = "CL";
                break;
            case "Hồ Chí Minh":
                codeprov = "HCM";
                break;
            default:
                break;
        }
        return codeprov;
    }



    public ArrayList<String> getItem(JSONObject object){
        try {
            ArrayList<String> result = new ArrayList<String>();
            Integer size = object.length();
            for (int i = 0; i < size; i++) {
                result.add(object.names().get(i).toString());
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

//    public void showDatePickerDialog()
//    {
//        final java.util.Calendar cal = java.util.Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year,
//                                  int monthOfYear,
//                                  int dayOfMonth) {
//                txt_date1.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
//                cal.set(year, monthOfYear, dayOfMonth);
//            }
//        };
//        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
//        //sẽ giống với trên TextView khi mở nó lên
//        String s=txt_date1.getText()+"";
//        String strArrtmp[]=s.split("/");
//        int ngay=Integer.parseInt(strArrtmp[0]);
//        int thang=Integer.parseInt(strArrtmp[1])-1;
//        int nam=Integer.parseInt(strArrtmp[2]);
//        DatePickerDialog pic=new DatePickerDialog(
//                super.getActivity(),
//                callback, nam, thang, ngay);
//        pic.setTitle("Chọn ngày hoàn thành");
//        pic.show();
//    }

    private void addControlls(View view){
        btn_see = (Button) view.findViewById(R.id.btn_scr1_see);
        spn_pro = (Spinner) view.findViewById(R.id.spn_scr1_province);
        spn_date = (Spinner) view.findViewById(R.id.spn_scr1_date);
        txt_scr1_g1 = (TextView) view.findViewById(R.id.txt_scr1_g1);
        txt_scr1_g2 = (TextView) view.findViewById(R.id.txt_scr1_g2);
        txt_scr1_g3 = (TextView) view.findViewById(R.id.txt_scr1_g3);
        txt_scr1_g4 = (TextView) view.findViewById(R.id.txt_scr1_g4);
        txt_scr1_g5 = (TextView) view.findViewById(R.id.txt_scr1_g5);
        txt_scr1_g6 = (TextView) view.findViewById(R.id.txt_scr1_g6);
        txt_scr1_g7 = (TextView) view.findViewById(R.id.txt_scr1_g7);
        txt_scr1_g8 = (TextView) view.findViewById(R.id.txt_scr1_g8);
        txt_scr1_gdb = (TextView) view.findViewById(R.id.txt_scr1_gdb);
    }
}
