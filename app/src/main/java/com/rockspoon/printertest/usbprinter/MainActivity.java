package com.rockspoon.printertest.usbprinter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends AppCompatActivity {

  @Bean
  PrinterManager printerManager;

  @Click(R.id.printTestBtn)
  protected void printTestBtnClick() {
    printerManager.printJob(new PrinterJob("Job0: This is a test\r\n\r\n\r\nTest\r\n"));
    printerManager.printJob(new PrinterJob("Job1: This is a test\r\n\r\n\r\nTest\r\n"));
    printerManager.printJob(new PrinterJob("Job2: This is a test\r\n\r\n\r\nTest\r\n"));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener((view) -> {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
    });
  }
}
