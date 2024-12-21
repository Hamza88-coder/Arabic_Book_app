package com.example.ham;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ham.databinding.ActivityRecyvDicBinding;

import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder> {

    private List<Meaning> meaningList;

    public MeaningAdapter(List<Meaning> meaningList) {
        this.meaningList = meaningList;
    }

    public static class MeaningViewHolder extends RecyclerView.ViewHolder {
        private final ActivityRecyvDicBinding binding;

        public MeaningViewHolder(ActivityRecyvDicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Meaning meaning) {
            binding.partOfSpeechTextview.setText(meaning.getPartOfSpeech());
            binding.definitionsTextview.setText(joinDefinitions(meaning.getDefinitions()));

            if (meaning.getSynonyms().isEmpty()) {
                binding.synonymsTitleTextview.setVisibility(View.GONE);
                binding.synonymsTextview.setVisibility(View.GONE);
            } else {
                binding.synonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setText(joinMeaningList(meaning.getSynonyms()));
            }

            if (meaning.getAntonyms().isEmpty()) {
                binding.antonymsTitleTextview.setVisibility(View.GONE);
                binding.antonymsTextview.setVisibility(View.GONE);
            } else {
                binding.antonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setText(joinMeaningList(meaning.getAntonyms()));
            }
        }

        private String joinDefinitions(List<Definition> definitions) {
            StringBuilder result = new StringBuilder();
            for (Definition definition : definitions) {
                int currentIndex = definitions.indexOf(definition);
                result.append((currentIndex + 1)).append(". ").append(definition.getDefinition()).append("\n\n");
            }
            return result.toString();
        }

        private String joinMeaningList(List<String> meaningList) {
            return String.join(", ", meaningList);
        }
    }

    public void updateNewData(List<Meaning> newMeaningList) {
        meaningList = newMeaningList;
        notifyDataSetChanged();
    }

    @Override
    public MeaningViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ActivityRecyvDicBinding binding= ActivityRecyvDicBinding.inflate(inflater, parent, false);
        return new MeaningViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MeaningViewHolder holder, int position) {
        holder.bind(meaningList.get(position));
    }

    @Override
    public int getItemCount() {
        return meaningList.size();
    }
}
