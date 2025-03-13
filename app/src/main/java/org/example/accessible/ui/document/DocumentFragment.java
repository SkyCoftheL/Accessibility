package org.example.accessible.ui.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.example.accessible.R;

public class DocumentFragment extends Fragment {

    private View documentView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (documentView==null)
            documentView=inflater.inflate(R.layout.fragment_document,container,false);


        return documentView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}