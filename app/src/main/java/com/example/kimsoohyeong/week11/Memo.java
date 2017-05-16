package com.example.kimsoohyeong.week11;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by KimSooHyeong on 2017. 5. 16..
 */

public class Memo {
    private String name = "";
    private String memo = "";

    public Memo(String name, String memo) {
        this.name = name;
        this.memo = memo;
    }

    public Memo(File file) {
        try {
            name = file.getName();
            BufferedReader br = new BufferedReader(
                    new FileReader(file));
            String str = null;
            while ((str = br.readLine()) != null)
                memo += str + "\n";
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }
}
