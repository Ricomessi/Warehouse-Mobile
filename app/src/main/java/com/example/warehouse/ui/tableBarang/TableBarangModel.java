package com.example.warehouse.ui.tableBarang;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TableBarangModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public TableBarangModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is table barang fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
