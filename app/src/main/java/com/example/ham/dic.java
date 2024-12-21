package com.example.ham;

import static java.util.Collections.emptyList;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ham.databinding.ActivityDicBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class dic extends AppCompatActivity {

    private ActivityDicBinding binding;
    private MeaningAdapter adapter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = binding.searchInput.getText().toString();
                getMeaning(word);
            }
        });

        adapter = new MeaningAdapter(emptyList());
        binding.meaningRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.meaningRecyclerView.setAdapter(adapter);
    }

    private void getMeaning(String word) {
        setInProgress(true);

        executorService.execute(() -> {
            try {
                Call<List<WordResult>> call = RetrofitInstance.getDictionaryApi().getMeaning(word);
                Response<List<WordResult>> response = call.execute();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    runOnUiThread(() -> setUI(response.body().get(0)));
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show());
            } finally {
                runOnUiThread(() -> setInProgress(false));
            }
        });
    }

    private void setUI(WordResult response) {
        binding.wordTextview.setText(response.getWord());
        binding.phoneticTextview.setText(response.getPhonetic());
        adapter.updateNewData(response.getMeanings());
    }

    private void setInProgress(boolean inProgress) {
        runOnUiThread(() -> {
            if (inProgress) {
                binding.searchBtn.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.searchBtn.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
