package com.algapo.act1ex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.algapo.act1ex.databinding.FragmentFirstBinding;
import com.algapo.act1ex.databinding.FragmentStartBinding;

public class StartFragment extends Fragment {

    private FragmentStartBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentStartBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:40.0, 0.0");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        binding.frStart1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartFragment.this)
                        .navigate(R.id.action_StartFragment_to_FirstFragment);
            }
        });
        binding.salirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Salir de la app
                getActivity().finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}