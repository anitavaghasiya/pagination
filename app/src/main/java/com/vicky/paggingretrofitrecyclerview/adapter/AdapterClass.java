package com.vicky.paggingretrofitrecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vicky.paggingretrofitrecyclerview.R;
import com.vicky.paggingretrofitrecyclerview.model.ModelClass;

import java.util.ArrayList;
import java.util.List;

public class AdapterClass extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<ModelClass> modelClasses;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private AdapterCallback mCallback;

    public AdapterClass(Context context, AdapterCallback mCallback) {
        modelClasses = new ArrayList<>();
        this.context = context;
        this.mCallback = mCallback;
    }

    private String errorMsg;

    public List<ModelClass> getModelClasses() {
        return modelClasses;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
            View viewItem = inflater.inflate(R.layout.item_list, parent, false);
            viewHolder = new ModelView(viewItem);
            break;

            case LOADING:
                View viewLoading  = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingView(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ModelClass modelClass = modelClasses.get(position);
        switch (getItemViewType(position)){
            case ITEM:
                final ModelView modelView = (ModelView) holder;
                String file_url = "http://www.jantachoupal.com/" + modelClass.getPic();
                modelView.txtName.setText(modelClass.getName());

                Picasso.get()
                        .load(file_url)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(modelView.imageview);
                break;

            case LOADING:
                LoadingView loadingView = (LoadingView) holder;
                if (retryPageLoad){
                    loadingView.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingView.mProgressbar.setVisibility(View.GONE);

                    loadingView.mErrorTxt.setText(
                            errorMsg != null ? errorMsg: "An unexpected error occured"
                    );
                }else {
                    loadingView.mErrorLayout.setVisibility(View.GONE);
                    loadingView.mProgressbar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return modelClasses == null ? 0 : modelClasses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == modelClasses.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    //Helper for bind view

    public void add(ModelClass m){
        modelClasses.add(m);
        notifyItemInserted(modelClasses.size() - 1);
    }

    public void addAll(List<ModelClass> modelClasses1){
        for (ModelClass result : modelClasses1){
            add(result);
        }
    }

    public void addLoadingFooter(){
        isLoadingAdded = true;
        add(new ModelClass());
    }

    public void removeLoadingFooter(){
        isLoadingAdded = false;
        int position = modelClasses.size() - 1;
        ModelClass result = getItem(position);

        if (result != null){
            modelClasses.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ModelClass getItem(int position){
        return  modelClasses.get(position);
    }

    private class ModelView extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView imageview;
        public ModelView(View viewItem) {
            super(viewItem);

            txtName = viewItem.findViewById(R.id.name);
            imageview = viewItem.findViewById(R.id.avatar);
        }
    }

    private class LoadingView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressbar;
        private ImageButton mRetryButton;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingView(View viewLoading) {
            super(viewLoading);

            mProgressbar = viewLoading.findViewById(R.id.loadmore_progress);
            mRetryButton = viewLoading.findViewById(R.id.loadmore_retry);
            mErrorTxt = viewLoading.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = viewLoading.findViewById(R.id.loadmore_errorlayout);

            mRetryButton.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.loadmore_retry:
                    break;
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryLoaded();
                    break;
            }
        }
    }

    private void showRetry(boolean show, @Nullable String  errorMsg) {

        retryPageLoad = show;
        notifyItemChanged(modelClasses.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }
}
