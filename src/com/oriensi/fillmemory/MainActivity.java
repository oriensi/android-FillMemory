/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oriensi.fillmemory;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {


    private TextView mTv;
    private Button mFill;
    private Button mFree;
    private long mSize;

    private final int FILL_MEM = 1;
    private final int FREE_MEM = 2;
    private final int UPDATE_SIZE = 10;

    static {
        System.loadLibrary("jni_fillmemory");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity);
        mTv = (TextView) findViewById(R.id.textView);
        mFill = (Button) findViewById(R.id.fill);
        mFree = (Button) findViewById(R.id.free);
        mFill.setOnClickListener(this);
        mFree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill:
                mHandler.sendEmptyMessage(FILL_MEM);
                break;
            case R.id.free:
                mHandler.sendEmptyMessage(FREE_MEM);
                break;
        }
    }

    private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case FILL_MEM:
                        handleFillMem();
                        break;
                    case FREE_MEM:
                        handleFreeMem();
                        break;
                    case UPDATE_SIZE:
                        mTv.setText(getString(R.string.fill_prompt) + String.valueOf(mSize) + " M");
                        break;
                }
            }
        };

    private void handleFillMem() {
        Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    mSize = nativeFillMemory();
                    mHandler.sendEmptyMessage(UPDATE_SIZE);
                }
            });
        th.start();
    }
    private void handleFreeMem() {
        Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    nativeFreeMemory();
                    mSize = 0;
                    mHandler.sendEmptyMessage(UPDATE_SIZE);
                }
            });
        th.start();
    }

    private static native long nativeFillMemory();
    private static native void nativeFreeMemory();

}
