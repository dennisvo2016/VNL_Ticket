package com.example.hp.vnlt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

/**
 * Created by HP on 4/29/2017.
 */

public class Screen2 extends Fragment {

    private Spinner spn_pro, spn_date;
    List<String> itemSpinner_date = new ArrayList<String>();
    private EditText txt_no;
    private Button btn_search;
    private TextView txt_result;
    private JSONObject objdate;
    private String[] itemSpinner_pro;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.screen2,container,false);
        addControlls(view);
        addEvents();
        return view;
    }

    private void addEvents(){
        try{
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Screen2.Reader().execute("http://thanhhungqb.tk:8080/kqxsmn");
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
            ArrayList<String> provinces = null;
            final String provname = spn_pro.getSelectedItem().toString();
            final JSONObject obj1 = new JSONObject(response);
            //tim danh sach ngay trong tinh
            String dates = getrealPro(provname);
            objdate = obj1.getJSONObject(dates);
            final ArrayList<String> realdate = getItem(objdate);
            for (int run = 0; run < realdate.size(); run++){
                Log.d("date",realdate.get(run));
                itemSpinner_date.add(realdate.get(run));
            }
            ArrayAdapter<String> dateadap = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemSpinner_date);
            spn_date.setAdapter(dateadap);
            spn_pro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spinner_item_select(obj1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //tac vu click cua button see
            this.btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String choosedate = spn_date.getSelectedItem().toString();
                    ArrayList<String> pricelist = getPrices(choosedate, objdate);
                    String choosenumber = txt_no.getText().toString();
                    String result = getResult(pricelist,choosenumber);
                    String realresult = "";
                    switch (result){
                        case "#0":
                            realresult = "NHẤT";
                            break;
                        case "#1":
                            realresult = "NHÌ";
                            break;
                        case "#2":
                            realresult = "BA";
                            break;
                        case "#3":
                            realresult = "TƯ";
                            break;
                        case "#4":
                            realresult = "NĂM";
                            break;
                        case "#5":
                            realresult = "SÁU";
                            break;
                        case "#6":
                            realresult = "BẢY";
                            break;
                        case "#7":
                            realresult = "TÁM";
                            break;
                        case "#8":
                            realresult = "ĐẶC BIỆT";
                            break;
                        default:
                            break;
                    }
                    if (!result.equals("")){
                        txt_result.setText("Chúc mừng bạn đã trúng giải "+realresult);
                    }else{
                        txt_result.setText("Xin chúc bạn may mắn lần sau");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getResult(ArrayList<String> pricelist, String number){
        try{
            for (int i = 0; i < pricelist.size(); i++){
                if (pricelist.get(i).toString().equals(number)){
                    return "#"+i;
                }
            }
            return "";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public void spinner_item_select(JSONObject obj1){
        try{
            //cho date vao spinner
            //dau tien la xoa du lieu truoc
            itemSpinner_date.clear();
            spn_date.setAdapter(null);
            String prname = spn_pro.getSelectedItem().toString();
            String dates_2 = getrealPro(prname);
            JSONObject objdate_2 = obj1.getJSONObject(dates_2);
            objdate = objdate_2;
            ArrayList<String> realdate_2 = getItem(objdate_2);
            for (int run = 0; run < realdate_2.size(); run++){
                Log.d("date",realdate_2.get(run));
                itemSpinner_date.add(realdate_2.get(run));
            }
            ArrayAdapter<String> dateadap_2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemSpinner_date);
            spn_date.setAdapter(dateadap_2);
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

    //return codeprov
    public String getrealPro(String provname){
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
                codeprov = "CT";
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

    class Reader extends AsyncTask<String, Integer, String> {
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
    private void addControlls(View view){
        txt_no = (EditText) view.findViewById(R.id.etxt_scr2_no);
        txt_result = (TextView) view.findViewById(R.id.txt_scr2_result);
        spn_date = (Spinner) view.findViewById(R.id.spn_scr2_date);
        spn_pro = (Spinner) view.findViewById(R.id.spn_scr2_province);
        btn_search = (Button) view.findViewById(R.id.btn_scr2_search);
    }
}
