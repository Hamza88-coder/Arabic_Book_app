package com.example.ham;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    ArrayList<ModelPdf> filterList;
    AdapterPdfAdmin adapterPdfAdmin;


    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterCategory) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();

        if( constraint !=null && constraint.length()>0){
            constraint= constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                if(filterList.get(i).getTitle().toUpperCase().contains( constraint)){
                    filterModels.add(filterList.get(i));
                }
            }
            results.count= filterModels.size();
            results.values=filterModels;
        }else{
            results.count= filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterPdfAdmin.pdfArrayList=(ArrayList<ModelPdf>) filterResults.values;
        adapterPdfAdmin.notifyDataSetChanged();

    }
}

