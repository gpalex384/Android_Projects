package com.algapo.myquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.algapo.myquiz.databinding.FragmentThirdBinding;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;
    private CheckBox mQuintoFragmentCheckBox;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuintoFragmentCheckBox = (CheckBox) view.findViewById(R.id.quintoFragmentCheckBox);

        binding.buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQuintoFragmentCheckBox.isChecked()) {
                    NavHostFragment.findNavController(ThirdFragment.this)
                            .navigate(R.id.action_ThirdFragment_to_FifthFragment);
                } else {
                    NavHostFragment.findNavController(ThirdFragment.this)
                            .navigate(R.id.action_ThirdFragment_to_FourthFragment);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}