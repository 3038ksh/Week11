package com.example.kimsoohyeong.week11;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MemoActivity extends AppCompatActivity {
    LinearLayout linear1, linear2;
    ListView listView;
    ArrayList<Memo> data;
    ArrayAdapter adapter;
    Button btnSave;
    EditText et1;
    TextView tvCount;
    DatePicker dp1;
    boolean isModify = false;
    int currentFilePos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        init();
    }

    private void init() {
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        btnSave = (Button) findViewById(R.id.btnsave);
        et1 = (EditText) findViewById(R.id.et1);
        dp1 = (DatePicker) findViewById(R.id.date);
        tvCount = (TextView) findViewById(R.id.tvCount);

        makeDir();
        initListView();
        checkPermission();
        dataSortAsc();
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listview);
        data = new ArrayList<>();

        loadData();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileModify(position, true);
                currentFilePos = position;
                Log.d("PRINT", "currentFilePos: " + currentFilePos);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("삭제");
                dlg.setMessage("삭제하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 파일 삭제
                        fileRemove(position);
                        dataSortAsc();
                    }
                });
                dlg.setNegativeButton("아니오", null);
                dlg.show();
                return true;
            }
        });

        listView.setAdapter(adapter);
    }

    private void fileRemove(int position) {
        // file을 실제 삭제
        String showPath = getExternalPath();

        File[] files = new File(showPath + "diary").listFiles();

        for (File f : files) {
            if (f.getName().equals(data.get(position).getName().toString())) {
                f.delete();
                // data에서 삭제
                data.remove(position);
                return;
            }
        }
    }

    private void fileModify(int position, boolean isFromData) {
        isModify = true;
        showScreen2();
        setDateFormat(position);
        Log.d("PRINT", "LOAD DATA: " + data.get(position).getMemo());
        if (isFromData)
            et1.setText(data.get(position).getMemo());
    }

    private void makeDir() {
        String path = getExternalPath();

        File file = new File(path + "diary");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void loadData() {
        // File을 불러오기
        String showPath = getExternalPath();

        File[] files = new File(showPath + "diary").listFiles();
        for (File f : files) {
            data.add(new Memo(f));
        }
    }

    private int getPosFromName(String name) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void onClick(View v) {
        Log.d("PRINT", "currentFilePos: " + currentFilePos);
        if (v.getId() == R.id.btn1) {
            // ListView -> 일기 작성 페이지
            showScreen2();
        } else {
            // 일기 작성 페이지
            // 저장 / 수정 페이지
            if (v.getId() == R.id.btnsave) {
                // 저장 버튼 눌렀을 때
                try {
                    if (isExist(getDateFormat() + ".memo")) {
                        // 이미 있던걸 클릭했는데 이미 있음
                        if (currentFilePos > -1)
                            fileRemove(currentFilePos);
                        int findFilePos = getPosFromName(getDateFormat() + ".memo");
                        if (findFilePos > -1) {
                            fileModify(findFilePos, false);
                            fileRemove(findFilePos);
                        }
                        currentFilePos = -1;
                        isModify = false;
                        return;
                    }
                    // 수정모드
                    if (isModify)
                        fileRemove(currentFilePos);
                    BufferedWriter bw = new BufferedWriter(
                            new FileWriter(getExternalPath() + "diary/" +
                                    getDateFormat() + ".memo", false));
                    bw.write(et1.getText().toString());
                    bw.close();
                    Memo newMemo = new Memo(getDateFormat() + ".memo",
                            et1.getText().toString());
                    data.add(newMemo);
                    dataSortAsc();
                    // TODO : 시발 왜 수정안됨
                    Log.d("PRINT", "ADD-NAME: " + newMemo.getName());
                    Log.d("PRINT", "ADD-MEMO: " + newMemo.getMemo());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 취소 버튼 눌렀을 때
                et1.setText("");
            }
            isModify = false;
            showScreen1();
        }
        currentFilePos = -1;
    }

    private boolean isExist(String name) {
        String showPath = getExternalPath();
        Log.d("PRINT", "ORIGINAL NAME : " + name);
        File[] files = new File(showPath + "diary").listFiles();
        for (File f : files) {
            Log.d("PRINT", "FILE NAME : " + f.getName());
            if (f.getName().equals(name))
                return true;
        }
        return false;
    }

    private String getDateFormat() {
        // DatePicker에서 날짜 가져오기
        String ret = "";
        ret += dp1.getYear() - 2000 + "-" +
                (dp1.getMonth() < 9 ? "0" + (dp1.getMonth() + 1) : (dp1.getMonth() + 1)) + "-" +
                (dp1.getDayOfMonth() < 10 ? "0" + dp1.getDayOfMonth() : dp1.getDayOfMonth());
        return ret;
    }

    private void setDateFormat(int pos) {
        String date = data.get(pos).getName();
        String part[] = date.split("-");

        dp1.updateDate(Integer.parseInt(part[0]) + 2000,
                Integer.parseInt(part[1]) - 1,
                Integer.parseInt(part[2].substring(0, 2)));
    }

    private void showScreen1() {
        // 리스트 뷰 페이지로 가기
        linear2.setVisibility(View.GONE);
        linear1.setVisibility(View.VISIBLE);
    }

    private void showScreen2() {
        // 등록 페이지로 가기
        linear1.setVisibility(View.GONE);
        linear2.setVisibility(View.VISIBLE);

        if (isModify) {
            btnSave.setText("수정");
        } else {
            btnSave.setText("저장");
        }
    }

    private void checkPermission() {
        //
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(),
                        "권한을 허용 안하면 저장을 못하거등요 ㅠㅠ 제발해주셈요 ㅠㅠ", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getExternalPath() {
        // 외부 저장소 경로 return
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else
            sdPath = getFilesDir() + "";
        return sdPath;
    }

    private void dataSortAsc() {
        Collections.sort(data, comAsc);
        tvCount.setText("등록된 메모 개수: " + data.size());
        adapter.notifyDataSetChanged();
    }

    Comparator<Memo> comAsc = new Comparator<Memo>() {
        @Override
        public int compare(Memo o1, Memo o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
}
